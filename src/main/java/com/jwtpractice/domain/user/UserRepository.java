package com.jwtpractice.domain.user;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository {
    public int saveUser(User user) throws Exception;
    public int saveJwt(String user_id, String refresh_token) throws Exception;
    public User findUserByUserId(String user_id);
    public String findUserJwt(String user_id) throws Exception;
    public int updateUserJwt(String user_id, String refresh_token) throws Exception;
}