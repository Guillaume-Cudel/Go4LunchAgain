package com.guillaume.myapplication.model;

import com.guillaume.myapplication.model.requests.Geometry;
import com.guillaume.myapplication.model.requests.OpeningHours;
import com.guillaume.myapplication.model.requests.Photos;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Restaurant {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours opening_hours;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("photos")
    @Expose
    private List<Photos> photos;
    @SerializedName("types")
    @Expose
    private List<String> types;
    @SerializedName("place_id")
    @Expose
    private String place_id;
    private int participantsNumber;
    private Details details;
    private String photoReference, photoWidth, type;

    public Restaurant(){

    }

    public Restaurant(String name, String vicinity, OpeningHours opening_hours, Geometry geometry,
                      String rating, List<Photos> photos, List<String> types) {

        this.name = name;
        this.vicinity = vicinity;
        this.opening_hours = opening_hours;
        this.geometry = geometry;
        this.rating = rating;
        this.photos = photos;
        this.types = types;
    }

    public Restaurant(String placeID, String photoData, String photoWidth, String name,
                      String vicinity, String type, String rating, Geometry geometry, Details detail, OpeningHours openingHours){
        this.place_id = placeID;
        this.photoReference = photoData;
        this.photoWidth = photoWidth;
        this.name = name;
        this.vicinity = vicinity;
        this.type = type;
        this.rating = rating;
        this.geometry = geometry;
        this.details = detail;
        this.opening_hours = openingHours;
    }

    public Restaurant(String placeID, String photoData, String photoWidth, String name,
                      String vicinity, String type, String rating){
        this.place_id = placeID;
        this.photoReference = photoData;
        this.photoWidth = photoWidth;
        this.name = name;
        this.vicinity = vicinity;
        this.type = type;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public OpeningHours getOpening_hours() {
        return opening_hours;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public List<Photos> getPhotos() {
        return photos;
    }

    public String getRating() {
        return rating;
    }

    public List<String> getTypes() {
        return types;
    }

    public String getPlace_id() {
        return place_id;
    }

    public Details getDetails() {
        return details;
    }

    public int getParticipantsNumber() {
        return participantsNumber;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public String getPhotoWidth() {
        return photoWidth;
    }

    public String getType() {
        return type;
    }

    public void setDetails(Details details) {
        this.details = details;
    }
}
