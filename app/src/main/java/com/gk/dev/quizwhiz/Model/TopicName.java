package com.gk.dev.quizwhiz.Model;


import java.io.Serializable;

public class TopicName implements Serializable {
    public String topic;
    public int nQuestions;

    public TopicName() {
    }

    public int getnQuestions() {
        return nQuestions;
    }

    public void setnQuestions(int nQuestions) {
        this.nQuestions = nQuestions;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}