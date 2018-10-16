package com.gk.dev.quizwhiz.Model;

public class LeaderboardDetails {
    private String name;
    private int rank,numberOfWins;

    public LeaderboardDetails(){}

    public LeaderboardDetails(String name, int numberOfWins, int rank) {
        this.name = name;
        this.numberOfWins = numberOfWins;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
