package com.jwtpractice.web.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResponseController {

    @GetMapping("/response")
    public ResponseEntity<?> responseData() {
        return ResponseEntity.ok(true);
    }
}