package com.jwtpractice.service.auth;

import com.jwtpractice.service.PrincipalDetailsSerivce;
import com.jwtpractice.web.dto.TokenResponse;
import com.jwtpractice.web.dto.user.SignInUserRequestDto;
import com.jwtpractice.web.dto.user.SignupUserRequestDto;
import com.jwtpractice.domain.user.User;
import com.jwtpractice.domain.user.UserRepository;
import com.jwtpractice.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PrincipalDetailsSerivce principalDetailsSerivce;

    @Override
    public boolean insertUser(SignupUserRequestDto signupUserRequestDto) throws Exception {
        return userRepository.saveUser(signupUserRequestDto.toUserEntity()) > 0;
    }

    @Override
    public TokenResponse signInUser(SignInUserRequestDto signInUserRequestDto) throws Exception {
        User userEntity = null;
        String accessToken = null;
        String refreshToken = null;

        userEntity = userRepository.findUserByUserId(signInUserRequestDto.getUser_id());


        if(!bCryptPasswordEncoder.matches(signInUserRequestDto.getUser_password(), userEntity.getUser_password())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        accessToken = jwtTokenProvider.createAccessToken(userEntity.getUser_id());
        refreshToken = jwtTokenProvider.createRefreshToken(userEntity.getUser_id());


//        if(userRepository.findUserJwt(signInUserRequestDto.getUser_id()) != null) {
//            userRepository.updateUserJwt(signInUserRequestDto.getUser_id(), refreshToken);
//
//        }else {
//            userRepository.saveJwt(signInUserRequestDto.getUser_id(), refreshToken);
//
//        }

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}