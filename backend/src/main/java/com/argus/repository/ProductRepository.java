package com.argus.repository;

import com.argus.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // O Spring cria o SQL automaticamente baseado no nome do método!
    Optional<Product> findByUrl(String url);
}