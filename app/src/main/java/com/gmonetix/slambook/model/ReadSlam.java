package com.gmonetix.slambook.model;

/**
 * @author Gmonetix
 */

public class ReadSlam {

    private String name, fromUsername, image, updatedOn, sentOn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getSentOn() {
        return sentOn;
    }

    public void setSentOn(String sentOn) {
        this.sentOn = sentOn;
    }
}
