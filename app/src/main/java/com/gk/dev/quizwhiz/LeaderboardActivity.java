package com.gk.dev.quizwhiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.gk.dev.quizwhiz.Adapters.LeaderboardAdapter;
import com.gk.dev.quizwhiz.Model.LeaderboardDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class LeaderboardActivity extends AppCompatActivity {
    int total;
    ArrayList<LeaderboardDetails> friends = new ArrayList<>();
    private RecyclerView recyclerView;
    private LeaderboardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LeaderboardDetails temp = (LeaderboardDetails) Objects.requireNonNull(getIntent().getExtras()).get("self");
        friends.add(temp);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {

                    @Override
                    public void onCompleted(
                            final JSONArray jsonArray,
                            GraphResponse response) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                final JSONObject friend = jsonArray.getJSONObject(i);
                                final String name = friend.getString("name");
                                String picURL = friend.getJSONObject("picture").getJSONObject("data").getString("url");
                                String fbId = friend.getString("id");
                                FirebaseDatabase.getInstance().getReference().child("UserDetails").child(fbId).child("won").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int wins = dataSnapshot.getValue(Integer.class);
                                        LeaderboardDetails friendDetails = new LeaderboardDetails(name, wins, 0);
                                        friends.add(friendDetails);
                                        total++;
                                        if (total == jsonArray.length()) {
                                            Collections.sort(friends, new Comparator<LeaderboardDetails>() {
                                                @Override
                                                public int compare(LeaderboardDetails t1, LeaderboardDetails t2) {
                                                    return t2.getNumberOfWins() - t1.getNumberOfWins();
                                                }
                                            });
                                            for(int i = 0;i<friends.size();i++){
                                                friends.get(i).setRank(i+1);
                                            }
                                            mAdapter = new LeaderboardAdapter(friends);
                                            recyclerView.setAdapter(mAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();

    }
}
