package com.argus.service;

import com.argus.model.PriceHistory;
import com.argus.model.Product;
import com.argus.repository.ProductRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void trackProduct(String url) {
        try {
            // 1. Conexão robusta (User Agent moderno)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000) // 15 segundos de timeout
                    .header("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7")
                    .get();

            // 2. Extração de Dados
            String title = doc.title();
            BigDecimal realPrice = findBestPrice(doc, url);

            if (realPrice == null) {
                System.out.println("❌ FALHA: Não foi possível encontrar preço para: " + url);
                return;
            }

            // 3. Identificar Loja (Visual apenas)
            String storeName = identifyStore(url);

            // 4. Salvar no Banco
            Optional<Product> existingProduct = productRepository.findByUrl(url);
            Product product;

            if (existingProduct.isPresent()) {
                product = existingProduct.get();
                product.setLastCheck(LocalDateTime.now());
                product.setCurrentPrice(realPrice); // Atualiza preço atual
                if (!product.getName().equals(title)) product.setName(title);
            } else {
                product = new Product();
                product.setUrl(url);
                product.setName(title);
                product.setStore(storeName);
                product.setLastCheck(LocalDateTime.now());
                product.setCurrentPrice(realPrice);
            }

            product = productRepository.save(product);
            createHistory(product, realPrice);

        } catch (IOException e) {
            System.err.println("Erro de conexão com URL: " + e.getMessage());
        }
    }

    // --- O CÉREBRO DO ROBÔ ---

    private BigDecimal findBestPrice(Document doc, String url) {
        BigDecimal price;

        // TENTATIVA 1: JSON-LD (Padrão Google - O mais confiável para Pichau/Terabyte/Magalu)
        price = extractJsonLdPrice(doc);
        if (price != null) return price;

        // TENTATIVA 2: Meta Tags (OpenGraph - Usado pelo Facebook/Zap)
        price = extractMetaPrice(doc);
        if (price != null) return price;

        // TENTATIVA 3: Seletores Específicos (Lojas Conhecidas)
        price = extractSpecificStorePrice(doc, url);
        if (price != null) return price;

        // TENTATIVA 4: Genérico Visual (Procura padrões visuais de preço)
        return extractGenericVisualPrice(doc);
    }

    // 1. Busca em dados estruturados (JSON invisível na página)
    private BigDecimal extractJsonLdPrice(Document doc) {
        Elements scripts = doc.select("script[type=application/ld+json]");
        for (Element script : scripts) {
            String json = script.html();
            // Procura por "price": "123.45" ou "lowPrice": 123.45 (Regex simples para evitar bibliotecas pesadas)
            Pattern pattern = Pattern.compile("\"(price|lowPrice|highPrice)\"\\s*:\\s*[\"]?(\\d+(\\.\\d+)?)[\"]?");
            Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                return new BigDecimal(matcher.group(2));
            }
        }
        return null;
    }

    // 2. Busca em Meta Tags (<meta property="og:price:amount" content="100.00">)
    private BigDecimal extractMetaPrice(Document doc) {
        Element metaPrice = doc.select("meta[property=product:price:amount], meta[property=og:price:amount]").first();
        if (metaPrice != null) {
            return parsePrice(metaPrice.attr("content"));
        }
        return null;
    }

    // 3. Seletores CSS Específicos (Hardcoded para garantir)
    private BigDecimal extractSpecificStorePrice(Document doc, String url) {
        Element el = null;

        if (url.contains("kabum")) {
            el = doc.select("h4.finalPrice").first();
            if (el == null) el = doc.select(".finalPrice").first();
        }
        else if (url.contains("amazon")) {
            el = doc.select(".a-price .a-offscreen").first();
            if (el == null) el = doc.select(".a-price-whole").first();
        }
        else if (url.contains("terabyteshop")) {
            el = doc.select("div#valVista").first(); // Preço à vista Terabyte
            if (el == null) el = doc.select("p#valVista").first();
        }
        else if (url.contains("pichau")) {
            el = doc.select("div:contains(à vista) + div").first(); // Tenta pegar o bloco de preço
            if (el == null) el = doc.select(".price").first();
        }
        else if (url.contains("mercadolivre")) {
            el = doc.select(".andes-money-amount__fraction").first();
        }

        if (el != null) return parsePrice(el.text());
        return null;
    }

    // 4. Última esperança: Procura qualquer coisa parecida com preço
    private BigDecimal extractGenericVisualPrice(Document doc) {
        // Tenta achar elementos que tenham classe "price", "preco", "valor"
        Elements potentialPrices = doc.select("[class*='price'], [class*='preco'], [class*='valor'], [id*='price']");

        for (Element el : potentialPrices) {
            String text = el.text();
            // Regex: Procura R$ seguido de números (ex: R$ 1.200,00)
            if (text.matches(".*R\\$\\s*[0-9]{1,3}(\\.[0-9]{3})*,[0-9]{2}.*")) {
                return parsePrice(text);
            }
        }
        return null;
    }

    private BigDecimal parsePrice(String text) {
        try {
            if (text == null || text.isEmpty()) return null;
            // Limpa tudo que não for número ou vírgula (R$, espaços, pontos de milhar)
            // Mantém apenas dígitos e vírgula
            String clean = text.replaceAll("[^0-9,]", "");
            // Troca vírgula por ponto para o BigDecimal aceitar
            clean = clean.replace(",", ".");
            return new BigDecimal(clean);
        } catch (Exception e) {
            return null;
        }
    }

    private String identifyStore(String url) {
        if (url.contains("kabum")) return "Kabum";
        if (url.contains("amazon")) return "Amazon";
        if (url.contains("terabyte")) return "Terabyte";
        if (url.contains("pichau")) return "Pichau";
        if (url.contains("mercadolivre")) return "Mercado Livre";
        return "Loja Genérica";
    }

    private void createHistory(Product product, BigDecimal price) {
        PriceHistory history = new PriceHistory();
        history.setProduct(product);
        history.setPrice(price);
        history.setDate(LocalDateTime.now());
        product.getPriceHistory().add(history);
        productRepository.save(product);
        System.out.println("💰 Preço capturado: R$ " + price + " | " + product.getStore());
    }
}