package com.gk.dev.quizwhiz.Model;

/**
 * Created by Suneja's on 13-08-2018.
 */

public class MatchStatistics {
    public int won;
    public int draw;
    public int lost;
    public MatchStatistics(){}
    public MatchStatistics (int won,int draw,int lost){
        this.won  =won;
        this.draw = draw;
        this.lost = lost;
    }
}
