package com.argus.controller;

import com.argus.model.Product;
import com.argus.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.argus.repository.ProductRepository;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ScraperService scraperService;

    @PostMapping("/track")
    public Map<String, String> trackUrl(@RequestBody String url) {
        String cleanUrl = url.replace("\"", "");

        scraperService.trackProduct(cleanUrl);

        return Collections.singletonMap("message", "Scraping iniciado para: " + cleanUrl);
    }

    @GetMapping
    public List<Product> listAll() {
        return repository.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProductConfig(@PathVariable Long id, @RequestBody Product productDetails) {
        return repository.findById(id)
                .map(product -> {
                    product.setTargetPrice(productDetails.getTargetPrice());
                    product.setNotificationEmail(productDetails.getNotificationEmail());

                    Product updated = repository.save(product);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

