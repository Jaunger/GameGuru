package com.daniel.gameguru.Entities;

public class Guide {
    private String id;
    private String title;
    private String content;
    private String gameId;
    private String gameName;
    private String authorId;
    private String imageUrl;
    private long timestamp;
    private String isPublished;

    public Guide() {

    }

    public Guide(String title, String id, String content, String gameId, String gameName, String authorId, String imageUrl, long timestamp, String isPublished) {
        this.title = title;
        this.id = id;
        this.content = content;
        this.gameId = gameId;
        this.gameName = gameName;
        this.authorId = authorId;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.isPublished = isPublished;
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

    public String getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(String isPublished) {
        this.isPublished = isPublished;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getGameId() {
        return gameId;
    }

    public Guide setGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }
}
