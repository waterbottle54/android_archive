package com.holy.simplecalculator;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivityTest extends TestCase {

    public void testCalculatePoly() {

        String poly = "6/3";

        System.out.println(calculatePoly(poly));
    }

    public void testSign() {

        String poly = "4+3/7+3.2";
        String[] operands = poly.split("\\*|\\/|\\+|\\-");

        if (operands.length > 0) {
            String last = operands[operands.length - 1];
            if (last.contains("－")) {
                poly = poly.replace(last, last.substring(1));
            } else {
                poly = poly.replace(last, "－" + last);
            }
        }

        System.out.println(poly);
    }


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
               // System.out.println(result);
            } else if (ch == '-') {
                result = result - calculateMono(monos[j++]);
               // System.out.println(result);
            }
        }

        return result;
    }

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

    private double calculateNum(String num) {
        String num2 = num.replace("－", "-");
        return Double.parseDouble(num2);
    }

}