package com.example.interestproject.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String description;
    private String prenom;
    private String status;

    public User(String id, String username, String prenom, String description, String imageURL, String status){
        this.id = id;
        this.username = username;
        this.prenom = prenom;
        this.description = description;
        this.imageURL = imageURL;
        this.status = status;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("id", id);
        result.put("username", username);
        result.put("imageURL", imageURL);
        result.put("description", description);
        result.put("prenom", prenom);

        return result;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
