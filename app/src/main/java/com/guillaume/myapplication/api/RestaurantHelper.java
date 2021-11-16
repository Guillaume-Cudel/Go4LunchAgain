package com.guillaume.myapplication.api;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.model.requests.Geometry;
import com.guillaume.myapplication.model.requests.OpeningHours;
import com.guillaume.myapplication.model.requests.Photos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

public class RestaurantHelper {

    private static final String COLLECTION_RESTAURANT = "restaurants";
    private static final String COLLECTION_RESTAURANT_LIKED = "usersRestaurantLiked";
    private static final String COLLECTION_USER = "usersToRestaurants";
    private static final String PARTICIPANTS_NUMBER = "participantsNumber";



    // Get the Collection Reference
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANT);
    }

    public static CollectionReference getUsersCollection(String placeID){
        return getRestaurantsCollection().document(placeID).collection(COLLECTION_USER);
    }

    public static CollectionReference getUsersRestaurantLikedCollection(String placeID){
        return getRestaurantsCollection().document(placeID).collection(COLLECTION_RESTAURANT_LIKED);
    }

    // --- CREATE ---

    public static void createRestaurant(String placeID, String photoData, String photoWidth, String name,
                                        String vicinity, String type, String rating, Geometry geometry, Details detail, OpeningHours openingHours) {
        Restaurant restaurantToCreate = new Restaurant(placeID, photoData, photoWidth, name, vicinity, type, rating, geometry, detail, openingHours);
        RestaurantHelper.getRestaurantsCollection().document(placeID).set(restaurantToCreate).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

    }

    public static void createRestaurantUser(String placeID, String uid, String username, String urlPicture) {
        UserFirebase userToCreate = new UserFirebase(uid, username, urlPicture);
        RestaurantHelper.getUsersCollection(placeID).document(uid).set(userToCreate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete !!!");
            }
        });
    }

    public static void createRestaurantLikedUser(String placeID, String uid, String username, String urlPicture){
        UserFirebase userToCreate = new UserFirebase(uid, username, urlPicture);
        RestaurantHelper.getUsersRestaurantLikedCollection(placeID).document(uid).set(userToCreate);
    }

    // -------- GET ----------

    public interface GetAllUsersCallback {
        void onSuccess(List<UserFirebase> list);

        void onError(Exception exception);
    }

    public static void getAllUsers(String placeID, GetAllUsersCallback callback){
        RestaurantHelper.getUsersCollection(placeID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    callback.onError(new Exception());
                }

                List<UserFirebase> users = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    UserFirebase user = doc.toObject(UserFirebase.class);
                    users.add(user);
                }
                callback.onSuccess(users);
            }
        });
    }

    public interface GetAllRestaurantssCallback {
        void onSuccess(List<Restaurant> list);

        void onError(Exception exception);
    }

    public static void getAllRestaurants(GetAllRestaurantssCallback callback){
        RestaurantHelper.getRestaurantsCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    callback.onError(new Exception());
                }
                List<Restaurant> restaurants = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    Restaurant restaurant = doc.toObject(Restaurant.class);
                    restaurants.add(restaurant);
                }
                callback.onSuccess(restaurants);
            }
            });
    }

    public interface GetUserTargetedCallback{
        void onSuccess(UserFirebase user);

        void onError( Exception exception);
    }

    public static void getTargetedUserCallback(String placeID, String uid, GetUserTargetedCallback callback){
        DocumentReference docRef = RestaurantHelper.getUsersCollection(placeID).document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                UserFirebase user = new UserFirebase();
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(UserFirebase.class);
                }else{
                    callback.onError(new Exception());
                }
                callback.onSuccess(user);
            }
        });
    }

    public interface GetRestaurantsTargetedCallback {
        void onSuccess(Restaurant restaurant);

        void onError(Exception exception);
    }

    public static void getTargetedRestaurant(String placeId, GetRestaurantsTargetedCallback callback){
        DocumentReference docRef = RestaurantHelper.getRestaurantsCollection().document(placeId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    callback.onError(new Exception());
                }
                Restaurant restaurant = new Restaurant();
                if (value != null && value.exists()){
                    restaurant = value.toObject(Restaurant.class);
                }
                callback.onSuccess(restaurant);
            }
        });
    }

    public interface GetUserRestaurantLikedCallback{
        void onSuccess(UserFirebase user);

        void onError(Exception exception);
    }

    public static void getUserRestaurantLiked(String placeID, String uid, GetUserRestaurantLikedCallback callback){
        DocumentReference docRef = RestaurantHelper.getUsersRestaurantLikedCollection(placeID).document(uid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    callback.onError(new Exception());
                }
                UserFirebase user = new UserFirebase();
                if (value != null && value.exists()){
                    user = value.toObject(UserFirebase.class);
                }else{
                    user = null;
                }
                callback.onSuccess(user);
            }
        });
    }

    // --- UPDATE ---


    public static void updateParticipantNumber(String placeID, boolean addParticipant){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = RestaurantHelper.getRestaurantsCollection().document(placeID);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                if(addParticipant) {
                    double newParticipantNumber = snapshot.getDouble(PARTICIPANTS_NUMBER) +1;
                    transaction.update(docRef, PARTICIPANTS_NUMBER, newParticipantNumber);
                }else{
                    double newParticipantNumber = snapshot.getDouble(PARTICIPANTS_NUMBER) -1;
                    transaction.update(docRef, PARTICIPANTS_NUMBER, newParticipantNumber);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });

    }

    // --- DELETE ---
    public static void deleteParticipant(String placeID, String uid) {
        RestaurantHelper.getUsersCollection(placeID).document(uid).delete();
    }

    public static void deleteUserRestaurantLiked(String placeID, String uid){
        RestaurantHelper.getUsersRestaurantLikedCollection(placeID).document(uid).delete();
    }



}
