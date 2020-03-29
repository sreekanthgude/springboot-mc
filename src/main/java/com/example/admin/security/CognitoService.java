package com.example.admin.security;

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.example.admin.model.User;
import com.example.admin.service.UserDetailsImpl;

import java.util.Map;

public interface CognitoService {

    public AdminCreateUserRequest storeCredentialsInAWSCognito(UserDetailsImpl userDetails);
    public User getUser(String username);
}
