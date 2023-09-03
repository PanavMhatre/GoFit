package com.example.fitnessmedia_congressionalappchallenge;

public class UserDataRecView {
    String name,email,image,date;


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        if(image.equals("")){
            return null;
        }
        return image;
    }

    public String getDate() {
        return date;
    }
}
