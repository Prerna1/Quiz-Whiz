package com.gk.dev.quizwhiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.gk.dev.quizwhiz.Model.ChallengeDetails;
import com.gk.dev.quizwhiz.Model.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class QuestionActivity extends AppCompatActivity {
    CountDownTimer countDownTimer;
    TextView timer, questionText, user1scoreTextView, user2scoreTextView, user1, user2;
    Button choice1, choice2, choice3, choice4;
    Integer i, j, k;
    Question question;
    ArrayList<Question> questions;
    ArrayList<Integer> numbers;
    DatabaseReference databaseReference;
    ChallengeDetails challengeDetails;
    DatabaseReference userStatus;
    private String fbId, c1, c2, c3, c4, ca, q, questionNumber;
    private ProgressBar progressBar;
    private DatabaseReference friendStatus;
    private ValueEventListener statusListener;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        timer = findViewById(R.id.tv_timer);
        final Profile profile = Profile.getCurrentProfile();
        fbId = profile.getId();

        userStatus = FirebaseDatabase.getInstance().getReference().child("UserDetails/" + fbId + "/status");
        userStatus.onDisconnect().setValue(0);
        userStatus.setValue(2);

        challengeDetails = (ChallengeDetails) Objects.requireNonNull(getIntent().getExtras()).get("challengeDetails");
        questions = new ArrayList<>();
        layout = findViewById(R.id.root_layout);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressBar = new ProgressBar(QuestionActivity.this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);
        questionText = findViewById(R.id.question);
        user1scoreTextView = findViewById(R.id.score1);
        user2scoreTextView = findViewById(R.id.score2);
        user1 = findViewById(R.id.user1);
        user2 = findViewById(R.id.user2);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        timer.setVisibility(View.GONE);
        user1scoreTextView.setVisibility(View.GONE);
        user2scoreTextView.setVisibility(View.GONE);
        user1.setVisibility(View.GONE);
        user2.setVisibility(View.GONE);
        choice1.setVisibility(View.GONE);
        choice2.setVisibility(View.GONE);
        choice3.setVisibility(View.GONE);
        choice4.setVisibility(View.GONE);
        choice1.setBackgroundColor(Color.WHITE);
        choice2.setBackgroundColor(Color.WHITE);
        choice3.setBackgroundColor(Color.WHITE);
        choice4.setBackgroundColor(Color.WHITE);
        i = 0;
        k = 0;
        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child("count").setValue(0);
        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("questionStatus").setValue(0);
        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("score").setValue(0);

        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.getValue(Integer.class);
                if (i == 7 && count == 2) {
                    Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
                    intent.putExtra("challengeDetails", challengeDetails);
                    intent.putExtra("user1Score", user1scoreTextView.getText().toString());
                    intent.putExtra("user2Score", user2scoreTextView.getText().toString());
                    startActivity(intent);

                } else {
                    if (count == 2) {
                        countDownTimer.cancel();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fireNextQuestion();
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("questionStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int status = dataSnapshot.getValue(Integer.class);
                    if (status == 1) {
                        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(challengeDetails.getOpponentFbId()).child("questionStatus").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int status1 = dataSnapshot.getValue(Integer.class);
                                if(status1 == 1){
                                    progressBar.setVisibility(View.GONE);
                                    timer.setVisibility(View.VISIBLE);
                                    user1scoreTextView.setVisibility(View.VISIBLE);
                                    user1.setVisibility(View.VISIBLE);
                                    user2.setVisibility(View.VISIBLE);
                                    user2scoreTextView.setVisibility(View.VISIBLE);
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
        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(challengeDetails.getOpponentFbId()).child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user2.setText(challengeDetails.getName());
                int dataSnapshotValue = 0;
                if (dataSnapshot.getValue(Integer.class) != null) {
                    dataSnapshotValue = dataSnapshot.getValue(Integer.class);
                }
                user2scoreTextView.setText(Integer.toString(dataSnapshotValue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user1.setText(profile.getName());
                int dataSnapshotValue = 0;
                if (dataSnapshot.getValue(Integer.class) != null)
                    dataSnapshotValue = dataSnapshot.getValue(Integer.class);
                user1scoreTextView.setText(Integer.toString(dataSnapshotValue));
                numbers = new ArrayList<>();
                numbers = challengeDetails.getNumbers();
                Question question1 = new Question();
                for (j = 0; j < 7; j++)
                    questions.add(question1);
                for (j = 0; j < 7; j++) {
                    Log.d("array", Integer.toString(numbers.get(j)));
                    questionNumber = Integer.toString(numbers.get(j));
                    databaseReference.child("Questions").child(challengeDetails.getTopic()).child(questionNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            question = new Question();
                            question = dataSnapshot.getValue(Question.class);
                            Log.d("peda", dataSnapshot.toString());
                            int ind = challengeDetails.getNumbers().indexOf(Integer.parseInt(dataSnapshot.getKey()));
                            questions.set(ind, question);
                            k++;
                            if (k == 7) {
                                databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("questionStatus").setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
//        QuestionActivity.super.onBackPressed();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to surrender? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseReference.child("UserDetails").child(fbId).child("status").setValue(1);
               startActivity(new Intent(QuestionActivity.this,DashboardActivity.class));
                startActivity(new Intent(QuestionActivity.this, DashboardActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void fireNextQuestion() {
        if (i < 7) {
            Log.d("abc", Integer.toString(i));
            Log.d("abc", questions.get(i).getQuestionText());
            databaseReference.child("Challenges").child(challengeDetails.getFbId()).child("count").setValue(0);
            timer.setText("10");
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
                            databaseReference.child("Challenges").child(challengeDetails.getFbId()).child("count").runTransaction(new Transaction.Handler() {
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
                                databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("score").runTransaction(new Transaction.Handler() {

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
                            databaseReference.child("Challenges").child(challengeDetails.getFbId()).child("count").runTransaction(new Transaction.Handler() {
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
                                databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("score").runTransaction(new Transaction.Handler() {

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
                            databaseReference.child("Challenges").child(challengeDetails.getFbId()).child("count").runTransaction(new Transaction.Handler() {
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
                                databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("score").runTransaction(new Transaction.Handler() {

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
                            databaseReference.child("Challenges").child(challengeDetails.getFbId()).child("count").runTransaction(new Transaction.Handler() {
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
                                databaseReference.child("Challenges").child(challengeDetails.getFbId()).child(fbId).child("score").runTransaction(new Transaction.Handler() {

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
                    countDownTimer.start();

                }
            }, 3000);

        } else {
            Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
            intent.putExtra("challengeDetails", challengeDetails);
            intent.putExtra("user1Score", user1scoreTextView.getText().toString());
            intent.putExtra("user2Score", user2scoreTextView.getText().toString());
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        friendStatus = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(challengeDetails.getFbId()).child("status");
        statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int status = dataSnapshot.getValue(Integer.class);
                if (status!=2) {
                    startActivity(new Intent(QuestionActivity.this,OpponentLeftActivity.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendStatus.addValueEventListener(statusListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        friendStatus.removeEventListener(statusListener);
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
}
