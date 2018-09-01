package com.gk.dev.quizwhiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.Profile;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    ChallengeDetails challengeDetails;
    TextView user1scoreTextView , user2scoreTextView , user1 , user2, matchStatus;
    String fbId;
    String user1Score,user2Score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final Profile profile = Profile.getCurrentProfile();
        fbId = profile.getId();
        matchStatus = findViewById(R.id.match_status);
        user1 = findViewById(R.id.user1);
        user2=findViewById(R.id.user2);
        user1scoreTextView=findViewById(R.id.score1);
        user2scoreTextView=findViewById(R.id.score2);
        challengeDetails = (ChallengeDetails) Objects.requireNonNull(getIntent().getExtras()).get("challengeDetails");
        user1Score =getIntent().getExtras().getString("user1Score");
        user2Score = getIntent().getExtras().getString("user2Score");
        user1scoreTextView.setText(user1Score);
        user2scoreTextView.setText(user2Score);
        user2.setText(challengeDetails.getName());
        user1.setText(profile.getName());
        if(Integer.parseInt(user1Score)>Integer.parseInt(user2Score)){
            matchStatus.setText("YOU WON");
        }
        else if(Integer.parseInt(user1Score)<Integer.parseInt(user2Score)){
            matchStatus.setText("YOU LOSE");
        }
        else{
            matchStatus.setText("TIE");
        }

    }
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        this.startActivity(new Intent(ResultActivity.this,SelectTopicActivity.class));

        return;
    }
}
