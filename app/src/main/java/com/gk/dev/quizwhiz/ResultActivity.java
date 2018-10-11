package com.gk.dev.quizwhiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.Profile;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    ChallengeDetails challengeDetails, challengerInformation;
    TextView user1scoreTextView, user2scoreTextView, user1, user2, matchStatus;
    String fbId;
    ValueEventListener challengeListener;
    DatabaseReference challenges, userStatus;
    String user1Score, user2Score;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final Profile profile = Profile.getCurrentProfile();
        fbId = profile.getId();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        userStatus = databaseReference.child("UserDetails/" + fbId + "/status");
        userStatus.onDisconnect().setValue(0);
        userStatus.setValue(1);

        matchStatus = findViewById(R.id.match_status);
        user1 = findViewById(R.id.user1);
        user2 = findViewById(R.id.user2);
        user1scoreTextView = findViewById(R.id.score1);
        user2scoreTextView = findViewById(R.id.score2);
        challengeDetails = (ChallengeDetails) Objects.requireNonNull(getIntent().getExtras()).get("challengeDetails");
        user1Score = getIntent().getExtras().getString("user1Score");
        user2Score = getIntent().getExtras().getString("user2Score");
        user1scoreTextView.setText(user1Score);
        user2scoreTextView.setText(user2Score);
        user2.setText(challengeDetails.getName());
        user1.setText(profile.getName());
        if (Integer.parseInt(user1Score) > Integer.parseInt(user2Score)) {
            matchStatus.setText("YOU WON");
        } else if (Integer.parseInt(user1Score) < Integer.parseInt(user2Score)) {
            matchStatus.setText("YOU LOSE");
        } else {
            matchStatus.setText("TIE");
        }

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this, DashboardActivity.class));
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        userStatus.setValue(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatus.setValue(1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        challenges = FirebaseDatabase.getInstance().getReference().child("Challenges").child(fbId);
        challengeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                challengerInformation = dataSnapshot.getValue(ChallengeDetails.class);
                if (challengerInformation != null && !challengerInformation.getTopic().equals("null") && challengerInformation.active == 0) {
                    Intent intent = new Intent(ResultActivity.this, AcceptRejectActivity.class);
                    intent.putExtra("challengeDetails", challengerInformation);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        challenges.addValueEventListener(challengeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        challenges.removeEventListener(challengeListener);
    }
}
