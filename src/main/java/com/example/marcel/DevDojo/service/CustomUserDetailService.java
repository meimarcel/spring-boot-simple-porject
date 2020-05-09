package com.example.marcel.DevDojo.service;

import com.example.marcel.DevDojo.model.UserClient;
import com.example.marcel.DevDojo.repository.UserClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserClientRepository userClientRepository;

    @Autowired
    public CustomUserDetailService(UserClientRepository userClientRepository) {
        this.userClientRepository = userClientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserClient userClient = userClientRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found"));
        List<GrantedAuthority> authorityListAdmin = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
        List<GrantedAuthority> authorityListUser = AuthorityUtils.createAuthorityList("ROLE_USER");

        return new User(userClient.getUsername(), userClient.getPassword(), userClient.isAdmin() ? authorityListAdmin : authorityListUser);
    }
}
