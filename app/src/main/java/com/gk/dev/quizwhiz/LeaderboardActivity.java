package com.gk.dev.quizwhiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gk.dev.quizwhiz.Adapters.LeaderboardAdapter;
import com.gk.dev.quizwhiz.Model.LeaderboardDetails;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private List<LeaderboardDetails> leaderboardList = new ArrayList<>();

    private RecyclerView recyclerView;
    private LeaderboardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new LeaderboardAdapter(leaderboardList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        LeaderboardDetails movie = new LeaderboardDetails("Prerna",20 , 1);
        leaderboardList.add(movie);

        LeaderboardDetails movie1 = new LeaderboardDetails("Kashish", 2, 2);
        leaderboardList.add(movie1);
    }
}
