package com.example.diplomaproject.controllers;

import com.example.diplomaproject.Roles;
import com.example.diplomaproject.entity.CustomUser;
import com.example.diplomaproject.entity.Wallet;
import com.example.diplomaproject.entity.WalletTransaction;
import com.example.diplomaproject.services.CustomUserService;
import com.example.diplomaproject.services.WalletService;
import com.example.diplomaproject.services.WalletTransitionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MainController {
    private final CustomUserService userService;
    private final WalletService walletService;

    private final WalletTransitionService walletTransitionService;
    private final PasswordEncoder encoder;

    public MainController(CustomUserService userService, WalletService walletService, WalletTransitionService walletTransitionService, PasswordEncoder encoder) {
        this.userService = userService;
        this.walletService = walletService;
        this.walletTransitionService = walletTransitionService;
        this.encoder = encoder;
    }

    @GetMapping("/")
    private String index(Model model,
                         @RequestParam(name = "walletName", defaultValue = "Гаманець") String walletName) {
        User user = getCurrentUser();
        CustomUser customUser = userService.findUserByLogin(user.getUsername());
        Wallet wallet = customUser.getWallet(walletName);
        List<WalletTransaction> listOfWT = walletTransitionService.findWalletTransactionsByWalletId(wallet.getId(), 0, 10);
        String formattedNumber = String.format("%.2f", wallet.getSum());

        model.addAttribute("login", customUser.getLogin());
        model.addAttribute("wallet_name", wallet.getName());
        model.addAttribute("sum_of_wallet", formattedNumber + wallet.getCurrency());
        model.addAttribute("currency", wallet.getCurrency());
        model.addAttribute("transactions", listOfWT);
        model.addAttribute("listOfWallet", customUser.getListWallets());
        model.addAttribute("purposes", customUser.getListPurposes());
        model.addAttribute("currentWallet", walletName);

        return "index";
    }

    // Додавання нового користувача
    @PostMapping("/add_user")
    private String addUser(@RequestParam String login,
                           @RequestParam String password,
                           Model model) {
        CustomUser customUser = userService.findUserByLogin(login);
        if (customUser != null) {
            model.addAttribute("check_login", "Користувач с таким логіном вже існує");
            return "registration";
        }
        if (password.length() < 6) {
            model.addAttribute("check_password", "Треба ввести не меньше 6 символів");
            model.addAttribute("login", login);
            return "registration";
        }

        userService.addUser(login, encoder.encode(password), "", Roles.User);
        return "redirect:/login";
    }

    @GetMapping("/private_office")
    private String userOffice(Model model) {
        CustomUser customUser = userService.findUserByLogin(getCurrentUser().getUsername());
        Wallet mainWallet = customUser.getWallet("Гаманець");
        String formatForMainWallet = String.format("%.2f", mainWallet.getSum());
        formatForMainWallet = formatForMainWallet.replace(",", ".");
        List<Wallet> walletList = customUser.getListWallets();



        model.addAttribute("user_wallets", walletList);
        model.addAttribute("userLogin", customUser.getLogin());
        model.addAttribute("purposes", customUser.getListPurposes());
        model.addAttribute("main_wallet", mainWallet.getName());
        model.addAttribute("main_walletSum", formatForMainWallet);
        model.addAttribute("main_walletCurrency", mainWallet.getCurrency());

        return "privateOffice";
    }


    @GetMapping("/login")
    private String login() {

        return "login";
    }

    @GetMapping("/admin")
    private String admin(Model model){

        List<CustomUser> users = userService.findAllUsers();

        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/403")
    private String error403(){

        return "403";
    }

    @GetMapping("/404")
    private String error404(){

        return "404";
    }


    @GetMapping("/registration")
    private String registration() {

        return "registration";
    }


    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }


}
