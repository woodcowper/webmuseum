package com.webmuseum.museum.utils;

public final class EmailHelper {

    public static String getNewPasswordTemplate(String username, String password){
        return username + ", your new password is: " + password;
    }
    
}
