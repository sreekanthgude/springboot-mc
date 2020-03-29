package com.example.admin.controller;


import com.example.admin.model.User;
import com.example.admin.payload.JwtResponse;
import com.example.admin.payload.LoginRequest;
import com.example.admin.security.CognitoService;
import com.example.admin.service.UserDetailsImpl;
import com.example.admin.utility.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthorizationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    CognitoService cognitoService;

    @PostMapping("/verifyUser")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {


        if(loginRequest !=null) {
            if(loginRequest.getUsername() == null || "".equals(loginRequest.getUsername())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("UserName required!!");
            };
            if(loginRequest.getPassword() == null || "".equals(loginRequest.getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Password required!!");
            };
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if(userDetails.getUsername() != null){
            // Checking user name in AWS cognito
            User user = cognitoService.getUser(userDetails.getUsername());

            if(user.getUsername() == null || "".equals(user.getUsername()) ){
                System.out.println("user not exists!!");
               /* Map<String, String> authParams = new HashMap<>();
                authParams.put("USERNAME",user.getUsername());
                authParams.put("PASSWORD",user.getPassword());*/
                cognitoService.storeCredentialsInAWSCognito(userDetails);
            }
        }
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                userDetails.getUsername(),
                jwt,
                roles));
    }

}
