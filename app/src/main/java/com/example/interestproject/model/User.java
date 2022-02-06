package com.example.interestproject.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String description;
    private String lastname;
    private String firstname;
    private String status;
    private String search;
    private String interests;

    public User(String id, String username, String lastname, String firstname, String description, String imageURL, String status, String interests){
        this.id = id;
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.description = description;
        this.imageURL = imageURL;
        this.status = status;
        this.search = username.toLowerCase();
        this.interests = interests;
    }


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public HashMap<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("id", id);
        result.put("username", username);
        result.put("imageURL", imageURL);
        result.put("description", description);
        result.put("lastname", lastname);
        result.put("firstname", firstname);
        result.put("status", status);
        result.put("search", search);
        result.put("interests", interests);

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


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSearch(String search) {
        this.search = search;
    }
    public String getSearch() {
        return this.search;
    }


    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public List<String> getInterestsList() {
        String[] interests = this.interests.split(",");
        return new ArrayList<String>(Arrays.asList(interests));
    }

}
