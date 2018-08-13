package com.gk.dev.quizwhiz.Model;

/**
 * Created by Suneja's on 13-08-2018.
 */
public class ChallengerInformation {
    public String name,picture,fid,status;
    public ChallengerInformation(){

    }
    public ChallengerInformation(String name,String picture, String fid , String status){
        this.name=name;
        this.picture=picture;
        this.fid=fid;
        this.status = status;
    }

}
