package com.jwtpractice.service;

import com.jwtpractice.domain.user.User;
import com.jwtpractice.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsSerivce implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserId(username);

        if(user == null) {
            throw new UsernameNotFoundException("정보를 올바르게 입력해주세요.");
        }

        System.out.println(user);

        return new PrincipalDetails(user);
    }
}