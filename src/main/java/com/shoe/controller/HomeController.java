package com.shoe.controller;

import com.shoe.entity.Category;
import com.shoe.entity.Product;
import com.shoe.service.CategoryService;
import com.shoe.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductService productService;
    
    /**
     * Display home page with recent categories and products
     */
    @GetMapping("/")
    public String home(Model model) {
        // Get recent categories (limit to 5)
        Pageable categoryPageable = PageRequest.of(0, 5);
        List<Category> recentCategories = categoryService.getAllCategories(categoryPageable).getContent();
        
        // Get recent products (limit to 10)
        Pageable productPageable = PageRequest.of(0, 10);
        List<Product> recentProducts = productService.getAllProducts(productPageable).getContent();
        
        model.addAttribute("recentCategories", recentCategories);
        model.addAttribute("recentProducts", recentProducts);
        // return "index";
        return "index";
        // return "index-test";
    }
    
    /**
     * Display dashboard with statistics
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get total counts
        long totalCategories = categoryService.getAllCategories().size();
        long totalProducts = productService.getAllProducts().size();
        
        // Get recent categories and products
        Pageable categoryPageable = PageRequest.of(0, 5);
        List<Category> recentCategories = categoryService.getAllCategories(categoryPageable).getContent();
        
        Pageable productPageable = PageRequest.of(0, 10);
        List<Product> recentProducts = productService.getAllProducts(productPageable).getContent();
        
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("recentCategories", recentCategories);
        model.addAttribute("recentProducts", recentProducts);
        
        return "dashboard";
    }
}
