package com.stockmanagement.stockmanagementsystem.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockmanagement.stockmanagementsystem.model.StockTransaction;
import com.stockmanagement.stockmanagementsystem.model.StockTransactionType;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByTransactionTypeOrderByTransactionDateDesc(StockTransactionType transactionType);

    List<StockTransaction> findTop5ByOrderByTransactionDateDesc();

    @Query("""
            SELECT transaction FROM StockTransaction transaction
            WHERE (:transactionType IS NULL OR transaction.transactionType = :transactionType)
              AND (:fromDate IS NULL OR transaction.transactionDate >= :fromDate)
              AND (:toDateExclusive IS NULL OR transaction.transactionDate < :toDateExclusive)
            ORDER BY transaction.transactionDate DESC
            """)
    List<StockTransaction> findForReport(
            @Param("transactionType") StockTransactionType transactionType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDateExclusive") LocalDateTime toDateExclusive);
}
