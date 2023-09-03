package com.example.fitnessmedia_congressionalappchallenge;

public class User {
    String name,age,email,password,ProfilePic,privateStatus;
    Boolean loginByPassword;

    public Boolean getLoginByPassword() {
        return loginByPassword;
    }

    public User(String name, String age, String email, String password, String profilePic, String privateStatus, Boolean loginByPassword) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        ProfilePic = profilePic;
        this.privateStatus = privateStatus;
        this.loginByPassword = loginByPassword;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return ProfilePic;
    }

    public String getPrivateStatus() {
        return privateStatus;
    }
}
