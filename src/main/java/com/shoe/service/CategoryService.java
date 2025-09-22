package com.shoe.service;

import com.shoe.entity.Category;
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
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Get all categories
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }
    
    /**
     * Get all categories with pagination
     */
    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    /**
     * Search categories by name with pagination
     */
    @Transactional(readOnly = true)
    public Page<Category> searchCategories(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
    }
    
    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    /**
     * Save category
     */
    public Category saveCategory(Category category) {
        // Check if category name already exists (case-insensitive)
        if (category.getId() == null) {
            // New category
            if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
                throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
            }
        } else {
            // Existing category
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(category.getName(), category.getId())) {
                throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
            }
        }
        
        return categoryRepository.save(category);
    }
    
    /**
     * Delete category by ID
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category with ID " + id + " not found");
        }
        
        // Check if category has products
        long productCount = categoryRepository.countProductsByCategoryId(id);
        if (productCount > 0) {
            throw new IllegalArgumentException("Cannot delete category with existing products. Please delete all products first.");
        }
        
        categoryRepository.deleteById(id);
    }
    
    /**
     * Check if category exists by name
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
    
    /**
     * Get categories with product count
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoriesWithProductCount() {
        return categoryRepository.findAllWithProductCount();
    }
    
    /**
     * Get paginated categories with default sorting
     */
    @Transactional(readOnly = true)
    public Page<Category> getCategoriesWithPagination(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepository.findAll(pageable);
    }
}
