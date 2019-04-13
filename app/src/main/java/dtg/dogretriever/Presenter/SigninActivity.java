package dtg.dogretriever.Presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.Model.Profile;
import dtg.dogretriever.R;

public class SigninActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText mUserNameView;
    private EditText mPasswordView;
    private EditText mReEnterPasswordView;
    private EditText mFullNameView;
    private EditText mAddressView;
    private EditText mPhoneNumberView;
    private EditText mEmailView;
    private View mProgressView;
    private View mSigninFormView;
    private FirebaseAdapter firebaseAdapter;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signin);


    mUserNameView    = findViewById(R.id.signin_user_name);
    mPasswordView    = findViewById(R.id.signin_password);
    mReEnterPasswordView    = findViewById(R.id.signin_re_enter_password);
    mFullNameView    = findViewById(R.id.signin_full_name);
    mAddressView    = findViewById(R.id.signin_address);
    mPhoneNumberView    = findViewById(R.id.signin_phone_number);
    mEmailView    = findViewById(R.id.signin_email);

    firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();

    mAuth = FirebaseAuth.getInstance();

    Button mSignInButton = findViewById(R.id.sign_in_button);
    mSignInButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            attemptSignin();
        }
    });

    mSigninFormView = findViewById(R.id.signin_form);
    mProgressView = findViewById(R.id.signin_progress);


}

    private void attemptSignin() {

        //Reset errors
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mReEnterPasswordView.setError(null);
        mFullNameView.setError(null);
        mAddressView.setError(null);
        mPhoneNumberView.setError(null);
        mEmailView.setError(null);

        //Store values of signin attempt
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String reEnterPassword = mReEnterPasswordView.getText().toString();
        String fullName = mFullNameView.getText().toString();
        String address = mAddressView.getText().toString();
        String phoneNumber = mPhoneNumberView.getText().toString();
        String eMail = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //check if user name is valid
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isUserNameValid(userName)) {
            mUserNameView.setError(getString(R.string.error_invalid_user_name));
            focusView = mUserNameView;
            cancel = true;
        }



        // Check for a valid password.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password,reEnterPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //check if full name is valid
        if (TextUtils.isEmpty(fullName)) {
            mFullNameView.setError(getString(R.string.error_field_required));
            focusView = mFullNameView;
            cancel = true;
        } else if (!isUserNameValid(fullName)) {
            mFullNameView.setError(getString(R.string.error_invalid_full_name));
            focusView = mFullNameView;
            cancel = true;
        }
/*
        //check if address name is valid
        if (TextUtils.isEmpty(address)) {
            mAddressView.setError(getString(R.string.error_field_required));
            focusView = mAddressView;
            cancel = true;
        } else if (!isUserNameValid(address)) {
            mAddressView.setError(getString(R.string.error_invalid_address));
            focusView = mAddressView;
            cancel = true;
        }
*/
/*
        //check if phone number is valid
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        } else if (!isUserNameValid(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneNumberView;
            cancel = true;
        }
*/

/*
        // Check for a valid email address.
        if (TextUtils.isEmpty(eMail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(eMail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
*/
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            registerNewUser(userName,password,fullName,address,phoneNumber,eMail);
            /*
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        */
        }


    }

    private boolean isEmailValid(String eMail){
    if(eMail.contains("@") && eMail.contains("."))
        return true;
    else
        return false;
    }

    private boolean isPasswordValid(String password, String reEnterPassword) {
    if(password.compareTo(reEnterPassword)  == 0)
        return true;
    else
        return false;
    }

    private boolean isUserNameValid(String userName) {
    //TODO implement restrictions
    return true;
    }

    public void registerNewUser(final String userName, final String password, final String fullName, final String address, final String phoneNumber, final String eMail){
        mAuth.createUserWithEmailAndPassword(eMail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Profile.ProfileBuilder profileBuilder = new Profile.ProfileBuilder(user.getUid(),userName,fullName,password,eMail);
                            Profile tempProfile = profileBuilder.build();

                            //check for optionals
                            if(!TextUtils.isEmpty(address)){
                                tempProfile.setAddress(address);
                            }
                            if(!TextUtils.isEmpty(phoneNumber)) {
                                tempProfile.setPhoneNumber(phoneNumber);
                            }

                            //add new profile to firebase
                            firebaseAdapter.addUserToDataBase(tempProfile);

                            //TODO need to update preferences
                           // updateUI(user);
                           // Intent i = new Intent(getBaseContext(),ProfileActivity.class);
                            //startActivity(i);
                            Intent intent = new Intent(getBaseContext(), ToolbarActivity.class);
                            intent.putExtra("fragmentToOpen", 3);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SigninActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
                            onPostExecute(false);
/*
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
  */
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSigninFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    protected void onPostExecute(final Boolean success) {
        //if register fails it will finish the loading animation and get back to the register page
        showProgress(false);

        if (success) {
            finish();
        }
        /*
        else {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
*/

    }
}
