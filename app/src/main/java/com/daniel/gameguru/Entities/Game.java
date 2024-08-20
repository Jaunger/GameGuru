package com.daniel.gameguru.Entities;

import java.util.HashMap;
import java.util.List;

public class Game {
    private String id;
    private String title;
    private String developer;
    private String publisher;
    private String releaseDate;
    private List<String> genres; // This can be a list if needed, but as per your previous code, it's a single string
    private String imageUrl;
    private String description;
    private String lowercasename;
    private HashMap<String,Integer> guideIds;

    public Game() {}

    public Game(String title, String developer, String publisher, String releaseDate, List<String> genre, String imageUrl, String description) {
        this.title = title;
        this.developer = developer;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.genres = genre;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenre(List<String> genres) {
        this.genres = genres;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Integer> getGuideIds() {
        return guideIds;
    }

    public void setGuideIds(HashMap<String, Integer> guideIds) {
        this.guideIds = guideIds;
    }

    public Game addGuideId(String guideId) {
        this.guideIds.put(guideId, 1);
        return this;
    }

    public String getLowercasename() {
        return lowercasename;
    }

    public Game setLowercasename(String lowercasename) {
        this.lowercasename = lowercasename;
        return this;
    }
}
