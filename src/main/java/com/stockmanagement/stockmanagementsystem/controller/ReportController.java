package com.stockmanagement.stockmanagementsystem.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.service.ReportService;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public String reports() {
        return "reports/index";
    }

    @GetMapping("/reports/current-stock")
    public String currentStock(Model model) {
        model.addAttribute("products", reportService.getCurrentStockReport());
        return "reports/current-stock";
    }

    @GetMapping("/reports/stock-in")
    public String stockIn(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        return transactionReport(StockTransactionType.IN, fromDate, toDate, model, "reports/stock-in");
    }

    @GetMapping("/reports/stock-out")
    public String stockOut(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        return transactionReport(StockTransactionType.OUT, fromDate, toDate, model, "reports/stock-out");
    }

    @GetMapping("/reports/low-stock")
    public String lowStock(Model model) {
        model.addAttribute("products", reportService.getLowStockReport());
        return "reports/low-stock";
    }

    @GetMapping("/reports/movements")
    public String movements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        return transactionReport(null, fromDate, toDate, model, "reports/movements");
    }

    private String transactionReport(StockTransactionType transactionType, LocalDate fromDate,
            LocalDate toDate, Model model, String view) {
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        try {
            model.addAttribute("transactions",
                    reportService.getTransactionReport(transactionType, fromDate, toDate));
        } catch (IllegalArgumentException exception) {
            model.addAttribute("transactions", java.util.List.of());
            model.addAttribute("errorMessage", exception.getMessage());
        }
        return view;
    }
}
