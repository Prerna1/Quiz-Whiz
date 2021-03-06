package com.gk.dev.quizwhiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            finish();
            startActivity(new Intent(RedirectActivity.this, LoginActivity.class));
        } else {
            finish();
            startActivity(new Intent(RedirectActivity.this, DashboardActivity.class));
        }
    }
}
