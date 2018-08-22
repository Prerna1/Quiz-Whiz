package com.gk.dev.quizwhiz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.gk.dev.quizwhiz.Model.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {
    ValueEventListener challengeListener;
    TextView matchesWon, matchesLost, matchesDraw, userName;
    UserDetails userDetails;
    ChallengeDetails challengerInformation;
    FirebaseAuth mAuth;
    String fbId;
    DatabaseReference challenges, userStatus;
    int won, lost, draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.user_name);
        matchesWon = findViewById(R.id.matches_won);
        matchesDraw = findViewById(R.id.matches_draw);
        matchesLost = findViewById(R.id.matches_lost);
        Button challengeButton = findViewById(R.id.challenge_button);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Profile profile = Profile.getCurrentProfile();
        fbId = profile.getId();

        userStatus = databaseReference.child("UserDetails/" + fbId + "/status");
        userStatus.onDisconnect().setValue(0);
        userStatus.setValue(1);

        Picasso.with(getApplicationContext()).load(profile.getProfilePictureUri(200, 200)).into((ImageView) findViewById(R.id.profilepic));
        userName.setText(profile.getFirstName());
        DatabaseReference stats = databaseReference.child("UserDetails/" + fbId);
        stats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(UserDetails.class);
                assert userDetails != null;
                won = userDetails.won;
                lost = userDetails.lost;
                draw = userDetails.draw;

                String wonText = "Won: " + Integer.toString(won);
                String drawText = "Draw: " + Integer.toString(draw);
                String lostText = "Lost: " + Integer.toString(lost);

                matchesWon.setText(wonText);
                matchesDraw.setText(drawText);
                matchesLost.setText(lostText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        challengeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(DashboardActivity.this, SelectTopicActivity.class));
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout1:
                userStatus.setValue(0);
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(DashboardActivity.this, RedirectActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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
                if (challengerInformation != null && !challengerInformation.getTopic().equals("null")) {
                    Intent intent = new Intent(DashboardActivity.this, AcceptRejectActivity.class);
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
