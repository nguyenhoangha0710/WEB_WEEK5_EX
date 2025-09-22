package com.shoe.repository;

import com.shoe.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find products by name containing the given string (case-insensitive)
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find products by category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * Find products by category and name containing the given string (case-insensitive)
     */
    Page<Product> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name, Pageable pageable);
    
    /**
     * Find all products ordered by name
     */
    List<Product> findAllByOrderByNameAsc();
    
    /**
     * Find products by category ordered by name
     */
    List<Product> findByCategoryIdOrderByNameAsc(Long categoryId);
    
    /**
     * Check if product exists by code (case-insensitive)
     */
    boolean existsByCodeIgnoreCase(String code);
    
    /**
     * Check if product exists by code (case-insensitive) excluding the given id
     */
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
    
    /**
     * Find product by code (case-insensitive)
     */
    Optional<Product> findByCodeIgnoreCase(String code);
    
    /**
     * Find products with category information
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category ORDER BY p.name")
    List<Product> findAllWithCategory();
    
    /**
     * Find products by category with category information
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :categoryId ORDER BY p.name")
    List<Product> findByCategoryIdWithCategory(@Param("categoryId") Long categoryId);
    
    /**
     * Search products by name or code
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);
}
