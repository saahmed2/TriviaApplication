package com.example.triviaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapplication.data.AnswerListAsyncResponse;
import com.example.triviaapplication.data.QuestionBank;
import com.example.triviaapplication.model.Question;
import com.example.triviaapplication.model.Score;
import com.example.triviaapplication.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String MESSAGE_ID = "messages_prefs";
    private TextView questionTv, questionCounterTv, scoreTv, highScoreTv;
    private Button trueButton, falseButton;
    private ImageButton previousButton, nextButton;
    private int currentQuestionIndex;
    private List<Question> questionList;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.prevButton);
        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);
        questionCounterTv = findViewById(R.id.counterTv);
        questionTv = findViewById(R.id.questionTv);
        scoreTv = findViewById(R.id.scoreTv);
        highScoreTv = findViewById(R.id.highScoreTv);

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        scoreTv.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

        //get previous state
        currentQuestionIndex = prefs.getState();
        Log.d("SetState", "onCreate: " + prefs.getState());

        highScoreTv.setText(MessageFormat.format("High Score: {0}", String.valueOf(prefs.getHighScore())));

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTv.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTv.setText(currentQuestionIndex + " / " + questionArrayList.size()); //counts from 0 to 234
                Log.d("Inside", "processFinished: " + questionArrayList);
            }
        });
        //Log.d("Main ", "onCreate: " + questionList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prevButton:
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id. nextButton:
                goNext();
                break;
            case R.id.trueButton:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.falseButton:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        Log.d("Score", "addPoints: " + score.getScore());
        scoreTv.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
    }

    private void deductPoints() {
        scoreCounter -= 100;
        if (scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreTv.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTv.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
            Log.d("Default Score", "deductPoints: " + scoreCounter);
        }
    }

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerTrue) {
            fadeView();
            addPoints();
            toastMessageId = R.string.correct_answer;
        } else {
            shakeAnimation();
            deductPoints();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId,
                Toast.LENGTH_SHORT).show();
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTv.setText(question);
        questionCounterTv.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size()));
    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.card_view);
        //alpha animations allows the animation to fade in and out from a visible float to an invisible float.
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        final CardView cardview = findViewById(R.id.card_view);
        cardview.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardview.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardview.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}