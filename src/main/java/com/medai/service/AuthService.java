package com.medai.service;

import com.medai.dto.request.LoginRequest;
import com.medai.dto.request.RegisterRequest;
import com.medai.dto.response.AuthResponse;
import com.medai.exception.BadRequestException;
import com.medai.exception.UnauthorizedException;
import com.medai.model.entity.User;
import com.medai.model.enums.UserRole;
import com.medai.repository.UserRepository;
import com.medai.security.JwtTokenProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) throws BadRequestException {

        if (request.getRole() == UserRole.ADMIN) {
            throw new BadRequestException("Cannot register with ADMIN role");
        }

        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setEnabled(true);
        user.setAccountNonLocked(true);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        if (request.getRole() == UserRole.PATIENT) {
            patientService.createPatientProfile(savedUser.getId());
        } else if  (request.getRole() == UserRole.DOCTOR) {
            doctorService.createDoctorProfile(savedUser.getId());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.getEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }
        if (!user.getAccountNonLocked()) {
            throw new UnauthorizedException("Account is locked");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        log.info("User logged in successfully: {}", user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public AuthResponse refreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        String email = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.getEnabled() || !user.getAccountNonLocked()) {
            throw new UnauthorizedException("Account is disabled or locked");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newToken = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(
                newToken,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
