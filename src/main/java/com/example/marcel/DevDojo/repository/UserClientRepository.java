package com.example.marcel.DevDojo.repository;

import com.example.marcel.DevDojo.model.UserClient;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserClientRepository extends PagingAndSortingRepository<UserClient, Long> {
    Optional<UserClient> findByUsername(String username);
}
