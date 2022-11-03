package com.jwtpractice.web.dto.user;

import com.jwtpractice.domain.user.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
public class SignupUserRequestDto {
    private String userName;
    private String userId;
    private String userPassword;

    public User toUserEntity() {
        return User.builder()
                .user_name(userName)
                .user_id(userId)
                .user_password(new BCryptPasswordEncoder().encode(userPassword))
                .user_roles("ROLE_USER")
                .build();
    }
}