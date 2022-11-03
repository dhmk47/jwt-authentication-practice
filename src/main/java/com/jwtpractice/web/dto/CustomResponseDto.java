package com.jwtpractice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponseDto<T> {
    private int code;
    private String message;
    private T data;
}