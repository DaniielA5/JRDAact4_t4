package com.example.crudrapido.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.crudrapido.dto.AuthResponse;
import com.example.crudrapido.dto.LoginRequest;
import com.example.crudrapido.dto.RegisterRequest;
import com.example.crudrapido.entity.Role;
import com.example.crudrapido.entity.User;
import com.example.crudrapido.exception.EmailAlreadyExistsException;
import com.example.crudrapido.repository.UserRepository;
import com.example.crudrapido.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {
    
private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con ese email");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return buildResponse(user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        //   BadCredentialsException si email o password son incorrectos
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        return buildResponse(request.email());
    }

    private AuthResponse buildResponse(String email) {
        String token = jwtService.generateToken(email);
        return new AuthResponse(token, "Bearer", jwtService.getExpiration());
    }
}