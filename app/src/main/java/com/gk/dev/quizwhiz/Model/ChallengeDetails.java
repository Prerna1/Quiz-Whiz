package com.gk.dev.quizwhiz.Model;

public class ChallengeDetails {
    public String name, picture, fbId, topic;

    public ChallengeDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getFbId() {
        return fbId;
    }

    public ChallengeDetails(String name, String picture, String fbId, String topic) {
        this.name = name;
        this.picture = picture;
        this.fbId = fbId;
        this.topic = topic;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
