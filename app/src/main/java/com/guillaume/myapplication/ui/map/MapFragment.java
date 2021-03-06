package com.guillaume.myapplication.ui.map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.guillaume.myapplication.NavigationActivity;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.model.requests.Geometry;
import com.guillaume.myapplication.model.requests.OpeningHours;
import com.guillaume.myapplication.ui.BaseActivity;
import com.guillaume.myapplication.ui.restaurant_profil.RestaurantProfilActivity;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.requests.Photos;
import com.guillaume.myapplication.viewModel.FirestoreRestaurantViewModel;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;
import com.guillaume.myapplication.viewModel.UtilsViewModel;
import com.guillaume.myapplication.viewModel.RestaurantViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener {

    private LatLng mLatlng;
    private GoogleMap map;
    private List<Restaurant> restaurantsList = new ArrayList<>();
    private List<Restaurant> restaurantsSaved = new ArrayList<>();
    private NavigationActivity navActivity;
    private RestaurantViewModel mRestaurantVM;
    private FirestoreRestaurantViewModel mFirestoreRestaurantVM;
    private FirestoreUserViewModel mFirestoreUserVM;
    private UtilsViewModel utilsViewModel;
    private ProgressDialog loading;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser authUser = mAuth.getCurrentUser();
    private String userUid, mRadius;
    private boolean permissionOK;


    public static MapFragment newInstance() {
        return (new MapFragment());
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if(authUser != null){
            userUid = authUser.getUid();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loading = ProgressDialog.show(getActivity(), "", getString(R.string.messageRecovingRestaurants), true);
        navActivity = (NavigationActivity) getActivity();
        utilsViewModel = new ViewModelProvider(navActivity).get(UtilsViewModel.class);
        mRestaurantVM = Injection.provideRestaurantViewModel(getActivity());
        mFirestoreRestaurantVM = Injection.provideFirestoreRestaurantViewModel(getActivity());
        mFirestoreUserVM = Injection.provideFirestoreUserViewModel(getActivity());

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getChildFragmentManager().beginTransaction().replace(R.id.map_fragment, mapFragment).commit();

        refreshMap();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // set maximal and minimal zoom
        map.setMinZoomPreference(10.0f);
        map.setMaxZoomPreference(18.0f);

        //Display Toulouse when the map starting
        LatLngBounds toulouseBounds = new LatLngBounds(
                new LatLng(43.3, 1.3), // SW bounds
                new LatLng(43.9, 1.6)  // NE bounds
        );
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(toulouseBounds.getCenter(), 12));

        // verify permission
        verifyPermission();

        if(!permissionOK){
            verifyPermission();
        }
        if(userUid != null) {
            recoveRadius();
        }
    }

    private void verifyPermission(){
        permissionOK = true;
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionOK = false;
            return;
        }
        // filter my position
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnInfoWindowClickListener(this);
    }

    private void refreshMap() {
        utilsViewModel.refreshLiveData.observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(userUid != null && authUser != null) {
                    map.clear();
                    recoveRadius();
                }
            }
        });
    }

    public void recoveRadius() {
        mFirestoreUserVM.getUser(userUid).observe(requireActivity(), new Observer<UserFirebase>() {
            @Override
            public void onChanged(UserFirebase user) {
                mRadius = user.getCurrentRadius();
                recoveLocation();
            }
        });
    }

    private void recoveLocation(){
        utilsViewModel.locationLiveData.observe(requireActivity(), new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng) {
                mLatlng = latLng;
                plotBlueDot(mLatlng.latitude, mLatlng.longitude);
                recoveRestaurantsFromDatabase(mLatlng.latitude, mLatlng.longitude);
            }
        });
    }

    private void recoveRestaurantsFromDatabase(double latitude, double longitude) {
        mFirestoreRestaurantVM.getAllRestaurants().observe(requireActivity(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                MapFragment.this.restaurantsSaved.clear();
                MapFragment.this.restaurantsSaved.addAll(restaurants);
                recoveRestaurantsFromPlace(latitude, longitude, mRadius);
            }
        });
    }

    private void recoveRestaurantsFromPlace(double latitude, double longitude, String scope) {
        String locationText = latitude + "," + longitude;

        if (scope != null) {
            mRestaurantVM.getRestaurants(locationText, scope).observe(requireActivity(), new Observer<List<Restaurant>>() {
                @Override
                public void onChanged(List<Restaurant> restaurants) {
                    MapFragment.this.restaurantsList.clear();
                    MapFragment.this.restaurantsList.addAll(restaurants);
                    utilsViewModel.setCurrentRestaurantsDisplayed(restaurantsList);

                    for (int i = 0; i < restaurantsList.size(); i++) {
                        boolean isSaved = false;
                        Restaurant restaurant = restaurantsList.get(i);
                        String rate = null;
                        String type = restaurant.getTypes().get(0);
                        String photoData = null;
                        String photoWidth = null;
                        List<Photos> photoInformation;
                        if (restaurant.getRating() != null) {
                            rate = restaurant.getRating();
                        }
                        if (restaurant.getPhotos() != null) {
                            photoInformation = restaurant.getPhotos();
                            photoData = photoInformation.get(0).getPhotoReference();
                            photoWidth = photoInformation.get(0).getWidth();
                        }
                        if(restaurantsSaved.size() > 0) {
                            for (Restaurant r : restaurantsSaved) {
                                if (r.getPlace_id().equals(restaurant.getPlace_id())) {
                                    isSaved = true;
                                    break;
                                }
                            }
                            if (!isSaved) {
                                addRestaurantsToDatabase(restaurant.getPlace_id(), photoData, photoWidth, restaurant.getName(), restaurant.getVicinity(),
                                        type, rate, restaurant.getGeometry(), restaurant.getDetails(), restaurant.getOpening_hours());
                            }
                        }else{
                            addRestaurantsToDatabase(restaurant.getPlace_id(), photoData, photoWidth, restaurant.getName(), restaurant.getVicinity(),
                                    type, rate, restaurant.getGeometry(), restaurant.getDetails(), restaurant.getOpening_hours());
                        }
                    }
                    markRestaurantsFromDatabase();
                }
            });
        }

    }

    private void addRestaurantsToDatabase(String placeID, String photoData, String photoWidth, String name, String vicinity,
                                          String type, String rating, Geometry geometry, Details detail, OpeningHours openingHours) {
        mFirestoreRestaurantVM.createRestaurant(placeID, photoData, photoWidth, name,
                vicinity, type, rating, geometry, detail, openingHours);
    }

    private void markRestaurantsFromDatabase() {
        int radius = Integer.parseInt(mRadius);
        if (restaurantsList.size() > 0) {

            for (int i = 0; i < restaurantsSaved.size(); i++) {
                Restaurant restaurant = restaurantsSaved.get(i);
                LatLng restaurantLocation = recoveLatLng(restaurant);
                String infoRate;
                if (restaurant.getRating() != null) {
                    infoRate = "Rate: " + restaurant.getRating();
                } else {
                    infoRate = "No rating";
                }

                int distance = displayRestaurantsWithinRange(restaurantLocation);
                if (distance < radius) {
                    if (restaurant.getParticipantsNumber() > 0) {
                        setGreenMarkers(restaurantLocation, restaurant.getName(), infoRate, restaurant);
                    } else {
                        setRedMarkers(restaurantLocation, restaurant.getName(), infoRate, restaurant);
                    }
                }
            }
            loading.cancel();
        }
    }

    private int displayRestaurantsWithinRange(LatLng restaurantLocation) {
        double currentLatitude = mLatlng.latitude;
        double currentLongitude = mLatlng.longitude;

        Location loc1 = new Location("");
        loc1.setLatitude(currentLatitude);
        loc1.setLongitude(currentLongitude);

        Location loc2 = new Location("");
        loc2.setLatitude(restaurantLocation.latitude);
        loc2.setLongitude(restaurantLocation.longitude);

        float distanceInMeters = loc1.distanceTo(loc2);
        int distanceRound = (int) distanceInMeters;

        return distanceRound;
    }

    private void setRedMarkers(LatLng location, String title, String rate, Restaurant restaurant) {
        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_restaurant_red);
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        Marker restaurantMarker = map.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .snippet(rate)
                .icon(descriptor)
        );
        restaurantMarker.setTag(restaurant);
    }

    private void setGreenMarkers(LatLng location, String title, String rate, Restaurant restaurant) {
        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_restaurant_green);
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        Marker restaurantMarker = map.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .snippet(rate)
                .icon(descriptor)
        );
        restaurantMarker.setTag(restaurant);
    }


    private void plotBlueDot(double latitude, double longitude) {

            if (map != null) {
                LatLng myPosition = new LatLng(latitude, longitude);

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                map.animateCamera(zoom);
            }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Restaurant restaurant = (Restaurant) marker.getTag();
        if (restaurant != null) {
            String place_id = restaurant.getPlace_id();
            String rating = restaurant.getRating();
            String type = null;
            String photoData = null;
            String photoWidth = null;
            if (restaurant.getTypes() != null) {
                type = restaurant.getTypes().get(0);
            }

            Intent i = new Intent(getActivity(), RestaurantProfilActivity.class);
            i.putExtra("place_id", place_id);
            i.putExtra("name", restaurant.getName());
            if (restaurant.getPhotoReference() != null) {
                photoData = restaurant.getPhotoReference();
                photoWidth = restaurant.getPhotoWidth();
            }
            i.putExtra("photo", photoData);
            i.putExtra("photoWidth", photoWidth);
            i.putExtra("vicinity", restaurant.getVicinity());
            i.putExtra("type", type);
            i.putExtra("rate", rating);
            getActivity().startActivity(i);
        }
    }

    // Convert vector drawable to bitmap for get personalized marker icon
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private LatLng recoveLatLng(Restaurant r) {
        String restaurantLatitude = r.getGeometry().getLocation().getLat();
        String restaurantLongitude = r.getGeometry().getLocation().getLng();
        double rLatitude = Double.parseDouble(restaurantLatitude);
        double rLongitude = Double.parseDouble(restaurantLongitude);

        return new LatLng(rLatitude, rLongitude);
    }

}