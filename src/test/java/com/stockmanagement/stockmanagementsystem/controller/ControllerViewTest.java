package com.stockmanagement.stockmanagementsystem.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import com.stockmanagement.stockmanagementsystem.config.SecurityConfig;
import com.stockmanagement.stockmanagementsystem.service.CategoryService;
import com.stockmanagement.stockmanagementsystem.model.Category;
import com.stockmanagement.stockmanagementsystem.model.Product;
import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;
import com.stockmanagement.stockmanagementsystem.model.User;
import com.stockmanagement.stockmanagementsystem.service.ProductService;
import com.stockmanagement.stockmanagementsystem.service.SupplierService;
import com.stockmanagement.stockmanagementsystem.service.StockInService;
import com.stockmanagement.stockmanagementsystem.service.StockOutService;
import com.stockmanagement.stockmanagementsystem.service.ReportService;
import com.stockmanagement.stockmanagementsystem.service.UserService;

@WebMvcTest({HomeController.class, CategoryController.class, ProductController.class,
        SupplierController.class, StockInController.class, StockOutController.class,
        LowStockController.class, ReportController.class, LoginController.class, UserController.class})
@Import(SecurityConfig.class)
@WithMockUser(username = "admin", roles = "ADMIN")
class ControllerViewTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private SupplierService supplierService;

    @MockitoBean
    private StockInService stockInService;

    @MockitoBean
    private StockOutService stockOutService;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private UserService userService;

    @Test
    void rendersHomeCategoryAndProductPages() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(productService.searchProducts("")).thenReturn(List.of());
        when(reportService.getRecentTransactions()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Inventory Dashboard")));
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/list"));
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"));
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"));
    }

    @Test
    void returnsProductFormWithValidationMessages() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(post("/products").with(csrf())
                        .param("productCode", "P001")
                        .param("name", "Wireless Mouse")
                        .param("quantity", "-1")
                        .param("reorderLevel", "-1")
                        .param("unitPrice", "-1")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "product", "category", "quantity", "reorderLevel", "unitPrice"));
    }

    @Test
    void rendersSupplierPages() throws Exception {
        when(supplierService.searchSuppliers("")).thenReturn(List.of());

        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(view().name("suppliers/list"));
        mockMvc.perform(get("/suppliers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("suppliers/form"));
    }

    @Test
    void returnsSupplierFormWithValidationMessages() throws Exception {
        mockMvc.perform(post("/suppliers").with(csrf())
                        .param("supplierCode", "")
                        .param("name", "")
                        .param("phone", "")
                        .param("email", "not-an-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("suppliers/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "supplier", "supplierCode", "name", "phone", "email", "status"));
    }

    @Test
    void rendersStockInPages() throws Exception {
        when(stockInService.getStockInTransactions()).thenReturn(List.of());
        when(productService.getActiveProducts()).thenReturn(List.of());
        when(supplierService.getActiveSuppliers()).thenReturn(List.of());

        mockMvc.perform(get("/stock-in"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock-in/list"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Current Available")));
        mockMvc.perform(get("/stock-in/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock-in/form"));
    }

    @Test
    void returnsStockInFormWithValidationMessages() throws Exception {
        mockMvc.perform(post("/stock-in").with(csrf())
                        .param("quantity", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock-in/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "stockInForm", "productId", "supplierId", "quantity", "transactionDate"));
    }

    @Test
    void showsFriendlyMessageWhenStockInDatabaseWriteFails() throws Exception {
        when(productService.getActiveProducts()).thenReturn(List.of());
        when(supplierService.getActiveSuppliers()).thenReturn(List.of());
        doThrow(new DataAccessResourceFailureException("database unavailable"))
                .when(stockInService).processStockIn(org.mockito.ArgumentMatchers.any());

        mockMvc.perform(post("/stock-in").with(csrf())
                        .param("productId", "1")
                        .param("supplierId", "2")
                        .param("quantity", "5")
                        .param("transactionDate", "2026-10-11T12:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock-in/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Stock-In could not be completed. Please try again.")));
    }

    @Test
    void rendersStockOutPages() throws Exception {
        when(stockOutService.getStockOutTransactions()).thenReturn(List.of());
        when(productService.getActiveProducts()).thenReturn(List.of());

        mockMvc.perform(get("/stock-out"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock-out/list"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Current Available")));
        mockMvc.perform(get("/stock-out/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock-out/form"));
    }

    @Test
    void returnsStockOutFormWithValidationMessages() throws Exception {
        mockMvc.perform(post("/stock-out").with(csrf())
                        .param("quantity", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("stock-out/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "stockOutForm", "productId", "quantity", "transactionDate"));
    }

    @Test
    void redirectsAfterSuccessfulStockOut() throws Exception {
        Product product = new Product();
        product.setProductCode("P001");
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setQuantity(8);
        when(stockOutService.processStockOut(org.mockito.ArgumentMatchers.any())).thenReturn(transaction);

        mockMvc.perform(post("/stock-out").with(csrf())
                        .param("productId", "1")
                        .param("quantity", "8")
                        .param("transactionDate", "2026-10-11T12:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stock-out"))
                .andExpect(flash().attribute("successMessage",
                        "Stock-Out completed successfully. 8 units were removed from P001. Current available quantity: 0."));
    }

    @Test
    void rendersLowStockPageAndDashboardCount() throws Exception {
        when(productService.getLowStockProducts()).thenReturn(List.of());
        when(reportService.getLowStockCount()).thenReturn(3L);
        when(reportService.getRecentTransactions()).thenReturn(List.of());

        mockMvc.perform(get("/low-stock"))
                .andExpect(status().isOk())
                .andExpect(view().name("low-stock/list"))
                .andExpect(model().attribute("lowStockCount", 0));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("lowStockCount", 3L));
    }

    @Test
    void rendersAllReportPages() throws Exception {
        when(reportService.getCurrentStockReport()).thenReturn(List.of());
        when(reportService.getLowStockReport()).thenReturn(List.of());
        when(reportService.getTransactionReport(
                org.mockito.ArgumentMatchers.nullable(com.stockmanagement.stockmanagementsystem.model.StockTransactionType.class),
                org.mockito.ArgumentMatchers.nullable(java.time.LocalDate.class),
                org.mockito.ArgumentMatchers.nullable(java.time.LocalDate.class))).thenReturn(List.of());

        mockMvc.perform(get("/reports")).andExpect(status().isOk()).andExpect(view().name("reports/index"));
        mockMvc.perform(get("/reports/current-stock")).andExpect(status().isOk()).andExpect(view().name("reports/current-stock"));
        mockMvc.perform(get("/reports/stock-in")).andExpect(status().isOk()).andExpect(view().name("reports/stock-in"));
        mockMvc.perform(get("/reports/stock-out")).andExpect(status().isOk()).andExpect(view().name("reports/stock-out"));
        mockMvc.perform(get("/reports/low-stock")).andExpect(status().isOk()).andExpect(view().name("reports/low-stock"));
        mockMvc.perform(get("/reports/movements")).andExpect(status().isOk()).andExpect(view().name("reports/movements"));
    }

    @Test
    void downloadsReportsAsCsvAndKeepsDateFilters() throws Exception {
        Category category = new Category();
        category.setName("Accessories");
        Product product = new Product();
        product.setProductCode("P001");
        product.setName("Mouse, Pro");
        product.setCategory(category);
        product.setQuantity(5);
        product.setReorderLevel(2);
        product.setUnitPrice(new BigDecimal("1500.00"));
        when(reportService.getCurrentStockReport()).thenReturn(List.of(product));

        mockMvc.perform(get("/reports/current-stock/download"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("current-stock-report.csv")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"Mouse, Pro\"")));

        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setTransactionType(StockTransactionType.IN);
        transaction.setQuantity(3);
        transaction.setTransactionDate(LocalDateTime.of(2026, 10, 10, 9, 30));
        transaction.setReferenceNumber("=unsafe");
        when(reportService.getTransactionReport(
                StockTransactionType.IN, java.time.LocalDate.of(2026, 10, 1),
                java.time.LocalDate.of(2026, 10, 31))).thenReturn(List.of(transaction));

        mockMvc.perform(get("/reports/stock-in/download")
                        .param("fromDate", "2026-10-01").param("toDate", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("stock-in-report.csv")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"'=unsafe\"")));

        when(reportService.getLowStockReport()).thenReturn(List.of());
        when(reportService.getTransactionReport(StockTransactionType.OUT, null, null)).thenReturn(List.of());
        when(reportService.getTransactionReport(null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/reports/low-stock/download")).andExpect(status().isOk());
        mockMvc.perform(get("/reports/stock-out/download")).andExpect(status().isOk());
        mockMvc.perform(get("/reports/movements/download")).andExpect(status().isOk());
    }

    @Test
    void preselectsProductOnStockInForm() throws Exception {
        when(productService.getActiveProducts()).thenReturn(List.of());
        when(supplierService.getActiveSuppliers()).thenReturn(List.of());

        mockMvc.perform(get("/stock-in/new").param("productId", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("stockInForm",
                        org.hamcrest.Matchers.hasProperty("productId", org.hamcrest.Matchers.is(5L))));
    }

    @Test
    @WithAnonymousUser
    void showsLoginAndRedirectsProtectedPages() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
        mockMvc.perform(get("/products")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "MANAGEMENT")
    void managementHasReadOnlyAccess() throws Exception {
        when(productService.searchProducts("")).thenReturn(List.of());
        mockMvc.perform(get("/products")).andExpect(status().isOk());
        mockMvc.perform(get("/products/new")).andExpect(status().isForbidden());
        mockMvc.perform(post("/products").with(csrf())).andExpect(status().isForbidden());
        mockMvc.perform(get("/reports")).andExpect(status().isOk());
        mockMvc.perform(get("/stock-in")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STOCK_CLERK")
    void stockClerkCannotAccessAdministrativeRoutes() throws Exception {
        mockMvc.perform(get("/stock-out/new")).andExpect(status().isOk());
        mockMvc.perform(get("/suppliers")).andExpect(status().isForbidden());
        mockMvc.perform(get("/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "INVENTORY_MANAGER")
    void inventoryManagerCanManageInventoryButNotUsers() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());
        mockMvc.perform(get("/categories")).andExpect(status().isOk());
        mockMvc.perform(get("/stock-in")).andExpect(status().isOk());
        mockMvc.perform(get("/stock-out")).andExpect(status().isOk());
        mockMvc.perform(get("/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PURCHASING_OFFICER")
    void purchasingOfficerCanManageSupplyButNotStockOut() throws Exception {
        when(supplierService.searchSuppliers("")).thenReturn(List.of());
        mockMvc.perform(get("/suppliers")).andExpect(status().isOk());
        mockMvc.perform(get("/stock-in")).andExpect(status().isOk());
        mockMvc.perform(get("/reports")).andExpect(status().isOk());
        mockMvc.perform(get("/stock-out")).andExpect(status().isForbidden());
    }

    @Test
    void rejectsPostWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/products")).andExpect(status().isForbidden());
    }

    @Test
    void adminCanViewUserManagement() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());
        mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(view().name("users/list"));
        mockMvc.perform(get("/users/new")).andExpect(status().isOk()).andExpect(view().name("users/form"));

        User user = new User();
        user.setUsername("test.user");
        when(userService.getUserById(1L)).thenReturn(user);
        mockMvc.perform(get("/users/password/1"))
                .andExpect(status().isOk()).andExpect(view().name("users/password"));
    }

    @Test
    void logoutRequiresPostWithCsrf() throws Exception {
        mockMvc.perform(post("/logout")).andExpect(status().isForbidden());
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login?logout"));
    }
}
