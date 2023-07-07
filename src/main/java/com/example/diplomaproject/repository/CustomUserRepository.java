package com.example.diplomaproject.repository;

import com.example.diplomaproject.entity.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {
    @Query("SELECT c FROM CustomUser c WHERE c.login = :login")
    CustomUser findByLogin(@Param("login") String login);

    @Query("SELECT c FROM CustomUser c WHERE c.role = 'User'")
    List<CustomUser> findAllUsers();

}
