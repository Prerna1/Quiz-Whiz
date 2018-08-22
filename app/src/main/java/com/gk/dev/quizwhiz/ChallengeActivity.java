package com.gk.dev.quizwhiz;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.gk.dev.quizwhiz.Adapters.ChallengeAdapter;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.gk.dev.quizwhiz.Model.FriendDetails;
import com.gk.dev.quizwhiz.Model.TopicName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

public class ChallengeActivity extends AppCompatActivity implements ChallengeAdapter.ListItemClickListener {

    boolean friendsRetrieved;
    ValueEventListener challengeListener;
    DatabaseReference challenges, userStatus;
    String fbId;
    TopicName selectedTopic;
    List<FriendDetails> friends;
    RecyclerView recyclerView;
    ChallengeDetails challengerInformation;
    Profile profile;
    SearchView searchView;
    ChallengeAdapter mAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        selectedTopic = (TopicName) Objects.requireNonNull(getIntent().getExtras()).get("selectedTopic");
        progressBar = findViewById(R.id.pb_challenge);

        friendsRetrieved = false;

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        profile = Profile.getCurrentProfile();
        fbId = profile.getId();

        recyclerView = findViewById(R.id.friends_recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        userStatus = databaseReference.child("UserDetails/" + fbId + "/status");
        userStatus.onDisconnect().setValue(0);
        userStatus.setValue(1);

        friends = new ArrayList<>();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {

                    @Override
                    public void onCompleted(
                            JSONArray jsonArray,
                            GraphResponse response) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject friend = jsonArray.getJSONObject(i);
                                String name = friend.getString("name");
                                String picURL = friend.getJSONObject("picture").getJSONObject("data").getString("url");
                                String fbId = friend.getString("id");
                                FriendDetails friendDetails = new FriendDetails(name, picURL, 0, fbId);
                                friends.add(friendDetails);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        Collections.sort(friends, new Comparator<FriendDetails>() {
                            @Override
                            public int compare(FriendDetails t1, FriendDetails t2) {
                                return t1.getName().compareToIgnoreCase(t2.getName());
                            }
                        });
                        friendsRetrieved = true;

                        mAdapter = new ChallengeAdapter(getApplicationContext(), friends, ChallengeActivity.this);
                        recyclerView.setAdapter(mAdapter);
                        progressBar.setVisibility(View.GONE);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.challenge_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (friendsRetrieved)
                    mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (friendsRetrieved)
                    mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
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
                    Intent intent = new Intent(ChallengeActivity.this, AcceptRejectActivity.class);
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

    @Override
    public void onListItemClick(FriendDetails selectedFriend) {
        if (selectedFriend.getStatus() == 0) {
            Toast.makeText(this, "The person is currently Offline!", Toast.LENGTH_SHORT).show();
        } else if (selectedFriend.getStatus() == 2) {
            Toast.makeText(this, "The person is currently Busy!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(ChallengeActivity.this, WaitForResponseActivity.class);
            ChallengeDetails challengeDetails = new ChallengeDetails(profile.getName(), profile.getProfilePictureUri(1000, 1000).toString(), fbId, selectedTopic.getTopic());
            ArrayList<Integer> numbers = new ArrayList<>(), subNumbers;
            for (int i = 0; i < selectedTopic.getnQuestions(); i++) {
                numbers.add(i);
            }
            Collections.shuffle(numbers);
            subNumbers= new ArrayList<>(numbers.subList(0, 7));
            challengeDetails.setNumbers(subNumbers);

            DatabaseReference challenge = FirebaseDatabase.getInstance().getReference().child("Challenges").child(selectedFriend.getFbId());
            challenge.setValue(challengeDetails);

            intent.putExtra("selectedFriend", selectedFriend);
            intent.putExtra("selectedTopic", selectedTopic);
            startActivity(intent);
        }
    }
}
