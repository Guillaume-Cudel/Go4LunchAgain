package com.guillaume.myapplication;

import static com.guillaume.myapplication.R.string.notification_actived;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.guillaume.myapplication.databinding.ActivityNavigationBinding;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.model.requests.Photos;
import com.guillaume.myapplication.notification.AlarmReceiver;
import com.guillaume.myapplication.search.SuggestionSimpleCursorAdapter;
import com.guillaume.myapplication.search.SuggestionsDatabase;
import com.guillaume.myapplication.ui.BaseActivity;
import com.guillaume.myapplication.ui.chat.ChatActivity;
import com.guillaume.myapplication.ui.map.MapFragment;
import com.guillaume.myapplication.ui.restaurant_profil.RestaurantProfilActivity;
import com.guillaume.myapplication.ui.restaurants_list.RestaurantsListFragment;
import com.guillaume.myapplication.ui.workmates.WorkmatesFragment;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;
import com.guillaume.myapplication.viewModel.LocationViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class NavigationActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private ActivityNavigationBinding binding;
    private BottomNavigationView mBottomNavigationView;
    private LocationViewModel locationViewModel;
    private FirestoreUserViewModel firestoreUserViewModel;
    private LocationCallback locationCallback;
    private final FirebaseUser currentUser = this.getCurrentUser();
    private UserFirebase mCurrentUser;
    private int mRadius, id;
    private SearchView searchView;
    private SuggestionsDatabase database;
    private List<Restaurant> currentRestaurantsDisplayed = new ArrayList<Restaurant>();

    public Fragment fragmentMap;
    public Fragment fragmentRestaurantsList;
    public Fragment fragmentWorkmates;

    private static final int FRAGMENT_MAP = 0;
    private static final int FRAGMENT_RESTAURANT = 1;
    private static final int FRAGMENT_WORKMATES = 2;
    private static int DELTA_VALUE = 500;

    // Easy location
    private static final int REQUEST_LOCATION_PERMISSION = 10;


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_navigation;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_bottom_nav_view);

        locationViewModel = Injection.provideLocationViewModel(this);
        firestoreUserViewModel = Injection.provideFirestoreUserViewModel(this);
        database = new SuggestionsDatabase(this);

        // Show the first fragment when starting activity
        fragmentMap = new MapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragmentMap);
        fragmentTransaction.commit();


        // Toolbar configuration
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        updateUIWhenCreating();
        onClickItemsDrawer();
        setCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentUser != null) {
            firestoreUserViewModel.getUser(currentUser.getUid()).observe(this, new Observer<UserFirebase>() {
                @Override
                public void onChanged(UserFirebase userFirebase) {
                    mCurrentUser = userFirebase;
                    String radiusString = userFirebase.getCurrentRadius();
                    mRadius = Integer.parseInt(radiusString);
                }
            });
        }

        recoveCurrentRestaurantsDisplayed();
    }

    // UI

    private void updateUIWhenCreating() {
        View header = binding.navigationDrawerNavView.getHeaderView(0);
        ImageView profilImage = (ImageView) header.findViewById(R.id.profilImage);
        TextView profilUsername = (TextView) header.findViewById(R.id.profil_name);
        TextView profilUsermail = (TextView) header.findViewById(R.id.profil_mail);

        if (currentUser != null) {

            //Get picture URL from Firebase
            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profilImage);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();
            String username = TextUtils.isEmpty(currentUser.getDisplayName()) ? getString(R.string.info_no_username_found) : currentUser.getDisplayName();

            //Update views with data
            profilUsername.setText(username);
            profilUsermail.setText(email);
        }
    }

    @Override
    public void onBackPressed() {
        //  Handle back click to close menu
        if (this.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ACTION

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search restaurants !");

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);

        return true;
    }

    private void onClickItemsDrawer() {
        NavigationView navView = binding.navigationDrawerNavView;
        if (navView != null) {
            setupDrawerContent(navView);
        }
        setUpBottomContent(mBottomNavigationView);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpBottomContent(BottomNavigationView bottomContent) {
        bottomContent.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //  Handle Navigation Item Click
        id = item.getItemId();
        item.setChecked(true);
        binding.drawerLayout.closeDrawers();

        switch (id) {
            case R.id.nav_your_lunch:
                showRestaurantChoosed();
                break;

            case R.id.workmates_chat:
                openChatActivity();
                break;

            case R.id.nav_settings:
                openSettings();
                break;
            case R.id.nav_log_out:
                //todo verify this
                //FirebaseAuth.getInstance().signOut();
                finish();
                AuthUI.getInstance().signOut(this);
                break;

            case R.id.navigation_map:
                showFragment(FRAGMENT_MAP);
                break;

            case R.id.navigation_restaurants_list:
                showFragment(FRAGMENT_RESTAURANT);
                break;

            case R.id.navigation_workmates:
                showFragment(FRAGMENT_WORKMATES);
                break;

            default:
                break;
        }
        this.binding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    // VIEW

    private void showFragment(int fragmentID) {
        switch (fragmentID) {
            case FRAGMENT_MAP:
                this.showMapFragment();
                break;

            case FRAGMENT_RESTAURANT:
                this.showRestaurantsListFragment();
                break;

            case FRAGMENT_WORKMATES:
                this.showWorkmatesFragment();
                break;

            default:
                break;
        }
    }


    private void showMapFragment() {
        if (fragmentMap == null) {
            fragmentMap = MapFragment.newInstance();
            startTransactionFragment(fragmentMap);
            return;
        }/*else {
            // Refresh your fragment here
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(fragmentMap);
            fragmentMap = MapFragment.newInstance();
            transaction.commitAllowingStateLoss();
            startTransactionFragment(fragmentMap);
            Log.w("IsRefresh", "Yes");
        }*/
        startTransactionFragment(fragmentMap);
    }

    private void showRestaurantsListFragment() {
        if (fragmentRestaurantsList == null) {
            fragmentRestaurantsList = RestaurantsListFragment.newInstance();
            startTransactionFragment(fragmentRestaurantsList);
            return;
        }
        startTransactionFragment(fragmentRestaurantsList);
    }

    private void showWorkmatesFragment() {
        if (fragmentWorkmates == null) {
            fragmentWorkmates = WorkmatesFragment.newInstance();
            startTransactionFragment(fragmentWorkmates);
            return;
        }
        startTransactionFragment(fragmentWorkmates);
    }

    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment).commit();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        requestLocationPermission();
    }

    // Location

    // Configure EASY location request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(2000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    // todo check it
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            locationViewModel.setLocation(location.getLatitude(), location.getLongitude());
                        }
                    }
                }
            };

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null) {
                        startLocationUpdates(locationRequest, locationCallback);
                    }
                    if (location != null) {
                        locationViewModel.setLocation(location.getLatitude(), location.getLongitude());
                        stopLocationUpdates(locationCallback);
                    }
                }
            });
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.locationNotGranted), REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    private void startLocationUpdates(LocationRequest locationRequest, LocationCallback locationCallback) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

    }

    private void showRestaurantChoosed() {
        if (mCurrentUser.getRestaurantChoosed() == null) {
            //todo put alertdialog instead Toast ---> check
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You have not chosen a restaurant.");
            builder.setMessage("Please, choose a restaurant.");
            builder.setNegativeButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            firestoreUserViewModel.getRestaurant(mCurrentUser.getUid(), mCurrentUser.getRestaurantChoosed()).observe(this, new Observer<Restaurant>() {
                @Override
                public void onChanged(Restaurant restaurant) {
                    Intent i = new Intent(NavigationActivity.this, RestaurantProfilActivity.class);
                    i.putExtra("place_id", restaurant.getPlace_id());
                    i.putExtra("name", restaurant.getName());
                    i.putExtra("photo", restaurant.getPhotoReference());
                    i.putExtra("photoWidth", restaurant.getPhotoWidth());
                    i.putExtra("vicinity", restaurant.getVicinity());
                    i.putExtra("type", restaurant.getType());
                    i.putExtra("rate", restaurant.getRating());
                    startActivity(i);

                }
            });
        }
    }

    private void openSettings() {
        if (mCurrentUser != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.settings_dialog_title);

            final View customWindow = getLayoutInflater().inflate(R.layout.settings_radius_window, null);
            SeekBar radiusBar = customWindow.findViewById(R.id.seekBar);
            EditText radiusText = customWindow.findViewById(R.id.seekBar_text);

            Button increaseButton = customWindow.findViewById(R.id.increase_button);
            Button decreaseButton = customWindow.findViewById(R.id.decrease_button);

            radiusBar.setMax(5000);
            radiusBar.setProgress(mRadius);

            String sProgress = getString(R.string.progress);
            String progressText = sProgress + radiusBar.getProgress() + "/" + radiusBar.getMax();
            radiusText.setText(progressText);
            builder.setView(customWindow);

            radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                    String changingSeekBar = getString(R.string.changing_scope);
                    String progressTextChanged = sProgress + progressValue + "/" + radiusBar.getMax();
                    radiusText.setText(progressTextChanged);
                    Toast.makeText(getApplicationContext(), changingSeekBar, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    String startSeekBar = getString(R.string.start_seekBar);
                    Toast.makeText(getApplicationContext(), startSeekBar, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String progressText = sProgress + mRadius + "/" + seekBar.getMax();
                    String stoppedSeekBar = "Stopped tracking seekbar";
                    radiusText.setText(progressText);
                    Toast.makeText(getApplicationContext(), stoppedSeekBar, Toast.LENGTH_SHORT).show();

                }
            });

            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int progressNumber = radiusBar.getProgress();
                    if (progressNumber - DELTA_VALUE < 0) {
                        radiusBar.setProgress(0);
                    } else {
                        radiusBar.setProgress(progressNumber - DELTA_VALUE);
                    }
                    String progressText = sProgress + radiusBar.getProgress() + "/" + radiusBar.getMax();
                    radiusText.setText(progressText);
                }
            });

            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int progressNumber = radiusBar.getProgress();
                    if (progressNumber + DELTA_VALUE > radiusBar.getMax()) {
                        radiusBar.setProgress(0);
                    } else {
                        radiusBar.setProgress(progressNumber + DELTA_VALUE);
                    }
                    String progressText = sProgress + radiusBar.getProgress() + "/" + radiusBar.getMax();
                    radiusText.setText(progressText);
                }
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int currentRadius = radiusBar.getProgress();
                    String finalRadius = String.valueOf(currentRadius);
                    firestoreUserViewModel.updateRadius(currentUser.getUid(), finalRadius);
                    //showMapFragment();
                    locationViewModel.refreshMap(finalRadius);
                    dialog.cancel();
                }
            });

            builder.setNegativeButton("Cancel", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void setCurrentUser() {
        locationViewModel.setCurrentUser(currentUser);
    }

    private void recoveCurrentRestaurantsDisplayed() {
        locationViewModel.currentRestaurantsDisplayedLiveData.observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                currentRestaurantsDisplayed = restaurants;
                database.deleteOldSuggestions();
                for (Restaurant r : currentRestaurantsDisplayed) {
                    database.insertSuggestion(r.getName());
                }
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        for (Restaurant r : currentRestaurantsDisplayed) {
            if (query.equals(r.getName())) {
                displayProfilRestaurant(r);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Cursor cursor = database.getSuggestions(newText);
        if (cursor.getCount() != 0) {
            String[] columns = new String[]{SuggestionsDatabase.FIELD_SUGGESTION};
            int[] columnTextId = new int[]{android.R.id.text1};

            SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(),
                    R.layout.listview_item, cursor,
                    columns, columnTextId
                    , 0);

            searchView.setSuggestionsAdapter(simple);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        SQLiteCursor cursor = (SQLiteCursor) searchView.getSuggestionsAdapter().getItem(position);
        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);

        searchView.setQuery(cursor.getString(indexColumnSuggestion), true);
        return true;
    }

    private void displayProfilRestaurant(Restaurant restaurant){
        String place_id = restaurant.getPlace_id();
        String rating = restaurant.getRating();
        String type = null;
        String photoData = null;
        String photoWidth = null;
        if (restaurant.getTypes() != null) {
            type = restaurant.getTypes().get(0);
        }

        Intent i = new Intent(this, RestaurantProfilActivity.class);
        i.putExtra("place_id", place_id);
        i.putExtra("name", restaurant.getName());
        if (restaurant.getPhotos() != null) {
            Photos restaurantPhoto = restaurant.getPhotos().get(0);
            photoData = restaurantPhoto.getPhotoReference();
            photoWidth = restaurantPhoto.getWidth();
        }
        i.putExtra("photo", photoData);
        i.putExtra("photoWidth", photoWidth);
        i.putExtra("vicinity", restaurant.getVicinity());
        i.putExtra("type", type);
        i.putExtra("rate", rating);
        startActivity(i);
    }

    private void openChatActivity(){
        Intent i = new Intent(this, ChatActivity.class);
        startActivity(i);
    }
}
