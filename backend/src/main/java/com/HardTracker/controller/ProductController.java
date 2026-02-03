package com.HardTracker.controller;

import com.HardTracker.model.Product;
import com.HardTracker.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.HardTracker.repository.ProductRepository;
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
}


