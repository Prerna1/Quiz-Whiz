package com.gk.dev.quizwhiz.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ChallengeDetails implements Serializable {
    public String name, picture, fbId, topic;
    public int isAccepted;
    public ArrayList<Integer> numbers;

    public ChallengeDetails() {
        isAccepted = 0;
    }

    public ChallengeDetails(String name, String picture, String fbId, String topic) {
        this.name = name;
        this.picture = picture;
        this.fbId = fbId;
        this.topic = topic;
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

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }

    public ArrayList<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(ArrayList<Integer> numbers) {
        this.numbers = numbers;
    }

}
