package com.guillaume.myapplication.model;

import com.guillaume.myapplication.model.requests.OpeningHours;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Details {

    @SerializedName("formatted_phone_number")
    @Expose
    private String formatted_phone_number;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours opening_hours;
    @SerializedName("website")
    @Expose
    private String website;


    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public OpeningHours getOpening_hours() {
        return opening_hours;
    }

    public String getWebsite() {
        return website;
    }
}
