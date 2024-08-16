package com.daniel.gameguru.Entities;

public class Guide {
    private String id;
    private String title;
    private String imageUrl;
    private String gameName;
    private String category;
    private String content;
    private String authorId;
    private boolean isPublished;
    private long timestamp;

    // Empty constructor for Firestore
    public Guide() {
    }

    // Constructor with parameters
    public Guide(String id, String title, String imageUrl, String gameName, String category, String content, String authorId, boolean isPublished, long timestamp) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.gameName = gameName;
        this.category = category;
        this.content = content;
        this.authorId = authorId;
        this.isPublished = isPublished;
        this.timestamp = timestamp;
    }

    // Getters and setters for the ID
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
