package com.HardTracker.controller;

import com.HardTracker.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ScraperService scraperService;

    // Endpoint para testar: POST http://localhost:8080/api/products/track
    @PostMapping("/track")
    public String trackUrl(@RequestBody String url) {
        // Limpeza simples caso venha aspas extras do JSON
        String cleanUrl = url.replace("\"", "");

        scraperService.trackProduct(cleanUrl);
        return "Scraping iniciado para: " + cleanUrl;
    }
}