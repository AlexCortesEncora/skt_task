package com.skt.management_app.controller;

import com.skt.management_app.core.SelectProductService;
import com.skt.management_app.model.SelectProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductController {

    @Autowired
    private SelectProductService selectProductService;

    @GetMapping("/products")
    public ModelAndView getAllStudents() {
        ModelAndView view = new ModelAndView("products");
        SelectProductResponse response = selectProductService.selectAll();
        view.addObject("status", response.getStatus())
                .addObject("message", response.getMessage())
                .addObject("products", response.getProducts());
        return view;
    }
}
