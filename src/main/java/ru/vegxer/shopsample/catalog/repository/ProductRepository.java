package ru.vegxer.shopsample.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vegxer.shopsample.catalog.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
