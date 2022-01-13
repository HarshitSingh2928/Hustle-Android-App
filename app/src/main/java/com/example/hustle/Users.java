package com.example.hustle;

public class Users {
    String name,mail,password,userId,image,userTask;

    public Users(String name, String mail, String password, String userId, String image, String userTask) {
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.image = image;
        this.userTask = userTask;
    }

    public Users(String name, String mail, String password,String image) {
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.image=image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserTask() {
        return userTask;
    }

    public void setUserTask(String userTask) {
        this.userTask = userTask;
    }
}
