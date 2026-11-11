package com.example.crudrapido.dto;

public record StudentResponse (
        Long studentId,
        String firstName,
        String lastName,
        String email
) {}