package com.jwtpractice.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private int id;
    private String user_name;
    private String user_id;
    private String user_password;
    private String user_roles;
    private LocalDateTime create_date;
    private LocalDateTime update_date;

    private String refresh_token;

    public List<String> getUserRoles() {
        if(user_roles == null || user_roles.isBlank()) {
            return new ArrayList<String>();
        }
        return Arrays.asList(user_roles.replaceAll(" ", "").split((",")));
    }
}