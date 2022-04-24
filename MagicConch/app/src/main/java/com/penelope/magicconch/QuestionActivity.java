package com.penelope.magicconch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    public static final String EXTRA_EXIT = "com.penelope.magicconch.exit";

    private ActivityResultLauncher<Intent> answerActivityLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        answerActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null) {
                        boolean exit = data.getBooleanExtra(EXTRA_EXIT, false);
                        if (exit) {
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
                }
        );

        Button questionButton = findViewById(R.id.buttonQuestion);
        questionButton.setOnClickListener(v -> startAnswerQuestion());
    }

    private void startAnswerQuestion() {
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra(AnswerActivity.EXTRA_ANSWER, getRandomAnswer());
        answerActivityLauncher.launch(intent);
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
    }

    private String getRandomAnswer() {
        final String[] answers = {
                "받아들일 필요가 있을 것이다","다른 대안들도 모색해라","상황이 모호하다","애초의 생각을 믿어라",
                "1년 후 즈음에는,아무 상관 없을 것이다","어려울 수는 있지만, 그 안에서 가치를 찾게 될 것이다",
                "그렇다, 하지만 무리하지는 마라","꼭 해야 한다","아마도","확실하다","무조건","당신의 직감을 믿어라",
                "당신은 별로 관심 없다","명백한 사실들을 간과하지는 마라","당신이 조금 더 나이 든 후라면...",
                "마지막까지 초심을 잃지 마라","음,,, 글쎄","시간 낭비 하지 마라","아주 특별할 수도 있다",
                "이번에는 흡족하지 않을 지도","망설이지 마라","물론이다","보다 참신한 해결책을 시도하라",
                "비밀로 간직해라","실행에 옮기면 좋아질 것이다","예기치 못한 일들에 대비하라","절대 아니다",
                "넘어가라","의심하지 마라","절대로","결정하기 전에 시간을 가져라","보장은 없다",
                "즐기는 것 또한 잊지 마라","전력투구하라","초점을 바꿔라","바보같은 생각이다","실패할 리 없다",
                "바보같은 생각이다","당신 앞의 장애물을 극복하라","대가를 치러야 할 것이다",
                "어려울 수는 있지만, 그 안에서 가치를 찾게 될 것이다","낙관해 볼 만하다","염려하지마라",
                "적극적인 자세로 임해라","기다리지 마라","힘을 아껴라","마지막까지 초심을 잃지 마라","웃어 넘겨라",
                "당신이 알아야 할 모든 것은 알게 될 것이다","신중하게 접근하라","더 관대해라",
                "기정사실인 듯 행동하라","예","아니오","숲 속에서는 숲을 보지 못한다",
                "조금 더 명확한 관점으로 보라","협력이 성공의 열쇠다","쾌쾌 묵은 방법들은 과감히 버려라",
                "혼자가 아니라면","상당한 노력이 요구될 것이다","타협해야 할 것이다","당신의 의무를 다해라",
                "뻔한 것은 최선책이 아닐 수도 있다","다른 것들을 포기해야만 할 지도","기다려라","불확실하다",
                "다른 방도를 모색해 볼 필요가 있다","긍정적인 결과가 예상된다","실망하지 않을 것이다",
                "성공을 위해서는 도움이 필요할 것이다","이유의 리스트를 작성해보라",
                "어려울 수는 있지만, 그 안에서 가치를 찾게 될 것이다","소신을 가져라",
                "당신 일에 집중하는 편이 나을 듯","다른 것들을 포기해야만 할 지도","새로운 계획을 생각하라",
                "지나친 의심이 망칠 수 있다","행동만이 결과를 만든다","주변의 조언을 차분히 돌아보라",
                "사소하지만 중대한 실수가 있을 것이다","원하는 것을 구체적으로 적고 바라보라","쉽게 포기하지 말라",
                "당장 행동하라","안 될 이유를 점검해보라","의문을 제기하고, 의심을 제기하라",
                "성급한 것은 좋지 않다","당신에게 맞는 길이 기다리고 있다","두 번째 길이 나타날 것이다",
                "늘 같은 방법을 반복할 필요는 없다","에너지를 아껴두라","지켜보라","심호흡을 하고 침착하라",
                "주변에 크게 알리지 말라","상황을 바꿀 방법이 필요할 것이다","결단코 확실하다",
                "가장 가까운 사람의 의견을 믿어라","준비가 되었으면 시작하라","즐기는 마음을 가져라",
                "상황은 계속해서 바뀐다","넓은 마음으로 받아 들이게 될 것이다",
        };

        int index = new Random().nextInt(answers.length);
        return answers[index];
    }

}