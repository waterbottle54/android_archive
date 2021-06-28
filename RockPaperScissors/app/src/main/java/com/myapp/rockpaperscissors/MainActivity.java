package com.myapp.rockpaperscissors;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 가위/바위/보 선택을 나타내는 상수 코드
    public static final int CHOICE_ROCK = 0;        // 바위
    public static final int CHOICE_PAPER = 1;       // 보
    public static final int CHOICE_SCISSOR = 2;     // 가위
    public static final int CHOICE_NONE = -1;       // 안냄

    // 결과를 나타내는 상수 코드
    public static final int RESULT_LOSE = 0;
    public static final int RESULT_DRAW = 1;
    public static final int RESULT_WIN = 2;

    // 나와 상대의 선택으로부터 결과를 판정한다.
    public static int getResultFromChoice(int myChoice, int enemyChoice) {

        // 비기는 경우
        if (enemyChoice == myChoice) {
            return RESULT_DRAW;
        }
        // 이기는 경우들
        else if (enemyChoice == MainActivity.CHOICE_PAPER) {
            if (myChoice == MainActivity.CHOICE_SCISSOR) {
                return RESULT_WIN;
            }
        }
        else if (enemyChoice == MainActivity.CHOICE_ROCK) {
            if (myChoice == MainActivity.CHOICE_PAPER) {
                return RESULT_WIN;
            }
        }
        else if (enemyChoice == MainActivity.CHOICE_SCISSOR) {
            if (myChoice == MainActivity.CHOICE_ROCK) {
                return RESULT_WIN;
            }
        }
        return RESULT_LOSE;
    }

    // 가위/바위/보 선택에 대응하는 이미지 리소스
    public static int getImageResFromChoice(int choice) {
        switch (choice) {
            case CHOICE_ROCK:
                return R.drawable.rock;
            case CHOICE_PAPER:
                return R.drawable.paper;
            case CHOICE_SCISSOR:
                return R.drawable.scissors;
        }
        return 0;
    }

    // 카운트다운 타이머
    private CountDownTimer mCountdown;
    // 시작 여부
    private boolean mStarted;
    // 낸 패 (가위/바위/보/안냄 상수 코드 중 하나)
    private int mChoice;

    // 액티비티 내 레이아웃 / 뷰
    private View mStartLayout;
    private View mPlayLayout;
    private View mChoiceLayout;
    private TextView mPlayText;
    private ImageView mMyHandImage;
    private ImageView mEnemyHandImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 앱이 첫 실행될 때 표지를 잠깐 띄웠다가 fade out 한다.
        if (savedInstanceState == null) {
            fadeOutCover();
        }

        // 가위바위보 시작 버튼에 리스너 설정
        Button startButton = findViewById(R.id.btn_start);
        startButton.setOnClickListener(this);

        // 가위/바위/보 이미지뷰에 리스너 설정
        ImageView rockImage = findViewById(R.id.img_rock);
        ImageView paperImage = findViewById(R.id.img_paper);
        ImageView scissorImage = findViewById(R.id.img_scissor);
        rockImage.setOnClickListener(this);
        paperImage.setOnClickListener(this);
        scissorImage.setOnClickListener(this);

        // 레이아웃 / 뷰 초기화
        mStartLayout = findViewById(R.id.layout_start);
        mPlayLayout = findViewById(R.id.layout_play);
        mChoiceLayout = findViewById(R.id.layout_choice);
        mPlayText = findViewById(R.id.txt_play);
        mMyHandImage = findViewById(R.id.img_hand_me);
        mEnemyHandImage = findViewById(R.id.img_hand_enemy);
    }

    // 표지 레이아웃에 fade out 애니메이션 적용

    private void fadeOutCover() {

        View cover = findViewById(R.id.cover);
        cover.setVisibility(View.VISIBLE);

        // fade out 애니메이션 로드 : 자체 정의한 fade out 리소스를 사용한다.
        // 리스너를 설정하여 애니메이션이 끝날 때 표지 레이아웃을 GONE 상태로 만든다.
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                cover.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        // 2초간 보여주다가, fade out 애니메이션 적용
        cover.postDelayed(new Runnable() {
            @Override
            public void run() {
                cover.startAnimation(fadeOut);
            }
        }, 2000);
    }

    // 액티비티 내의 버튼 클릭 처리

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start:
                if (!mStarted) {
                    startGame();
                }
                break;
            case R.id.img_rock:
                if (mStarted) {
                    choiceOne(CHOICE_ROCK);
                }
                break;
            case R.id.img_paper:
                if (mStarted) {
                    choiceOne(CHOICE_PAPER);
                }
                break;
            case R.id.img_scissor:
                if (mStarted) {
                    choiceOne(CHOICE_SCISSOR);
                }
                break;
        }
    }

    // 가위바위보 시작 처리

    private void startGame() {

        mStarted = true;
        mChoice = CHOICE_NONE;
        mMyHandImage.setVisibility(View.GONE);
        mEnemyHandImage.setVisibility(View.GONE);

        // 시작 레이아웃에 slide out 애니메이션을 적용한 후 화면에서 없앤다.

        Animation slideOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {

                // slide out 이 끝나면 시작 레이아웃을 숨기고, 진행중 레이아웃을 띄운다.
                mStartLayout.setVisibility(View.GONE);
                mPlayLayout.setVisibility(View.VISIBLE);
                mPlayText.setText(getString(R.string.in_three_seconds));

                // 카운트다운을 시작한다 : 총 4초
                mCountdown = new CountDownTimer(
                        4000, 1000, MainActivity.this);
                mCountdown.start();

                // 선택 레이아웃 (가위/바위/보 이미지) 을 띄운다. (fade-in 적용)
                mChoiceLayout.setVisibility(View.VISIBLE);
                Animation fadeIn = AnimationUtils.loadAnimation(
                        MainActivity.this, android.R.anim.fade_in);
                mChoiceLayout.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        mStartLayout.startAnimation(slideOut);
    }

    // 사용자가 가위/바위/보 중 하나를 선택한 경우 처리

    private void choiceOne(int choice) {

        mChoice = choice;

        // 선택 레이아웃을 fade-out 으로 숨긴다.

        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mChoiceLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        mChoiceLayout.startAnimation(fadeOut);

        // 나의 패를 slide-in 으로 보여준다.

        Animation slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        mMyHandImage.setVisibility(View.VISIBLE);
        mMyHandImage.startAnimation(slideIn);
        mMyHandImage.setImageResource(getImageResFromChoice(mChoice));
    }

    // 카운트다운 타이머 클래스

    static class CountDownTimer extends android.os.CountDownTimer {

        // 메모리 누수 방지를 위해 WeakReference 사용
        WeakReference<MainActivity> mainActivity;

        public CountDownTimer(long millisInFuture, long countDownInterval, MainActivity mainActivity) {
            super(millisInFuture, countDownInterval);
            this.mainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            // 매 카운트다운 타이밍마다, 진행중 레이아웃에 slide out 애니메이션을 적용한다.
            // (3초안에 내세요 -> 3 -> 2 -> 1 -> 0)

            Animation slideOut = AnimationUtils.loadAnimation(
                    mainActivity.get(), android.R.anim.slide_out_right);

            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {

                    // slide out 이 끝나면, 텍스트뷰의 카운트값을 갱신한다.
                    mainActivity.get().mPlayText.setText(String.valueOf(millisUntilFinished/1000));
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });

            mainActivity.get().mPlayLayout.startAnimation(slideOut);
        }

        @Override
        public void onFinish() {

            // 카운트다운이 끝나면, 진행중 레이아웃을 숨긴다.
            mainActivity.get().mPlayLayout.setVisibility(View.GONE);

            // 선택 레이아웃도 fade-out 애니메이션으로 숨긴다. (끝내 선택 안한 경우)
            if (mainActivity.get().mChoiceLayout.getVisibility() == View.VISIBLE) {
                Animation fadeOut = AnimationUtils.loadAnimation(mainActivity.get(), android.R.anim.fade_out);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mainActivity.get().mChoiceLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                mainActivity.get().mChoiceLayout.startAnimation(fadeOut);
            }

            // 상대의 패를 랜덤하게 결정하고, slide-in 애니메이션으로 보여준다.

            int enemyChoice = (int)(Math.random() * 3);

            Animation slideIn = AnimationUtils.loadAnimation(
                    mainActivity.get(), R.anim.slide_in_right);
            mainActivity.get().mEnemyHandImage.setVisibility(View.VISIBLE);
            mainActivity.get().mEnemyHandImage.startAnimation(slideIn);
            mainActivity.get().mEnemyHandImage.setImageResource(
                    MainActivity.getImageResFromChoice(enemyChoice));

            // 승부를 판정한다.

            int myChoice = mainActivity.get().mChoice;
            int result = MainActivity.getResultFromChoice(myChoice, enemyChoice);

            // 2초 후, 시작 레이아웃을 띄우고, 결과를 보여준다. 버튼은 "다시 하기" 로 바꾼다.

            mainActivity.get().mPlayLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainActivity.get().mStartLayout.setVisibility(View.VISIBLE);
                    TextView startTitle = mainActivity.get().findViewById(R.id.txt_start_title);
                    Button startButton = mainActivity.get().findViewById(R.id.btn_start);

                    String strResult = "패배 T.T";
                    if (result == MainActivity.RESULT_DRAW) {
                        strResult = "무승부 ~";
                    } else if (result == MainActivity.RESULT_WIN) {
                        strResult = "승리 !!";
                    }
                    startTitle.setText(strResult);
                    startButton.setText("다시 하기");
                    mainActivity.get().mStarted = false;
                }
            }, 2000);
        }
    }

}




