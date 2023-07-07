package com.example.diplomaproject.springSecurity;

import com.example.diplomaproject.services.CustomUserService;
import com.example.diplomaproject.entity.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final CustomUserService userService;

    public UserDetailsServiceImp(CustomUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        CustomUser customUser = userService.findUserByLogin(login);
        if (customUser == null) {
            throw new UsernameNotFoundException(login + " not found");
        }

        List<GrantedAuthority> role = Arrays.asList(
                new SimpleGrantedAuthority(customUser.getRole().toString())
        );

        return new User(customUser.getLogin(), customUser.getPassword(), role);
    }
}
