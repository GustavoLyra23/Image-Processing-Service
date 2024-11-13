package org.gustavolyra.image_process_service.services;

import lombok.extern.slf4j.Slf4j;
import org.gustavolyra.image_process_service.exceptions.DbConstraintException;
import org.gustavolyra.image_process_service.exceptions.UnathorizedException;
import org.gustavolyra.image_process_service.models.dto.auth.AuthResponseDto;
import org.gustavolyra.image_process_service.models.dto.auth.UserDataDto;
import org.gustavolyra.image_process_service.models.entities.Role;
import org.gustavolyra.image_process_service.models.entities.User;
import org.gustavolyra.image_process_service.repositories.RoleRepository;
import org.gustavolyra.image_process_service.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public AuthResponseDto login(UserDataDto userDataDto) {
        log.info("Entering in login method...");
        User user = userRepository.findByEmail(userDataDto.getUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        if (passwordEncoder.matches(userDataDto.getPassword(), user.getPassword())) {
            String accessToken = jwtService.GenerateToken(user.getUsername());
            log.info("Token generated with success for user {}", user.getUsername());
            return AuthResponseDto.builder().accessToken(accessToken).build();
        } else {
            log.error("Invalid credentials for user {}", user.getUsername());
            throw new UnathorizedException("Invalid credentials");
        }
    }

    @Transactional
    public void register(UserDataDto userDataDto) {
        log.info("Entering in register method...");
        var user = userRepository.findByEmail(userDataDto.getUsername());
        if (user.isPresent()) {
            log.error("User already exists");
            throw new DbConstraintException("User already exists");
        } else {

            var role = roleRepository.findByName("USER").orElseGet(() -> {
                log.info("Role not found, creating new role");
                var newRole = roleRepository.save(Role.builder()
                        .name("ROLE_USER")
                        .build());
                log.info("Role created with success");
                return newRole;
            });
            userRepository.save(User.builder()
                    .email(userDataDto.getUsername())
                    .password(passwordEncoder.encode(userDataDto.getPassword()))
                    .roles(Set.of(role))
                    .build());
            log.info("User {} registered with success", userDataDto.getUsername());
        }
    }
}
