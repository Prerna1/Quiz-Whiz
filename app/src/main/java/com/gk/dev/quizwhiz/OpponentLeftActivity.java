package com.gk.dev.quizwhiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OpponentLeftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponent_left);
    }
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        this.startActivity(new Intent(OpponentLeftActivity.this,SelectTopicActivity.class));

        return;
    }
}
