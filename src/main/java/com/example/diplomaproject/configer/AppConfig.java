package com.example.diplomaproject.configer;

import com.example.diplomaproject.Roles;
import com.example.diplomaproject.services.CustomUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner admin(final CustomUserService userService,
                                   final PasswordEncoder encoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

                userService.addAdmin("admin", encoder.encode("Kang1992"), "daser1992@gmail.com", Roles.Admin);


            }
        };
    }

}
