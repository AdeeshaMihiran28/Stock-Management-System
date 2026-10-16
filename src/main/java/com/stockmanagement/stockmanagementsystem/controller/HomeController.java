package com.stockmanagement.stockmanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.stockmanagement.stockmanagementsystem.service.ReportService;

@Controller
public class HomeController {

    private final ReportService reportService;

    public HomeController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalProducts", reportService.getTotalProducts());
        model.addAttribute("totalCategories", reportService.getTotalCategories());
        model.addAttribute("totalSuppliers", reportService.getTotalSuppliers());
        model.addAttribute("totalStockQuantity", reportService.getTotalStockQuantity());
        model.addAttribute("lowStockCount", reportService.getLowStockCount());
        model.addAttribute("outOfStockCount", reportService.getOutOfStockCount());
        model.addAttribute("recentTransactions", reportService.getRecentTransactions());
        return "index";
    }
}
