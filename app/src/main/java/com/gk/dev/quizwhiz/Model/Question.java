package com.gk.dev.quizwhiz.Model;

/**
 * Created by Suneja's on 03-01-2018.
 */

public class Question {
    private String questionText;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String correctAnswer;

    public Question() {

    }

    public String getQuestionText() {
        return questionText;
    }

    public String getChoice1() {
        return choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public String getcorrectAnswer() {
        return correctAnswer;
    }
}