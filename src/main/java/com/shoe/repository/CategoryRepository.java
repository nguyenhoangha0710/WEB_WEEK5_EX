package com.shoe.repository;

import com.shoe.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find categories by name containing the given string (case-insensitive)
     */
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find all categories ordered by name
     */
    List<Category> findAllByOrderByNameAsc();
    
    /**
     * Check if category exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Check if category exists by name (case-insensitive) excluding the given id
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    
    /**
     * Find category by name (case-insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Count products by category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * Find categories with product count
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products p GROUP BY c.id")
    List<Category> findAllWithProductCount();
}
