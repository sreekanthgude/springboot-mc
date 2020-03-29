package com.example.admin.security;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.directory.model.AuthenticationFailedException;
import com.example.admin.model.User;
import com.example.admin.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class CognitoServiceImpl implements CognitoService {

    @Autowired
    private AWSCognitoConfiguration cognitoConfiguration;

    @Autowired
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    @Override
    public AdminCreateUserRequest storeCredentialsInAWSCognito(UserDetailsImpl userDetails) {
        AdminCreateUserRequest createUserRequest = new AdminCreateUserRequest();
        createUserRequest.setUserPoolId(cognitoConfiguration.getUserPoolId());
        createUserRequest.setUsername(userDetails.getUsername());
        /*createUserRequest.setUserAttributes(user.getUser().getAttributes());*/
        // Set the email verified flag
        /*createUserRequest.getUserAttributes().add(new AttributeType().withName("email_verified").withValue("true"));*/
         //createUserRequest.
        createUserRequest.withTemporaryPassword(userDetails.getPassword());
        //createUserRequest.withDesiredDeliveryMediums(DeliveryMediumType.EMAIL);
        AdminCreateUserResult createUserResult = cognitoIdentityProvider.adminCreateUser(createUserRequest);
        if (createUserResult != null) {
            System.out.println("User Created!!!");
        }
        return createUserRequest;
    }

    @Override
    public User getUser(String username) {
        User user =  new User();
        try {
            AdminGetUserRequest getUserRequest = new AdminGetUserRequest()
                    .withUserPoolId(cognitoConfiguration.getUserPoolId()).withUsername(username);
            AdminGetUserResult userResult = cognitoIdentityProvider.adminGetUser(getUserRequest);
            if (userResult != null) {
                user.setUsername(userResult.getUsername());
                //user.setPassword(userResult.get);
            }

        } catch (UserNotFoundException exception) {
            String message = "Error in finding the User. ".concat(exception.getErrorMessage());
        }
        return user;
    }


}
