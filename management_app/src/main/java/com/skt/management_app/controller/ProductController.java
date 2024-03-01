package com.skt.management_app.controller;

import com.skt.management_app.core.ProductService;
import com.skt.management_app.model.Product;
import com.skt.management_app.model.SaveProductResponse;
import com.skt.management_app.model.SelectProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ModelAndView getAllStudents() {
        ModelAndView view = new ModelAndView("products");
        SelectProductResponse response = productService.selectAll();
        view.addObject("status", response.getStatus())
                .addObject("message", response.getMessage())
                .addObject("products", response.getProducts());
        return view;
    }

    @GetMapping("/add-product")
    public ModelAndView addStudent() {
        ModelAndView view = new ModelAndView("add_product");
        view.addObject("product", new Product());
        return view;
    }

    @PostMapping("/save-product")
    public ModelAndView saveStaff(@ModelAttribute Product product) {
        ModelAndView view = new ModelAndView("add_product_response");
        SaveProductResponse response = productService.save(product);
        view.addObject("status", response.getStatus())
                .addObject("message", response.getMessage());
        return view;
    }
}
