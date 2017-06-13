package com.gmonetix.slambook.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * @author Gmonetix
 */

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String firebaseToken;

    public User() {
    }

    public User(String uid, String email, String firebaseToken) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }
}