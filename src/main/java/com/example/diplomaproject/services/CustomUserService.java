package com.example.diplomaproject.services;

import com.example.diplomaproject.Roles;
import com.example.diplomaproject.entity.CustomUser;
import com.example.diplomaproject.entity.Wallet;
import com.example.diplomaproject.repository.CustomUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserService {
    private final CustomUserRepository customUserRepository;
    private final WalletService walletService;


    public CustomUserService(CustomUserRepository customUserRepository, WalletService walletService) {
        this.customUserRepository = customUserRepository;
        this.walletService = walletService;

    }


    public void addAdmin(String login, String hashPass, String email, Roles role) {
        CustomUser admin = new CustomUser(login, hashPass, email, role);
        Wallet wallet = new Wallet("Гаманець");
        CustomUser test = customUserRepository.findByLogin(login);
        if (test != null) {
            return;
        }
        admin.addWallet(wallet);
        wallet.setCustomUser(admin);
        customUserRepository.save(admin);
        walletService.save(wallet);
    }


    public void addUser(  String login, String hashPass, String email, Roles role) {

        CustomUser customUser = new CustomUser(login, hashPass, email, role);
        Wallet wallet = new Wallet("Гаманець");
        customUser.addNewPurpose("Заробітня плата", "Відпустка", "Комунальні платежі", "Продукти", "Транспорт", "Інше");
        customUser.addWallet(wallet);
        wallet.setCustomUser(customUser);
        customUserRepository.save(customUser);
        walletService.save(wallet);
    }

    @Transactional
    public void updateUser(CustomUser user){
        customUserRepository.save(user);

    }

    @Transactional
    public List<CustomUser> findAllUsers(){
       List<CustomUser> customUserList = customUserRepository.findAllUsers();
       return customUserList;
    }

    @Transactional(readOnly = true)
    public CustomUser findUserByLogin(String login) {
        CustomUser customUser = customUserRepository.findByLogin(login);

        return customUser;
    }

}
