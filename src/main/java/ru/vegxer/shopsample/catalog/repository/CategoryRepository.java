package ru.vegxer.shopsample.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vegxer.shopsample.catalog.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.parent is NULL ORDER BY c.id ASC")
    Page<Category> findPrimalCategories(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parent.id = :categoryId ORDER BY c.id ASC")
    Page<Category> findSubcategories(final long categoryId, Pageable pageable);
}
