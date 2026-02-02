package com.HardTracker.service;

import com.HardTracker.model.PriceHistory;
import com.HardTracker.model.Product;
import com.HardTracker.repository.ProductRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ScraperService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void trackProduct(String url) {
        try {
            // 1. Conexão com Jsoup (Simulando Firefox para não ser bloqueado)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000) // Espera até 10s
                    .get();

            // 2. Extração de Dados (Lógica Genérica para Teste)
            String title = doc.title();

            // ATENÇÃO: Em produção, você precisará de "ifs" para detectar se é Kabum, Amazon, etc.
            BigDecimal currentPrice = new BigDecimal("1500.00"); // Simulação

            // 3. Atualizar ou Criar Produto
            Optional<Product> existingProduct = productRepository.findByUrl(url);
            Product product;

            if (existingProduct.isPresent()) {
                product = existingProduct.get();
                product.setLastCheck(LocalDateTime.now());
                // Só atualiza nome se mudou
                if (!product.getName().equals(title)) product.setName(title);
            } else {
                product = new Product();
                product.setUrl(url);
                product.setName(title);
                product.setStore("Desconhecida");
                product.setLastCheck(LocalDateTime.now());
            }

            // Salva o produto (necessário para ter o ID antes de salvar o histórico)
            product = productRepository.save(product);

            // 4. Gravar Histórico de Preço
            createHistory(product, currentPrice);

        } catch (IOException e) {
            System.err.println("Erro ao acessar URL: " + url + " - " + e.getMessage());
        }
    }

    private void createHistory(Product product, BigDecimal price) {
        PriceHistory history = new PriceHistory();
        history.setProduct(product);
        history.setPrice(price);
        history.setDate(LocalDateTime.now());

        // Adiciona na lista do produto (o CascadeType.ALL no Product salva o histórico automaticamente)
        product.getPriceHistory().add(history);
        productRepository.save(product);

        System.out.println("Preço registrado: " + price + " para " + product.getName());
    }
}