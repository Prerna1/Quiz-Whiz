package com.gk.dev.quizwhiz.Model;

import java.io.Serializable;

public class FriendDetails implements Serializable {
    public String name, pictureURL, fbId;
    public int status;

    public FriendDetails() {

    }

    public FriendDetails(String name, String pictureURL, int status, String fbId) {
        this.name = name;
        this.pictureURL = pictureURL;
        this.status = status;
        this.fbId = fbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
}
