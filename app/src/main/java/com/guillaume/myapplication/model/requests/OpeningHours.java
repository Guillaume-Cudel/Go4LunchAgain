package com.guillaume.myapplication.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private String openNow;
    @SerializedName("weekday_text")
    @Expose
    private List<String> weekday_text;

    public String getOpenNow() {
        return openNow;
    }

    public List<String> getWeekday_text(){
        return weekday_text;
    }
}
