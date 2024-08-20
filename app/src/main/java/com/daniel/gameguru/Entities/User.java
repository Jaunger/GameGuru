package com.daniel.gameguru.Entities;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String username;
    private String name;
    private String email;
    private String description;
    private String image;
    private HashMap<String, Integer> following;
    private Map<String, Integer> recentGuides = new HashMap<>();

    public User() {
    }

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public User setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public Map<String, Integer> getRecentGuides() {
        return recentGuides;
    }

    public User setRecentGuides(Map<String, Integer> recentGuides) {
        this.recentGuides = recentGuides;
        return this;
    }

    public void addRecentlyViewedItem(String itemId, int timestamp) {
        this.recentGuides.put(itemId, timestamp);
    }

    public HashMap<String, Integer> getFollowing() {
        return following;
    }

    public User setFollowing(HashMap<String, Integer> following) {
        this.following = following;
        return this;
    }
}
