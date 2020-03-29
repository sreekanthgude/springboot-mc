package com.example.admin.controller;


import com.example.admin.model.Role;
import com.example.admin.model.RoleType;
import com.example.admin.model.User;
import com.example.admin.payload.AddUserRequest;
import com.example.admin.repository.RoleRepository;
import com.example.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Author Sreekanth Gude
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest addUserRequest){

        if(addUserRequest !=null) {
            if(addUserRequest.getUsername() == null || "".equals(addUserRequest.getUsername())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("UserName required!!");
            };
            if(addUserRequest.getPassword() == null || "".equals(addUserRequest.getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Password required!!");
            };
        }

        Optional<User> userInfo = userRepository.findByUsername(addUserRequest.getUsername());
        if(userInfo != null && userInfo.isPresent()){
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("User Already found in system. Please use another username!!");
        }
        User user = new User(addUserRequest.getUsername(),
                encoder.encode(addUserRequest.getPassword()));
        Set<Role> roles = new HashSet<>();
        addUserRequest.getRole().forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                            .orElseThrow(() -> new RuntimeException("Admin Role is not found."));
                    roles.add(adminRole);

                    break;
                case "user":
                    Role modRole = roleRepository.findByName(RoleType.USER)
                            .orElseThrow(() -> new RuntimeException("User Role is not found."));
                    roles.add(modRole);

                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleType.DEFAULT)
                            .orElseThrow(() -> new RuntimeException("Default Role is not found."));
                    roles.add(userRole);
            }
        });
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(user.getUsername()+" "+"User Successfully created!");
    }


}
