package com.example.fix4you_api.Service.Login.DTOs;

import com.example.fix4you_api.Data.Enums.EnumUserType;

public class ResponseLogin {
    private String token;
    private String userId;
    private EnumUserType userType;

    public ResponseLogin(String token, String userId, EnumUserType userType) {
        this.token = token;
        this.userId = userId;
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public EnumUserType getUserType() {
        return userType;
    }
}