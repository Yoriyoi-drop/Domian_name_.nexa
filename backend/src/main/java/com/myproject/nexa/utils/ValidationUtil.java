package com.myproject.nexa.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        
        // At least 3 characters, alphanumeric and underscore only
        String usernameRegex = "^[a-zA-Z0-9_]{3,50}$";
        return username.matches(usernameRegex);
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        
        // At least 6 characters
        return password.length() >= 6;
    }
}