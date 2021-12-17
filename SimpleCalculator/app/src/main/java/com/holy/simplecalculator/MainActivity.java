package com.holy.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewDisplay;

    private String formula = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDisplay = findViewById(R.id.textViewDisplay);
        textViewDisplay.setText(formula);

        // 버튼에 리스너를 설정한다
        findViewById(R.id.textViewNum0).setOnClickListener(this);
        findViewById(R.id.textViewNum1).setOnClickListener(this);
        findViewById(R.id.textViewNum2).setOnClickListener(this);
        findViewById(R.id.textViewNum3).setOnClickListener(this);
        findViewById(R.id.textViewNum4).setOnClickListener(this);
        findViewById(R.id.textViewNum5).setOnClickListener(this);
        findViewById(R.id.textViewNum6).setOnClickListener(this);
        findViewById(R.id.textViewNum7).setOnClickListener(this);
        findViewById(R.id.textViewNum8).setOnClickListener(this);
        findViewById(R.id.textViewNum9).setOnClickListener(this);
        findViewById(R.id.textViewPoint).setOnClickListener(this);
        findViewById(R.id.textViewClear).setOnClickListener(this);
        findViewById(R.id.textViewSign).setOnClickListener(this);
        findViewById(R.id.textViewBack).setOnClickListener(this);
        findViewById(R.id.textViewDivide).setOnClickListener(this);
        findViewById(R.id.textViewMultiply).setOnClickListener(this);
        findViewById(R.id.textViewSubtract).setOnClickListener(this);
        findViewById(R.id.textViewAdd).setOnClickListener(this);
        findViewById(R.id.textViewSubmit).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        // 버튼 클릭을 처리한다
        int id = view.getId();

        if (id == R.id.textViewNum0) {
            onNumberClick(0);
        } else if (id == R.id.textViewNum1) {
            onNumberClick(1);
        } else if (id == R.id.textViewNum2) {
            onNumberClick(2);
        } else if (id == R.id.textViewNum3) {
            onNumberClick(3);
        } else if (id == R.id.textViewNum4) {
            onNumberClick(4);
        } else if (id == R.id.textViewNum5) {
            onNumberClick(5);
        } else if (id == R.id.textViewNum6) {
            onNumberClick(6);
        } else if (id == R.id.textViewNum7) {
            onNumberClick(7);
        } else if (id == R.id.textViewNum8) {
            onNumberClick(8);
        } else if (id == R.id.textViewNum9) {
            onNumberClick(9);
        } else if (id == R.id.textViewPoint) {
            onPointClick();
        } else if (id == R.id.textViewAdd) {
            onOperatorClick('+');
        } else if (id == R.id.textViewSubtract) {
            onOperatorClick('-');
        } else if (id == R.id.textViewMultiply) {
            onOperatorClick('*');
        } else if (id == R.id.textViewDivide) {
            onOperatorClick('/');
        } else if (id == R.id.textViewSign) {
            onSignClick();
        } else if (id == R.id.textViewClear) {
            onClearClick();
        } else if (id == R.id.textViewBack) {
            onBackClick();
        } else if (id == R.id.textViewSubmit) {
            onSubmitClick();
        }

        textViewDisplay.setText(formula);
    }

    // 숫자가 클릭되었을 때

    private void onNumberClick(int value) {

        String strValue = String.valueOf(value);

        if (formula.length() == 1 && getLast() == '0') {
            formula = strValue;
            return;
        }

        formula = formula.concat(strValue);
    }

    // 소수점이 클릭되었을 때

    private void onPointClick() {

        // 소수점은 숫자 뒤에만 입력가능
        if (Character.isDigit(getLast())) {
            formula = formula.concat(".");
        }
    }

    // 연산자가 클릭되었을 때

    private void onOperatorClick(char op) {

        // 연산자는 숫자 뒤에만 입력가능
        if (Character.isDigit(getLast())) {
            formula = formula.concat(Character.toString(op));
        }
    }

    // 부호 바꾸기가 클릭되었을 때

    private void onSignClick() {

        if (!Character.isDigit(getLast())) {
            return;
        }

        String[] operands = formula.split("\\*|\\/|\\+|\\-");

        // 마지막 숫자 부호 바꾸기
        if (operands.length > 0) {
            String last = operands[operands.length - 1];
            int end = formula.length();
            if (last.contains("－")) {
                formula = formula.substring(0, end - last.length())
                        + formula.substring(end - last.length(), end)
                        .replace(last, last.substring(1));
            } else {
                formula = formula.substring(0, end - last.length())
                        + formula.substring(end - last.length(), end)
                        .replace(last, "－" + last);
            }
        }
    }

    // 지우기가 클릭되었을 때

    private void onClearClick() {

        // 수식을 초기화한다
        formula = "";
    }

    // 백스페이스가 클릭되었을 때

    private void onBackClick() {

        // 숫자나 소수점일 때만 지운다
        if (Character.isDigit(getLast()) || getLast() == '.') {
            formula = formula.substring(0, formula.length() - 1);
        }
    }

    // = 가 클릭되었을 때

    private void onSubmitClick() {

        if (!Character.isDigit(getLast()) || formula.contains("Infinity")) {
            return;
        }

        // 수식을 연산하고 결과를 보여준다
        formula = String.valueOf(calculatePoly(formula)).replace("-", "－");
    }

    // 마지막 문자 획득하기

    private char getLast() {
        if (!formula.isEmpty()) {
            return formula.charAt(formula.length() - 1);
        } else {
            return '\0';
        }
    }


    // 다항식 계산 메소드

    public double calculatePoly(String poly) {

        double result;
        String[] monos = poly.split("\\+|\\-");

        if (monos.length == 0) {
            result = calculateMono(poly);
        } else {
            result = calculateMono(monos[0]);
        }

        int j = 1;
        for (int i = 0; i < poly.length() && j < poly.length(); i++) {
            char ch = poly.charAt(i);
            if (ch == '+') {
                result = result + calculateMono(monos[j++]);
            } else if (ch == '-') {
                result = result - calculateMono(monos[j++]);
            }
        }

        return result;
    }

    // 단항식 계산 메소드

    public double calculateMono(String mono) {

        double result;
        String[] nums = mono.split("\\/|\\*");

        if (nums.length == 0) {
            result = calculateNum(mono);
        } else {
            result = calculateNum(nums[0]);
        }

        int j = 1;
        for (int i = 0; i < mono.length() && j < nums.length; i++) {
            char ch = mono.charAt(i);
            if (ch == '*') {
                result = result * calculateNum(nums[j++]);
            } else if (ch == '/') {
                result = result / calculateNum(nums[j++]);
            }
        }

        System.out.println(mono + " = " + result);
        return result;
    }

    // 숫자 계산 메소드

    private double calculateNum(String num) {
        String num2 = num.replace("－", "-");
        return Double.parseDouble(num2);
    }

}














