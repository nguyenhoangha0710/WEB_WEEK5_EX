package com.shoe.controller;

import com.shoe.entity.Product;
import com.shoe.entity.Category;
import com.shoe.service.ProductService;
import com.shoe.service.CategoryService;
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
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * Display all products with pagination and search
     */
    @GetMapping
    public String listProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, 
            sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        
        Page<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProductsByNameOrCode(search, pageable);
            model.addAttribute("search", search);
        } else {
            products = productService.getAllProducts(pageable);
        }
        
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "product/list";
    }
    
    /**
     * Display product form for creating new product
     */
    @GetMapping("/new")
    public String showNewProductForm(Model model) {
        Product product = new Product();
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product/form";
    }
    
    /**
     * Display product form for editing existing product
     */
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product/form";
    }
    
    /**
     * Save product (create or update)
     */
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        if (result.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "product/form";
        }
        
        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", 
                product.getId() == null ? "Product created successfully!" : "Product updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "product/form";
        }
        
        return "redirect:/products";
    }
    
    /**
     * Delete product
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/products";
    }
    
    /**
     * View product details
     */
    @GetMapping("/view/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + id));
        
        model.addAttribute("product", product);
        return "product/view";
    }
    
    /**
     * Display products by category
     */
    @GetMapping("/category/{categoryId}")
    public String getProductsByCategory(@PathVariable Long categoryId,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                      @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                      @RequestParam(value = "search", required = false) String search,
                                      Model model) {
        
        Category category = categoryService.getCategoryById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
        
        Pageable pageable = PageRequest.of(page, size, 
            sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        
        Page<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.getProductsByCategory(categoryId, search, pageable);
            model.addAttribute("search", search);
        } else {
            products = productService.getProductsByCategory(categoryId, pageable);
        }
        
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "product/category-products";
    }
}
