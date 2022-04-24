package com.farm.foodcalorie;

import com.farm.foodcalorie.api.FoodApi;
import com.farm.foodcalorie.data.Food;

import junit.framework.TestCase;

public class FoodApiTest extends TestCase {

    public void testGet() {

        new Thread(() -> {

            System.out.println("zz");

            Food food1 = FoodApi.get("신라면");
            System.out.println(food1 != null ? food1.toString() : "null");

            Food food2 = FoodApi.get("북경오리");
            System.out.println(food2 != null ? food2.toString() : "null");

        }).start();

    }
}