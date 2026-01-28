package com.ecommerce.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@Slf4j
public class CredentialsController {

    @GetMapping("/credentials")
    public ResponseEntity<Map<String, Object>> getTestCredentials() {
        log.info("Credentials endpoint accessed - returning test credentials for development");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test credentials for development use only");
        response.put("users", List.of(
                Map.of(
                        "username", "admin",
                        "password", "admin123",
                        "role", "ADMIN, USER",
                        "description", "Administrator account with full access"
                ),
                Map.of(
                        "username", "user",
                        "password", "user123",
                        "role", "USER",
                        "description", "Regular user account"
                ),
                Map.of(
                        "username", "vendor",
                        "password", "vendor123",
                        "role", "VENDOR, USER",
                        "description", "Vendor account"
                )
        ));

        return ResponseEntity.ok(response);
    }
}
