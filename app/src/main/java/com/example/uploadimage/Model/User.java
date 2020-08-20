package com.example.uploadimage.Model;

public class User {

    private String id;
    private String username;
    private String imageurl;
    private String bio;
    private String nation;

    public User(String id, String username, String imageurl, String bio, String nation) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.bio = bio;
        this.nation = nation;
    }

    public User() {

    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
