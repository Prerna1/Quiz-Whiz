package com.gk.dev.quizwhiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.Profile;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.gk.dev.quizwhiz.Model.FriendDetails;
import com.gk.dev.quizwhiz.Model.TopicName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class WaitForResponseActivity extends AppCompatActivity {

    ValueEventListener responseListener;
    String fbId;
    DatabaseReference userStatus;
    TopicName selectedTopic;
    FriendDetails selectedFriend;
    DatabaseReference challengeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_response);

        selectedTopic = (TopicName) Objects.requireNonNull(getIntent().getExtras()).get("selectedTopic");
        selectedFriend = (FriendDetails) getIntent().getExtras().get("selectedFriend");

        challengeReference = FirebaseDatabase.getInstance().getReference().child("Challenges").child(selectedFriend.getFbId());

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Profile profile = Profile.getCurrentProfile();
        fbId = profile.getId();

        userStatus = databaseReference.child("UserDetails/" + fbId + "/status");
        userStatus.onDisconnect().setValue(0);
        userStatus.setValue(2);

        Button cancel = findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                challengeReference.removeValue();
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        responseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChallengeDetails challengeDetails = dataSnapshot.getValue(ChallengeDetails.class);
                if (challengeDetails == null) {
                    finish();
                } else if (challengeDetails.getIsAccepted() == 1) {
                    Intent intent = new Intent(WaitForResponseActivity.this, QuestionActivity.class);
                    ChallengeDetails challengeDetails1 = new ChallengeDetails();
                    challengeDetails1.setFbId(selectedFriend.getFbId());
                    challengeDetails1.setName(selectedFriend.getName());
                    challengeDetails1.setPicture(selectedFriend.getPictureURL());
                    challengeDetails1.setTopic(selectedTopic.getTopic());
                    challengeDetails1.setNumbers(challengeDetails.getNumbers());
                    intent.putExtra("challengeDetails", challengeDetails1);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        challengeReference.addValueEventListener(responseListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        userStatus.setValue(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatus.setValue(2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        challengeReference.removeEventListener(responseListener);
    }
}
