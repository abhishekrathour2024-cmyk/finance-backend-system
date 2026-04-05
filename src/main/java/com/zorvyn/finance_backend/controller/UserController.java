package com.zorvyn.finance_backend.controller;

import com.zorvyn.finance_backend.entity.User;
import com.zorvyn.finance_backend.entity.enums.Role;
import com.zorvyn.finance_backend.entity.enums.UserStatus;
import com.zorvyn.finance_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user,
                                           @RequestParam("role") Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user, role));
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam("role") Role role) {
        return ResponseEntity.ok(userService.getAllUsers(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id,
                                        @RequestParam("role") Role role) {
        return ResponseEntity.ok(userService.getUserById(id, role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User user,
                                           @RequestParam("role") Role role) {
        return ResponseEntity.ok(userService.updateUser(id, user, role));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id,
                                                 @RequestParam UserStatus status,
                                                 @RequestParam("role") Role role) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestParam("role") Role role) {
        userService.deleteUser(id, role);
        return ResponseEntity.noContent().build();
    }
}
