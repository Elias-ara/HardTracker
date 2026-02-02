package com.HardTracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Entity
    @Getter
    @Setter
    public class PriceHistory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private BigDecimal price;
        private LocalDateTime date;

        @ManyToOne
        @JoinColumn(name = "product_id")
        private Product product;
    }