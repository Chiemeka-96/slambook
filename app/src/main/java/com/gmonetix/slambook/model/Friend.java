package com.gmonetix.slambook.model;

/**
 * @author Gmonetix
 */

public class Friend {

    private String name,friendUsername, email, number, description, image, phoneVisibility, username, dob, registeredAt, firebaseUid, friendAccepted, friendAcceptedOn, friendBlocked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhoneVisibility() {
        return phoneVisibility;
    }

    public void setPhoneVisibility(String phoneVisibility) {
        this.phoneVisibility = phoneVisibility;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendAccepted() {
        return friendAccepted;
    }

    public void setFriendAccepted(String friendAccepted) {
        this.friendAccepted = friendAccepted;
    }

    public String getFriendAcceptedOn() {
        return friendAcceptedOn;
    }

    public void setFriendAcceptedOn(String friendAcceptedOn) {
        this.friendAcceptedOn = friendAcceptedOn;
    }

    public String getFriendBlocked() {
        return friendBlocked;
    }

    public void setFriendBlocked(String friendBlocked) {
        this.friendBlocked = friendBlocked;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }
}
