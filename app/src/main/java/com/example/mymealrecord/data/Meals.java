package com.example.mymealrecord.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meals {
    private String mealImageUrl;
    private String mealTime;
    private String foodMemo;
    private Integer Calorie;
    private Float Rating;
    private String acquisitionDate;

    public Meals() {
    }

    public Meals(String mealImageUrl, String mealTime, String foodMemo, Integer Calorie, Float Rating, String acquisitionDate) {
        this.mealImageUrl = mealImageUrl;
        this.mealTime = mealTime;
        this.Calorie = Calorie;
        this.Rating = Rating;
        this.foodMemo = foodMemo;
        this.acquisitionDate = acquisitionDate;
    }

}
