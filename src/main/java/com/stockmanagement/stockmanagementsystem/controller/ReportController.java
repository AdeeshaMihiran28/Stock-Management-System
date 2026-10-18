package com.stockmanagement.stockmanagementsystem.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.service.ReportService;

@Controller
public class ReportController {

    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    @GetMapping("/reports/current-stock/download")
    public ResponseEntity<byte[]> downloadCurrentStock() {
        StringBuilder csv = new StringBuilder();
        appendRow(csv, "Product Code", "Product Name", "Category", "Current Quantity",
                "Reorder Level", "Unit Price", "Stock Status");
        for (Product product : reportService.getCurrentStockReport()) {
            appendRow(csv, product.getProductCode(), product.getName(), product.getCategory().getName(),
                    product.getQuantity(), product.getReorderLevel(), product.getUnitPrice(), stockStatus(product));
        }
        return csvResponse("current-stock-report.csv", csv);
    }

    @GetMapping("/reports/stock-in")
    public String stockIn(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        return transactionReport(StockTransactionType.IN, fromDate, toDate, model, "reports/stock-in");
    }

    @GetMapping("/reports/stock-in/download")
    public ResponseEntity<byte[]> downloadStockIn(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return transactionCsvResponse(StockTransactionType.IN, fromDate, toDate, "stock-in-report.csv");
    }

    @GetMapping("/reports/stock-out")
    public String stockOut(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        return transactionReport(StockTransactionType.OUT, fromDate, toDate, model, "reports/stock-out");
    }

    @GetMapping("/reports/stock-out/download")
    public ResponseEntity<byte[]> downloadStockOut(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return transactionCsvResponse(StockTransactionType.OUT, fromDate, toDate, "stock-out-report.csv");
    }

    @GetMapping("/reports/low-stock")
    public String lowStock(Model model) {
        model.addAttribute("products", reportService.getLowStockReport());
        return "reports/low-stock";
    }

    @GetMapping("/reports/low-stock/download")
    public ResponseEntity<byte[]> downloadLowStock() {
        StringBuilder csv = new StringBuilder();
        appendRow(csv, "Product Code", "Product Name", "Category", "Quantity", "Reorder Level", "Status");
        for (Product product : reportService.getLowStockReport()) {
            appendRow(csv, product.getProductCode(), product.getName(), product.getCategory().getName(),
                    product.getQuantity(), product.getReorderLevel(), stockStatus(product));
        }
        return csvResponse("low-stock-report.csv", csv);
    }

    @GetMapping("/reports/movements")
    public String movements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {
        return transactionReport(null, fromDate, toDate, model, "reports/movements");
    }

    @GetMapping("/reports/movements/download")
    public ResponseEntity<byte[]> downloadMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return transactionCsvResponse(null, fromDate, toDate, "stock-movement-report.csv");
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

    private ResponseEntity<byte[]> transactionCsvResponse(StockTransactionType transactionType,
            LocalDate fromDate, LocalDate toDate, String filename) {
        List<StockTransaction> transactions = reportService.getTransactionReport(transactionType, fromDate, toDate);
        StringBuilder csv = new StringBuilder();
        appendRow(csv, "Date", "Type", "Product Code", "Product Name", "Supplier", "Quantity",
                "Reference Number", "Notes");
        for (StockTransaction transaction : transactions) {
            String supplier = transaction.getSupplier() == null ? ""
                    : transaction.getSupplier().getSupplierCode() + " - " + transaction.getSupplier().getName();
            appendRow(csv, CSV_DATE_FORMAT.format(transaction.getTransactionDate()), transaction.getTransactionType(),
                    transaction.getProduct().getProductCode(), transaction.getProduct().getName(), supplier,
                    transaction.getQuantity(), transaction.getReferenceNumber(), transaction.getNotes());
        }
        return csvResponse(filename, csv);
    }

    private ResponseEntity<byte[]> csvResponse(String filename, StringBuilder csv) {
        byte[] content = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(content.length)
                .body(content);
    }

    private void appendRow(StringBuilder csv, Object... values) {
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                csv.append(',');
            }
            String value = values[index] == null ? "" : values[index].toString();
            if (!value.isEmpty() && "=+-@".indexOf(value.charAt(0)) >= 0) {
                value = "'" + value;
            }
            csv.append('"').append(value.replace("\"", "\"\"")).append('"');
        }
        csv.append("\r\n");
    }

    private String stockStatus(Product product) {
        if (product.getQuantity() == 0) {
            return "OUT OF STOCK";
        }
        return product.getQuantity() <= product.getReorderLevel() ? "LOW STOCK" : "IN STOCK";
    }
}
