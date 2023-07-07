package com.example.diplomaproject.repository;

import com.example.diplomaproject.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("SELECT w FROM Wallet w WHERE w.name = :name")
    Wallet findWalletByName(@Param("name") String name);

    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Wallet findWalletById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM Wallet w WHERE w.id = :id")
    void deleteWalletById(@Param("id") Long id);


}
