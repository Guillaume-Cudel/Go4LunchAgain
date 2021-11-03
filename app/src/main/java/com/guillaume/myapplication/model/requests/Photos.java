package com.guillaume.myapplication.model.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Photos {

    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("html_attributions")
    @Expose
    private List<String> html_attributions;
    @SerializedName("photo_reference")
    @Expose
    private String photo_reference;
    @SerializedName("width")
    @Expose
    private String width;

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public List<String> getHtml_attributions() {
        return html_attributions;
    }

    public String getPhotoReference() {
        return photo_reference;
    }
}
