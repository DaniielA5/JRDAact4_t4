package com.example.crudrapido.dto;

public record AuthResponse (
    String token, 
    String type, 
    long expiresIn
){}