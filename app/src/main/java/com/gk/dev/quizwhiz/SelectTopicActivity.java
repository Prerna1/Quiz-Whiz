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

import com.facebook.Profile;
import com.gk.dev.quizwhiz.Adapters.TopicAdapter;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.gk.dev.quizwhiz.Model.TopicName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SelectTopicActivity extends AppCompatActivity implements TopicAdapter.ListItemClickListener {
    ValueEventListener challengeListener;
    ChallengeDetails challengerInformation;
    List<TopicName> topicsList;
    String fbId;
    DatabaseReference challenges, userStatus;
    SearchView searchView;
    TopicAdapter mAdapter;
    RecyclerView recyclerView;
    boolean topicsRetrieved;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_topic);
        topicsRetrieved = false;
        topicsList = new ArrayList<>();
        DatabaseReference topics = FirebaseDatabase.getInstance().getReference().child("Topics");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.topics_recycler_view);
        progressBar = findViewById(R.id.pb_select_topic);
        recyclerView.setLayoutManager(mLayoutManager);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Profile profile = Profile.getCurrentProfile();
        fbId = profile.getId();

        userStatus = databaseReference.child("UserDetails/" + fbId + "/status");
        userStatus.onDisconnect().setValue(0);
        userStatus.setValue(1);

        topics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    topicsList.add(Objects.requireNonNull(d.getValue(TopicName.class)));
                }
                assert topicsList != null;
                mAdapter = new TopicAdapter(topicsList, SelectTopicActivity.this);
                recyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
                topicsRetrieved = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_topic_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if (topicsRetrieved)
                    mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (topicsRetrieved)
                    mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        // close search view on back button pressed
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
                    Intent intent = new Intent(SelectTopicActivity.this, AcceptRejectActivity.class);
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
    public void onListItemClick(TopicName selectedTopic) {
        Intent intent = new Intent(SelectTopicActivity.this, ChallengeActivity.class);
        intent.putExtra("selectedTopic", selectedTopic);
        startActivity(intent);
    }
}

