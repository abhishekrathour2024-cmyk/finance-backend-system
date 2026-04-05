package com.zorvyn.finance_backend.service;

import com.zorvyn.finance_backend.entity.User;
import com.zorvyn.finance_backend.exception.BadRequestException;
import com.zorvyn.finance_backend.exception.ResourceNotFoundException;
import com.zorvyn.finance_backend.entity.enums.Role;
import com.zorvyn.finance_backend.entity.enums.UserStatus;
import com.zorvyn.finance_backend.repository.FinancialRecordRepository;
import com.zorvyn.finance_backend.repository.UserRepository;
import com.zorvyn.finance_backend.util.RoleValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FinancialRecordRepository financialRecordRepository;

    public UserService(UserRepository userRepository,
                       FinancialRecordRepository financialRecordRepository) {
        this.userRepository = userRepository;
        this.financialRecordRepository = financialRecordRepository;
    }

    public User createUser(User user, Role role) {
        RoleValidator.adminOnly(role);

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (user.getRole() == null) {
            throw new BadRequestException("User role is required");
        }
        if (user.getStatus() == null) {
            throw new BadRequestException("User status is required");
        }
        if (userRepository.existsByEmailAndDeletedFalse(user.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers(Role role) {
        RoleValidator.adminOnly(role);
        return userRepository.findAll();
    }

    public User getUserById(Long id, Role role) {
        RoleValidator.adminOnly(role);

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }

        return optionalUser.get();
    }

    public User updateUser(Long id, User updatedUser, Role role) {
        RoleValidator.adminOnly(role);

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }

        User existingUser = optionalUser.get();

        if (updatedUser.getName() == null || updatedUser.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (updatedUser.getRole() == null) {
            throw new BadRequestException("User role is required");
        }
        if (updatedUser.getStatus() == null) {
            throw new BadRequestException("User status is required");
        }

        if (!existingUser.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmailAndDeletedFalse(updatedUser.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setStatus(updatedUser.getStatus());

        return userRepository.save(existingUser);
    }

    public User updateUserStatus(Long id, UserStatus status, Role role) {
        RoleValidator.adminOnly(role);

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = optionalUser.get();
        user.setStatus(status);
        return userRepository.save(user);
    }

    public void deleteUser(Long id, Role role) {
        RoleValidator.adminOnly(role);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }

        if (financialRecordRepository.existsByUserIdAndDeletedFalse(id)) {
            throw new BadRequestException("Cannot delete user because financial records are linked to this user");
        }

        userRepository.deleteById(id);
    }
}