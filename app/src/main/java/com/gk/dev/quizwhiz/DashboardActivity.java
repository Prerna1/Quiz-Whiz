package com.gk.dev.quizwhiz;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.gk.dev.quizwhiz.Model.ChallengerInformation;
import com.gk.dev.quizwhiz.Model.MatchStatistics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {
    ValueEventListener l1;
    private FirebaseAuth mAuth;
    private String fbid;
    private DatabaseReference databaseReference,challenged, userStatus;
    TextView matchesWon ,matchesLost , matchesDraw,userName;
    private Button challengeButton;
    MatchStatistics matchStatistics;
    ChallengerInformation challengerInformation;
    private int won , lost , draw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        matchesWon =findViewById(R.id.matches_won);
        matchesDraw = findViewById(R.id.matches_draw);
        matchesLost= findViewById(R.id.matches_lost);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Profile profile = Profile.getCurrentProfile();
        fbid = profile.getId();
        userStatus = databaseReference.child("Users/"+fbid+"/status");
        userStatus.onDisconnect().setValue("0");
        userStatus.setValue("1");
        Picasso.with(getApplicationContext()).load(Profile.getCurrentProfile().getProfilePictureUri(200,200)).into((ImageView)findViewById(R.id.profilepic));
        databaseReference.child("Users/"+fbid+"/picture").setValue(Profile.getCurrentProfile().getProfilePictureUri(200,200).toString());
        userName = findViewById(R.id.user_name);
        userName.setText(Profile.getCurrentProfile().getFirstName());
        databaseReference.child("Users/"+fbid+"/name").setValue(Profile.getCurrentProfile().getFirstName());

        challengeButton =findViewById(R.id.challenge_button);

        challengeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(DashboardActivity.this,SelectTopicActivity.class));
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

                userStatus.setValue("0");
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(DashboardActivity.this,RedirectActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            userStatus.setValue("0");
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatus.onDisconnect().setValue("0");
        userStatus.setValue("1");
        DatabaseReference stats =databaseReference.child("Users/"+fbid);;
        stats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                matchStatistics = new MatchStatistics();
                matchStatistics = dataSnapshot.getValue(MatchStatistics.class);
                Log.d("prerna",matchStatistics.toString());
                if(matchStatistics==null){
                    won=lost=draw=0;
                }
                else{
                    won=matchStatistics.won;
                    lost=matchStatistics.lost;
                    draw=matchStatistics.draw;
                }
                matchesWon.setText("Won: "+Integer.toString(won));
                matchesDraw.setText("Draw: "+Integer.toString(draw));
                matchesLost.setText("Lost: "+Integer.toString(lost));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        userStatus.onDisconnect().setValue("0");
        userStatus.setValue("1");
        super.onStart();

        challenged = FirebaseDatabase.getInstance().getReference().child("challenges").child(fbid);
        l1=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                challengerInformation = dataSnapshot.getValue(ChallengerInformation.class);
                if(challengerInformation!=null) {
                    String pic = challengerInformation.picture;
                    if(pic!=null) {
                        if (pic.equals("0")) {

                        } else {
                            if(challengerInformation.fid!=null){
//                                DatabaseReference previous = FirebaseDatabase.getInstance().getReference().child("acceptreject").child(k.fid);
//                                previous.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        if(challengerInformation.status!=null){
//                                            if((challengerInformation.status).equals("0")){
                                           startActivity(new Intent(DashboardActivity.this, AcceptRejectActivity.class));
//
//                                            }
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        challenged.addValueEventListener(l1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        challenged.removeEventListener(l1);
    }
}
