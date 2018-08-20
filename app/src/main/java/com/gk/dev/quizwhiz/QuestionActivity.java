package com.gk.dev.quizwhiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.gk.dev.quizwhiz.Model.Question;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class QuestionActivity extends AppCompatActivity {
    CountDownTimer countDownTimer;
    TextView timer, questionText, score, timeTextView, scoreTextView;
    Button choice1, choice2, choice3, choice4;
    Integer i, numberOfQuestions, j, k;
    Question question;
    ArrayList<Question> questions;
    ArrayList<Integer> numbers;
    DatabaseReference databaseReference;
    private String fbid, c1, c2, c3, c4, ca, q;
    private ProgressBar progressBar;
    ChallengeDetails challengeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        timer = findViewById(R.id.tv_timer);
        Profile profile = Profile.getCurrentProfile();
        fbid = profile.getId();
        challengeDetails = (ChallengeDetails) getIntent().getSerializableExtra("challengeDetails");
        questions = new ArrayList<>();
        RelativeLayout layout = findViewById(R.id.root_layout);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar = new ProgressBar(QuestionActivity.this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);
        questionText = findViewById(R.id.question);
        timeTextView = findViewById(R.id.time);
        scoreTextView = findViewById(R.id.tv_score);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        timer.setVisibility(View.GONE);
        timeTextView.setVisibility(View.GONE);
        scoreTextView.setVisibility(View.GONE);
        choice1.setVisibility(View.GONE);
        choice2.setVisibility(View.GONE);
        choice3.setVisibility(View.GONE);
        choice4.setVisibility(View.GONE);
        choice1.setBackgroundColor(Color.WHITE);
        choice2.setBackgroundColor(Color.WHITE);
        choice3.setBackgroundColor(Color.WHITE);
        choice4.setBackgroundColor(Color.WHITE);
        score = findViewById(R.id.score);
        i = 0;
        k = 0;
        databaseReference.child("Challenges").child("count").setValue(0);
        databaseReference.child("Challenges").child(fbid).child("score").setValue(0);
        score.setVisibility(View.GONE);
        databaseReference.child("Challenges").child(fbid).child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int dataSnapshotValue = dataSnapshot.getValue(Integer.class);
                score.setText(Integer.toString(dataSnapshotValue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Challenges").child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.getValue(Integer.class);
                if (i == numberOfQuestions && count == 2) {
                    startActivity(new Intent(QuestionActivity.this, ResultActivity.class));
                } else {
                    if (count == 2) {
                        fireNextQuestion();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(Integer.toString((int) (millisUntilFinished / 1000)));

            }

            public void onFinish() {

                timer.setText("10");
                fireNextQuestion();
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        databaseReference.child("Questions").child("Tech").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfQuestions = (int) dataSnapshot.getChildrenCount();
                Log.d("prerna", Integer.toString(numberOfQuestions));
                numbers = new ArrayList<>();
                for (j = 0; j < numberOfQuestions; j++) {
                    numbers.add(j);
                }
                Log.d("prerna", Arrays.toString(numbers.toArray()));
                Collections.shuffle(numbers);
                Log.d("prerna", Arrays.toString(numbers.toArray()));
                for (j = 0; j < 7; j++) {
                    databaseReference.child("Questions").child("Tech").child(Integer.toString(numbers.get(j))).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            question = new Question();
                            question = dataSnapshot.getValue(Question.class);
                            Log.d("peda", dataSnapshot.toString());
                            questions.add(question);
                            k++;
                            if (k == 7) {
                                progressBar.setVisibility(View.GONE);
                                timer.setVisibility(View.VISIBLE);
                                timeTextView.setVisibility(View.VISIBLE);
                                scoreTextView.setVisibility(View.VISIBLE);
                                score.setVisibility(View.VISIBLE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                fireNextQuestion();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void fireNextQuestion() {
        if (i < 7) {
            Log.d("abc", Integer.toString(i));
            Log.d("abc", questions.get(i).getQuestionText());
            databaseReference.child("Challenges").child("count").setValue(0);
            choice1.setEnabled(true);
            choice2.setEnabled(true);
            choice3.setEnabled(true);
            choice4.setEnabled(true);
            choice1.setBackgroundColor(Color.WHITE);
            choice2.setBackgroundColor(Color.WHITE);
            choice3.setBackgroundColor(Color.WHITE);
            choice4.setBackgroundColor(Color.WHITE);
            q = questions.get(i).getQuestionText();
            questionText.setText(q);
            choice1.setVisibility(View.GONE);
            choice2.setVisibility(View.GONE);
            choice3.setVisibility(View.GONE);
            choice4.setVisibility(View.GONE);

            c1 = questions.get(i).getChoice1();
            c2 = questions.get(i).getChoice2();
            c3 = questions.get(i).getChoice3();
            c4 = questions.get(i).getChoice4();
            ca = questions.get(i).getcorrectAnswer();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    choice1.setText(questions.get(i).getChoice1());
                    choice2.setText(questions.get(i).getChoice2());
                    choice3.setText(questions.get(i).getChoice3());
                    choice4.setText(questions.get(i).getChoice4());
                    choice1.setVisibility(View.VISIBLE);
                    choice2.setVisibility(View.VISIBLE);
                    choice3.setVisibility(View.VISIBLE);
                    choice4.setVisibility(View.VISIBLE);
                    choice1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            choice1.setEnabled(false);
                            choice2.setEnabled(false);
                            choice3.setEnabled(false);
                            choice4.setEnabled(false);
                            databaseReference.child("Challenges").child("count").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    if (mutableData.getValue() == null) {
                                        mutableData.setValue(1);
                                    } else {
                                        int count = mutableData.getValue(Integer.class);
                                        mutableData.setValue(count + 1);
                                    }
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                    // Analyse databaseError for any error during increment
                                }
                            });
                            if (c1.equalsIgnoreCase(ca)) {
                                choice1.setBackgroundColor(getResources().getColor(R.color.green));
                                databaseReference.child("Challenges").child(fbid).child("score").runTransaction(new Transaction.Handler() {

                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        if (mutableData.getValue() == null) {
                                            mutableData.setValue(1);
                                        } else {
                                            int score = mutableData.getValue(Integer.class);
                                            mutableData.setValue(score + Integer.parseInt(timer.getText().toString()));
                                        }
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                        // Analyse databaseError for any error during increment
                                    }
                                });

                            } else {
                                choice1.setBackgroundColor(getResources().getColor(R.color.red));
                            }
                        }
                    });
                    choice2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            choice1.setEnabled(false);
                            choice2.setEnabled(false);
                            choice3.setEnabled(false);
                            choice4.setEnabled(false);
                            databaseReference.child("Challenges").child("count").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    if (mutableData.getValue() == null) {
                                        mutableData.setValue(1);
                                    } else {
                                        int count = mutableData.getValue(Integer.class);
                                        mutableData.setValue(count + 1);
                                    }
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                    // Analyse databaseError for any error during increment
                                }
                            });
                            if (c2.equalsIgnoreCase(ca)) {
                                choice2.setBackgroundColor(getResources().getColor(R.color.green));
                                databaseReference.child("Challenges").child(fbid).child("score").runTransaction(new Transaction.Handler() {

                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        if (mutableData.getValue() == null) {
                                            mutableData.setValue(1);
                                        } else {
                                            int score = mutableData.getValue(Integer.class);
                                            mutableData.setValue(score + Integer.parseInt(timer.getText().toString()));
                                        }
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                        // Analyse databaseError for any error during increment
                                    }
                                });

                            } else {
                                choice2.setBackgroundColor(getResources().getColor(R.color.red));
                            }
                        }
                    });
                    choice3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            choice1.setEnabled(false);
                            choice2.setEnabled(false);
                            choice3.setEnabled(false);
                            choice4.setEnabled(false);
                            databaseReference.child("Challenges").child("count").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    if (mutableData.getValue() == null) {
                                        mutableData.setValue(1);
                                    } else {
                                        int count = mutableData.getValue(Integer.class);
                                        mutableData.setValue(count + 1);
                                    }
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                    // Analyse databaseError for any error during increment
                                }
                            });
                            if (c3.equalsIgnoreCase(ca)) {
                                choice3.setBackgroundColor(getResources().getColor(R.color.green));
                                databaseReference.child("Challenges").child(fbid).child("score").runTransaction(new Transaction.Handler() {

                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        if (mutableData.getValue() == null) {
                                            mutableData.setValue(1);
                                        } else {
                                            int score = mutableData.getValue(Integer.class);
                                            mutableData.setValue(score + Integer.parseInt(timer.getText().toString()));
                                        }
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                        // Analyse databaseError for any error during increment
                                    }
                                });

                            } else {
                                choice3.setBackgroundColor(getResources().getColor(R.color.red));
                            }
                        }
                    });
                    choice4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            choice1.setEnabled(false);
                            choice2.setEnabled(false);
                            choice3.setEnabled(false);
                            choice4.setEnabled(false);
                            databaseReference.child("Challenges").child("count").runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData mutableData) {
                                    if (mutableData.getValue() == null) {
                                        mutableData.setValue(1);
                                    } else {
                                        int count = mutableData.getValue(Integer.class);
                                        mutableData.setValue(count + 1);
                                    }
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                    // Analyse databaseError for any error during increment
                                }
                            });
                            if (c4.equalsIgnoreCase(ca)) {
                                choice4.setBackgroundColor(getResources().getColor(R.color.green));
                                databaseReference.child("Challenges").child(fbid).child("score").runTransaction(new Transaction.Handler() {

                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        if (mutableData.getValue() == null) {
                                            mutableData.setValue(1);
                                        } else {
                                            int score = mutableData.getValue(Integer.class);
                                            mutableData.setValue(score + Integer.parseInt(timer.getText().toString()));
                                        }
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean success, DataSnapshot dataSnapshot) {
                                        // Analyse databaseError for any error during increment
                                    }
                                });

                            } else {
                                choice4.setBackgroundColor(getResources().getColor(R.color.red));
                            }
                        }
                    });
                    i++;
                    final Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countDownTimer.start();
                        }
                    }, 500);

                }
            }, 3000);

        } else {
            startActivity(new Intent(QuestionActivity.this, ResultActivity.class));
        }
    }
}


