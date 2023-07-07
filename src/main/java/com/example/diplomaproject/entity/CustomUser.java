package com.example.diplomaproject.entity;

import com.example.diplomaproject.Roles;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class CustomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private Roles role;

    @OneToMany(mappedBy = "customUser", cascade = CascadeType.ALL)
    private List<Wallet> listWallets = new ArrayList<>();
    @ElementCollection
    private List<String> listPurposes = new ArrayList<>();

    public boolean addNewPurpose(String... purposes){
        for (String purpose: purposes) {
            if (!listPurposes.contains(purpose)){
                listPurposes.add(purpose);
            }else return false;

        }

        return true;
    }

    public boolean addWallet(Wallet wallet) {
        int count = 0;
        for (Wallet w: listWallets) {
            if (w.getName().equals(wallet.getName())){
                ++count;
            }
        }
        if (count > 0){
            return false;
        }else listWallets.add(wallet);
//        if (!listWallets.contains(wallet)) {
//            listWallets.add(wallet);
//        }
        return true;
    }


    public Wallet getWallet(String walletName){
        for (Wallet w: listWallets) {
            if (w.getName().equalsIgnoreCase(walletName)){
                return w;
            }
        }
        return null;
    }


    public CustomUser(String login, String password, String email, Roles role) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.role = role;
    }

}
