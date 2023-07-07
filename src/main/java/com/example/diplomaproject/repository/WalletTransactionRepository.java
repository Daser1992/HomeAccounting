package com.example.diplomaproject.repository;

import com.example.diplomaproject.entity.Wallet;
import com.example.diplomaproject.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet.id = :walletId ORDER BY wt.id DESC")
    List<WalletTransaction>findWalletTransactionsByWalletId(@Param("walletId") Long walletId, Pageable pageable);

    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet = :wallet")
    List<WalletTransaction> findWalletTransactionByWallet(@Param("wallet") Wallet wallet);

    @Query("DELETE FROM WalletTransaction wt WHERE wt.wallet.id = :walletId")
    void deleteWalletTransactionByWalletId(@Param("walletId") Long id);

    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.id = :id")
    WalletTransaction findWalletTransactionById(@Param("id") Long id);

    @Query("SELECT wt.wallet FROM WalletTransaction wt WHERE wt.id = :transactionId")
    Wallet findWalletByTransactionId(@Param("transactionId") Long transactionId);


}
