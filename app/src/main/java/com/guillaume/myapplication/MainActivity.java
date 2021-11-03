
package com.guillaume.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.guillaume.myapplication.R;
import com.guillaume.myapplication.databinding.ActivityMainBinding;
import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.ui.BaseActivity;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends BaseActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 123;

    ActivityMainBinding binding;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    GoogleSignInClient gsi;
    private FirebaseAuth mAuth;
    private FirestoreUserViewModel firestoreUserViewModel;


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestoreUserViewModel = Injection.provideFirestoreUserViewModel(this);
        mAuth = FirebaseAuth.getInstance();
    }


    // ACTION


    @Override
    protected void onResume() {
        super.onResume();
        configureGoogleAuth();

        binding.googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        onClickGoogleButton();
        onClickFacebookButton();
    }

    private void onClickGoogleButton() {
        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void onClickFacebookButton() {
        binding.facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureFacebookAuth();
            }
        });
    }

    // NAVIGATION

    private void startNavigationActivity() {
        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
    }

    private void signInWithGoogle() {
        Intent signInIntent = gsi.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI();
    }

    // AUTHENTIFICATION

    private void configureGoogleAuth() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsi = GoogleSignIn.getClient(this, gso);

    }

    private void configureFacebookAuth() {
        //binding.facebookLoginButton.setReadPermissions("email", "public_profile");
        binding.facebookLoginButton.setReadPermissions("email");
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        showSnackBar(binding.mainActivityLayout, getString(R.string.canceled));
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showSnackBar(binding.mainActivityLayout, getString(R.string.error));
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.w(TAG, "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code" + e.getStatusCode());
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.w(TAG, "signInWithCredential:success");
                            createUserInFirestore();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showSnackBar(binding.mainActivityLayout, getString(R.string.error));
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            createUserInFirestore();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI();
                        }
                    }
                });
    }

    private void updateUI() {
        if (isCurrentUserLogged()) {
            startNavigationActivity();
        }
    }


    // CUSTOMIZATION

    // UI

    private void showSnackBar(ConstraintLayout constraintLayout, String message) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG).show();
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore() {

        //todo verify and get user in the view model and not here
        if (isCurrentUserLogged()) {
            String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
            String username = getCurrentUser().getDisplayName();
            String uid = getCurrentUser().getUid();
            String radius = "1000";
            firestoreUserViewModel.createUser(uid, username, urlPicture, radius);
        }
    }
}
