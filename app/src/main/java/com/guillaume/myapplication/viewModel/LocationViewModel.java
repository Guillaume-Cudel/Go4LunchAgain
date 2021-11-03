package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class LocationViewModel extends ViewModel {

    private final MutableLiveData<LatLng> _locationLiveData = new MutableLiveData<>();
    public LiveData<LatLng> locationLiveData = _locationLiveData;

    private LatLng mLocation;


    public void setLocation(double latitude, double longitude) {
        this.mLocation = new LatLng(latitude, longitude);
        _locationLiveData.postValue(mLocation);
    }

    public LatLng getLocation() {
        return mLocation;
    }

}
