package com.example.diplomaproject.controllers;

import com.example.diplomaproject.DTOs.*;
import com.example.diplomaproject.entity.CustomUser;
import com.example.diplomaproject.entity.Wallet;
import com.example.diplomaproject.entity.WalletTransaction;
import com.example.diplomaproject.services.CustomUserService;
import com.example.diplomaproject.services.WalletService;
import com.example.diplomaproject.services.WalletTransitionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@AllArgsConstructor
public class MyRestController {

    private final CustomUserService userService;
    private final WalletService walletService;
    private final WalletTransitionService walletTransitionService;


    // Додавання нового гаманця
    @PostMapping("/add_new_wallet")
    private String addNewWallet(@RequestBody MiniWalletDTO walletDTO) {

        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet wallet = new Wallet(walletDTO.getName());
        wallet.setCurrency(walletDTO.getCurrency());
        boolean checkWallet = customUser.addWallet(wallet);
        if (checkWallet == false) {
            return "false";
        }
        wallet.setCustomUser(customUser);
        walletService.save(wallet);
        userService.updateUser(customUser);

        System.out.println(walletDTO);
        return walletDTO.getName();
    }

    // Додавання нової транзакції
    @PostMapping("/money_transaction")
    private RestTransactionDTO money(@RequestBody WalletTransactionDTO walletTransactionDTO) {
        Double sum;
        WalletTransaction wt;
        Date date = new Date();
        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet userWallet = customUser.getWallet(walletTransactionDTO.getWalletName());
        if (userWallet == null) {
            System.out.println("NO");
            System.out.println(walletTransactionDTO);
            return null;
        }
        String formattedSum = String.format("%.2f", walletTransactionDTO.getSum());
        formattedSum = formattedSum.replace(",", ".");
        Double resultOfFormattedSum = Double.parseDouble(formattedSum);
        sum = userWallet.getSum();
        String sign;


        if (walletTransactionDTO.getSign().equals("-")) {
            sum -= resultOfFormattedSum;
            userWallet.setSum(sum);
            sign = "-";

        } else {
            sum += resultOfFormattedSum;
            userWallet.setSum(sum);
            sign = "+";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
        String strDate = sdf.format(date);
        String strMonth = sdf2.format(date);
        Integer month = Integer.parseInt(strMonth);
        wt = new WalletTransaction(resultOfFormattedSum, walletTransactionDTO.getPurpose(), sign, strDate, month);

        wt.setWallet(userWallet);
        userWallet.addWalletTransaction(wt);
        walletTransitionService.save(wt);
        walletService.update(userWallet);

        return new RestTransactionDTO(strDate, sum, userWallet.getCurrency());
    }

    @GetMapping("/get_wallet")
    private WalletDTO getWallet(@RequestParam("walletName") String walletName) {
        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet wallet = customUser.getWallet(walletName);
//        List<WalletTransaction> walletTransactions = walletTransitionService.findWalletTransactionsByWallet(wallet);

        return new WalletDTO(wallet.getName(), wallet.getSum(), wallet.getCurrency());
    }


    // Додавання нового призначення
    @PostMapping("/add_new_purpose")
    private ResponseEntity<String> addNewPurpose(@RequestParam("purpose") String newPurpose) {

        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        boolean check = customUser.addNewPurpose(newPurpose);
        if (check) {
            userService.updateUser(customUser);
        } else {
            return ResponseEntity.badRequest().body("Purpose name is exist!");
        }

        return ResponseEntity.ok("New purpose was add.");
    }

    // Отримання доходів або витрат
    @GetMapping("/transactions_by_purpose")
    private Map<String, Double> getAllTransactionsOfPurpose(@RequestParam("walletName") String walletName,
                                                            @RequestParam("sign") String sign) {
        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet wallet = customUser.getWallet(walletName);
        List<WalletTransaction> listOfWT = wallet.getWalletTransactions();
//        List<WalletTransaction> listOfWT = walletTransitionService.findWalletTransactionsByWallet(wallet);

        Map<String, Double> map = new HashMap<>();
        for (WalletTransaction wt : listOfWT) {

            if (wt.getSign().equals(sign)) {
                if (map.containsKey(wt.getPurpose())) {
                    Double temp = map.get(wt.getPurpose());
                    temp += wt.getSuma();
                    map.put(wt.getPurpose(), temp);

                } else {
                    map.put(wt.getPurpose(), wt.getSuma());
                }

            }

        }

        List<Map.Entry<String, Double>> list = new ArrayList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<String, Double> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    @GetMapping("/get_all_wallets")
    private List<String> getUserWallets() {
        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        List<Wallet> wallets = customUser.getListWallets();
        List<String> walletNamesList = new ArrayList<>();
        for (Wallet w : wallets) {
            walletNamesList.add(w.getName());
        }

        return walletNamesList;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
