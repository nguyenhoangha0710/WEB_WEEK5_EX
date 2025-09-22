package com.shoe.service;

import com.shoe.entity.Product;
import com.shoe.entity.Category;
import com.shoe.repository.ProductRepository;
import com.shoe.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Get all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategory();
    }
    
    /**
     * Get all products with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    /**
     * Search products by name with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
    }
    
    /**
     * Search products by name or code with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProductsByNameOrCode(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.searchProducts(searchTerm.trim(), pageable);
    }
    
    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    
    /**
     * Get products by category with search
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findByCategoryId(categoryId, pageable);
        }
        return productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, name.trim(), pageable);
    }
    
    /**
     * Get all products by category (without pagination)
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdWithCategory(categoryId);
    }
    
    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * Save product
     */
    public Product saveProduct(Product product) {
        // Check if product code already exists (case-insensitive)
        if (product.getId() == null) {
            // New product
            if (productRepository.existsByCodeIgnoreCase(product.getCode())) {
                throw new IllegalArgumentException("Product with code '" + product.getCode() + "' already exists");
            }
        } else {
            // Existing product
            if (productRepository.existsByCodeIgnoreCaseAndIdNot(product.getCode(), product.getId())) {
                throw new IllegalArgumentException("Product with code '" + product.getCode() + "' already exists");
            }
        }
        
        // Validate category exists
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Optional<Category> category = categoryRepository.findById(product.getCategory().getId());
            if (!category.isPresent()) {
                throw new IllegalArgumentException("Category with ID " + product.getCategory().getId() + " not found");
            }
            product.setCategory(category.get());
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Delete product by ID
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
        
        productRepository.deleteById(id);
    }
    
    /**
     * Check if product exists by code
     */
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return productRepository.existsByCodeIgnoreCase(code);
    }
    
    /**
     * Get paginated products with default sorting
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsWithPagination(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }
    
    /**
     * Get products by category with pagination and sorting
     */
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategoryWithPagination(Long categoryId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByCategoryId(categoryId, pageable);
    }
}
