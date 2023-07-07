package com.example.diplomaproject.services;

import com.example.diplomaproject.entity.Wallet;
import com.example.diplomaproject.entity.WalletTransaction;
import com.example.diplomaproject.repository.WalletTransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class WalletTransitionService {

    private final WalletTransactionRepository WTRepository;

    public WalletTransitionService(WalletTransactionRepository WTRepository) {
        this.WTRepository = WTRepository;
    }

    @Transactional
    public void save(WalletTransaction walletTransaction){
        WTRepository.save(walletTransaction);
    }

    @Transactional(readOnly = true)
    public List<WalletTransaction> findWalletTransactionsByWalletId(Long walletId, int page, int number){
        PageRequest pageRequest = PageRequest.of(page, number, Sort.by("id").descending());
        List<WalletTransaction> listOfLastTransactions = WTRepository.findWalletTransactionsByWalletId(walletId, pageRequest);
        return listOfLastTransactions;
    }

    @Transactional(readOnly = true)
    public List<WalletTransaction> findWalletTransactionsByWallet(Wallet wallet){
        List<WalletTransaction> walletTransactions = WTRepository.findWalletTransactionByWallet(wallet);

        return  walletTransactions;
    }

    @Transactional
    public void deleteWalletTransactionsByWalletId(Long id){
        WTRepository.deleteWalletTransactionByWalletId(id);
    }

    @Transactional
    public boolean delete(WalletTransaction wt){
        WTRepository.delete(wt);
        return true;
    }


    @Transactional
    public WalletTransaction findWalletTransactionById(Long id){
       WalletTransaction wt = WTRepository.findWalletTransactionById(id);
       return wt;
    }

}
