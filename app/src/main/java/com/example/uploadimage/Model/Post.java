package com.example.uploadimage.Model;

public class Post {

    private String postId;
    private String postImage;
    private String title;
    private String date;
    private String description;
    private String publisher;

    public Post(String postId, String postImage, String description, String title, String date, String publisher) {
        this.postId = postId;
        this.postImage = postImage;
        this.title = title;
        this.date = date;
        this.description = description;
        this.publisher = publisher;
    }

    public Post(){

    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
