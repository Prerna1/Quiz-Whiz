package com.gk.dev.quizwhiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AcceptRejectActivity extends AppCompatActivity {

    Profile profile;
    String fbId;
    DatabaseReference userStatus, cancellation;
    ValueEventListener cancellationListener;
    ChallengeDetails challengeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accept_reject);
        challengeDetails = (ChallengeDetails) Objects.requireNonNull(getIntent().getExtras()).get("challengeDetails");

        TextView tvChallengeDetails = findViewById(R.id.chdetails);
        ImageView ivChallengerImage = findViewById(R.id.chpic);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        profile = Profile.getCurrentProfile();
        fbId = profile.getId();

        cancellation = FirebaseDatabase.getInstance().getReference().child("Challenges").child(fbId);

        userStatus = databaseReference.child("UserDetails/" + fbId + "/status");
        userStatus.onDisconnect().setValue(0);
        userStatus.setValue(2);

        final String challengeInfo = challengeDetails.getName() + " challenged you in " + challengeDetails.getTopic();
        tvChallengeDetails.setText(challengeInfo);
        Picasso.with(getApplicationContext()).load(challengeDetails.getPicture()).into(ivChallengerImage);

        Button accept = findViewById(R.id.accept);
        Button reject = findViewById(R.id.reject);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancellation.child("isAccepted").setValue(1);
                cancellation.child("active").setValue(1);
                Intent intent = new Intent(AcceptRejectActivity.this, QuestionActivity.class);
                challengeDetails.setOpponentFbId(challengeDetails.getFbId());
                challengeDetails.setFbId(fbId);
                intent.putExtra("challengeDetails", challengeDetails);
                startActivity(intent);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancellation.removeValue();
                finish();
            }
        });
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
    protected void onStart() {
        super.onStart();

        cancellationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChallengeDetails challengeChanges = dataSnapshot.getValue(ChallengeDetails.class);
                if (challengeChanges == null) {
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        cancellation.addValueEventListener(cancellationListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancellation.removeEventListener(cancellationListener);
    }
}
