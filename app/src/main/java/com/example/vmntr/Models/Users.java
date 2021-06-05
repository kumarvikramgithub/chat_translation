package com.example.vmntr.Models;

public class Users {
    String name,aboutUs,email,profileImage,phoneNumber,userId,userLanguage;

    public Users(String name, String aboutUs, String email, String profileImage, String phoneNumber, String userId,String userLanguage) {
        this.name = name;
        this.aboutUs = aboutUs;
        this.email = email;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.userLanguage=userLanguage;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }
}
