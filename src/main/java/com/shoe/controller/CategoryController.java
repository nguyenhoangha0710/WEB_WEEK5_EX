package com.shoe.controller;

import com.shoe.entity.Category;
import com.shoe.service.CategoryService;
import com.shoe.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductService productService;
    
    /**
     * Display all categories with pagination and search
     */
    @GetMapping
    public String listCategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, 
            sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        
        Page<Category> categories;
        if (search != null && !search.trim().isEmpty()) {
            categories = categoryService.searchCategories(search, pageable);
            model.addAttribute("search", search);
        } else {
            categories = categoryService.getAllCategories(pageable);
        }
        
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categories.getTotalPages());
        model.addAttribute("totalItems", categories.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
//         return "category/list";
// return "category/list-test";
        return "category/list";
    }
    
    /**
     * Display category form for creating new category
     */
    @GetMapping("/new")
    public String showNewCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/form";
        // return "category/form-test";
    }
    
    /**
     * Display category form for editing existing category
     */
    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Category category = categoryService.getCategoryById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));
        
        model.addAttribute("category", category);
        return "category/form";
        // return "category/form-test";
    }
    
    /**
     * Save category (create or update)
     */
    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "category/form";
            // return "category/form-test";
        }
        
        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", 
                category.getId() == null ? "Category created successfully!" : "Category updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/categories/new";
        }
        
        return "redirect:/categories";
    }
    
    /**
     * Delete category
     */
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/categories";
    }
    
    /**
     * View category details with products
     */
    @GetMapping("/view/{id}")
    public String viewCategory(@PathVariable Long id, 
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                              @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                              @RequestParam(value = "search", required = false) String search,
                              Model model) {
        
        Category category = categoryService.getCategoryById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + id));
        
        Pageable pageable = PageRequest.of(page, size, 
            sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        
        // Get products for this category
        Page<com.shoe.entity.Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.getProductsByCategory(id, search, pageable);
            model.addAttribute("search", search);
        } else {
            products = productService.getProductsByCategory(id, pageable);
        }
        
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "category/view";
    }
    
    /**
     * Get all categories for dropdown/select options
     */
    @GetMapping("/api/all")
    @ResponseBody
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
