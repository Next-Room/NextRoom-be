package com.nextroom.oescape.service;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nextroom.oescape.domain.Shop;
import com.nextroom.oescape.repository.ShopRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ShopRepository shopRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return shopRepository.findByAdminCode(username)
            .map(this::createUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Shop shop) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(shop.getAuthority().toString());

        return new User(
            String.valueOf(shop.getId()),
            shop.getPassword(),
            Collections.singleton(grantedAuthority)
        );
    }
}