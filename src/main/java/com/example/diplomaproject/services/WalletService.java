package com.example.diplomaproject.services;

import com.example.diplomaproject.entity.Wallet;
import com.example.diplomaproject.entity.WalletTransaction;
import com.example.diplomaproject.repository.WalletRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service

public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void save(Wallet wallet) {
        walletRepository.save(wallet);
    }

    @Transactional
    public void deleteWalletById(Long id) {
        walletRepository.deleteWalletById(id);
    }

    @Transactional
    public Wallet findWalletById(Long id) {
        Wallet wallet = walletRepository.findWalletById(id);

        return wallet;
    }

    @Transactional
    public void update(Wallet wallet) {
        walletRepository.save(wallet);
    }

}
