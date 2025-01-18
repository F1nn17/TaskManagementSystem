package ru.shiraku.taskmanagementsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.shiraku.taskmanagementsystem.exceptions.InvalidCredentialsException;
import ru.shiraku.taskmanagementsystem.exceptions.ShortPasswordException;
import ru.shiraku.taskmanagementsystem.exceptions.UserAlreadyExistsException;
import ru.shiraku.taskmanagementsystem.exceptions.NotFound;
import ru.shiraku.taskmanagementsystem.model.Role;
import ru.shiraku.taskmanagementsystem.model.dto.LoginRequest;
import ru.shiraku.taskmanagementsystem.model.dto.RegisterAdminRequest;
import ru.shiraku.taskmanagementsystem.model.dto.RegisterRequest;
import ru.shiraku.taskmanagementsystem.model.dto.UserResponse;
import ru.shiraku.taskmanagementsystem.model.entity.UserEntity;
import ru.shiraku.taskmanagementsystem.repository.UserRepository;
import ru.shiraku.taskmanagementsystem.utils.JWTUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;

    public UserService(JWTUtils jwtUtils, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (existByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("The user with this email already exists.");
        }
        if (request.getPassword().length() < 8) {
            throw new ShortPasswordException("Password must be at least 8 characters.");
        }
        UserEntity user = createUserFromRequest(request);
        userRepository.save(user);
        return new UserResponse(user.getName(), user.getLastName(), user.getEmail(), user.getRole());
    }

    @Transactional
    public void registerAdmin(RegisterAdminRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new NotFound("The user with this email does not exist.");
        }
        UserEntity user = userOpt.get();
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    public String loginUser(LoginRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new NotFound("User not found.");
        }
        UserEntity user = userOpt.get();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }
        return jwtUtils.generateToken(user.getId(), user.getEmail(), user.getRole());
    }

    public UserEntity findByEmail(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new NotFound("User not found.");
        }
        return userOpt.get();
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                                user.getName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getRole()
                        )
                )
                .collect(Collectors.toList());
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserEntity createUserFromRequest(RegisterRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        return user;
    }
}
