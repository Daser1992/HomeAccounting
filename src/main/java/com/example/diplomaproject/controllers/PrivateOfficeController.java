package com.example.diplomaproject.controllers;

import com.example.diplomaproject.DTOs.WalletTransactionsByWalletDTO;
import com.example.diplomaproject.entity.CustomUser;
import com.example.diplomaproject.entity.Wallet;
import com.example.diplomaproject.entity.WalletTransaction;
import com.example.diplomaproject.services.CustomUserService;
import com.example.diplomaproject.services.WalletService;
import com.example.diplomaproject.services.WalletTransitionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@AllArgsConstructor
public class PrivateOfficeController {
    private final CustomUserService userService;
    private final WalletService walletService;
    private final WalletTransitionService walletTransitionService;

    // Всі транзакції вибраного гаманця
    @GetMapping("/get_all_transaction_by_wallet")
    private List<WalletTransactionsByWalletDTO> getAllTransactionByWallet(@RequestParam("walletName") String walletName,
                                                                          @RequestParam("page") int page,
                                                                          @RequestParam("howMuch") int howMuchShow) {
        CustomUser user = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet wallet = user.getWallet(walletName);
        List<WalletTransaction> walletTransactions = walletTransitionService.findWalletTransactionsByWalletId(wallet.getId(), page, howMuchShow);
        List<WalletTransactionsByWalletDTO> result = new ArrayList<>();

        for (WalletTransaction wt : walletTransactions) {
            result.add(new WalletTransactionsByWalletDTO(wt.getId(), wt.getSuma(), wt.getPurpose(), wt.getSign(), wt.getDate(), wallet.getCurrency(), walletName));
        }

        return result;
    }

    // Транзакции за вибраним місяцем
    @GetMapping("/private_office/wallet_transactions_by_month")
    private List<WalletTransactionsByWalletDTO> walletTransactionsByMonth(@RequestParam("month") Integer month,
                                                                          @RequestParam("walletName") String walletName) {
        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet wallet = customUser.getWallet(walletName);
        List<WalletTransaction> walletTransactions = wallet.getWalletTransactions();


        List<WalletTransactionsByWalletDTO> result = new ArrayList<>();
        for (WalletTransaction wt : walletTransactions) {
            if (wt.getMonth().intValue() == month.intValue()) {
                result.add(new WalletTransactionsByWalletDTO(wt.getId(), wt.getSuma(), wt.getPurpose(), wt.getSign(),
                        wt.getDate(), wallet.getCurrency(), walletName));
            }
        }

        Collections.reverse(result);

        return result;
    }

    // Видалення транзакції
    @DeleteMapping("/remove_transaction")
    private ResponseEntity<String> deleteTransaction(@RequestParam("id") Long id) {
        WalletTransaction wt = walletTransitionService.findWalletTransactionById(id);
        if (wt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction with ID " + id + " not found.");
        }
        Wallet wallet = walletService.findWalletById(wt.getWallet().getId());
        Double wtSum = wt.getSuma();
        Double result;
        Double walletSum = wallet.getSum();
        if (walletTransitionService.delete(wt)) {

            if (wt.getSign().equals("+")) {
                result = walletSum - wtSum;
                wallet.setSum(result);
            } else {
                result = walletSum + wtSum;
                wallet.setSum(result);
            }

            walletService.update(wallet);

        }
        return ResponseEntity.ok("Transaction with ID " + id + " has been successfully deleted.");
    }

    //Видалення гаманця
    @PostMapping("/delete_wallet")
    private ResponseEntity<String> deleteWallet(@RequestParam("walletName") String walletName) {
        CustomUser user = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet wallet = user.getWallet(walletName);
        if (wallet == null) {
            return ResponseEntity.badRequest().body("Wallet not found");
        } else if (wallet.getName().equals("Гаманець")) {
            return ResponseEntity.status(403).body("Гаманець won't be delete!");
        }

        List<WalletTransaction> walletTransactions = walletTransitionService.findWalletTransactionsByWallet(wallet);
        for (WalletTransaction wt : walletTransactions) {
            walletTransitionService.delete(wt);
        }

        walletService.deleteWalletById(wallet.getId());

        return ResponseEntity.ok("Wallet was delete");
    }

    @PostMapping("/remove_purpose")
    public void removePurpose(@RequestParam("purpose") String purposeName){
        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());

        List<String> list = customUser.getListPurposes();
        list.remove(purposeName);

        userService.updateUser(customUser);

        System.out.println("All right");
    }


    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
