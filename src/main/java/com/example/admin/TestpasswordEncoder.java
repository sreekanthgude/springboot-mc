package com.example.admin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestpasswordEncoder {

    public static void main(String args[]){
        String encoded = new BCryptPasswordEncoder().encode("12345");
        System.out.println("encoded--->"+encoded);
    }
}
