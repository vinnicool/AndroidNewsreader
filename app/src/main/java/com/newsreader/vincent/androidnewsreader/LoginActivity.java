package com.newsreader.vincent.androidnewsreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
{
    // UI references.
    //private AutoCompleteTextView mEmailView;
    //private EditText mPasswordView;
    //private View mProgressView;
    //private View mLoginFormView;
    private LoginViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vh = new LoginViewHolder(this);

        // Set up the login form.
        //mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        //mPasswordView = (EditText) findViewById(R.id.password);
        //mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
         //   @Override
         //   public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
          //      if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
          //          attemptLogin();
          //          return true;
          //      }
          //      return false;
          //  }
        //});

        //Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        //Button mRegisterButton = (Button) findViewById(R.id.register);

        vh.mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        vh.mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });


    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin()
    {
        // Reset errors.
        vh.mEmailView.setError(null);
        vh.mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = vh.mEmailView.getText().toString();
        String password = vh.mPasswordView.getText().toString();

//        boolean cancel = false;
//        View focusView = null;
//
//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            vh.mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = vh.mPasswordView;
//            cancel = true;
//        }
//
//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            vh.mEmailView.setError(getString(R.string.error_field_required));
//            focusView = vh.mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            vh.mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = vh.mEmailView;
//            cancel = true;
//        }

        if (validateForm(email, password)) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            loginAsync(new User(email, password));
        }
    }

    private boolean validateForm(String email, String password)
    {
        View focusView = null;
        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            vh.mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = vh.mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            vh.mEmailView.setError(getString(R.string.error_field_required));
            focusView = vh.mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            vh.mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = vh.mEmailView;
            cancel = true;
        }

        if(cancel)
        {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void attemptRegister()
    {
        vh.mEmailView.setError(null);
        vh.mPasswordView.setError(null);

        String email = vh.mEmailView.getText().toString();
        String password = vh.mPasswordView.getText().toString();

        if(validateForm(email, password))
        {
            showProgress(true);

            registerAsync();
        }
    }



    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void loginAsync(User user)
    {
        try
        {
            MainActivity.service.login(user).enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    if(response.isSuccessful() && response.body() != null)
                    {
                         MainActivity.authToken = response.body();
                         //Start new activity
                     }
                    else
                    {
                        invalidLogin();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t)
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e)
        {
            Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerAsync(User user)
    {
        try
        {
            MainActivity.service.register(user).enqueue(new Callback<CustomHttpResponse>()
            {
                @Override
                public void onResponse(Call<CustomHttpResponse> call, Response<CustomHttpResponse> response)
                {
                    if(response.isSuccessful() && response.body() != null)
                    {
                        if(response.body().Sucess)
                        {
                            //Start new activity
                        }
                        else
                        {
                            invalidLogin();
                        }
                    }
                    else
                    {
                        invalidLogin();
                    }
                }

                @Override
                public void onFailure(Call<CustomHttpResponse> call, Throwable t)
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e)
        {
            invalidLogin();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            vh.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            vh.mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vh.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            vh.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            vh.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vh.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            vh.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            vh.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

//    @Override
//    public void onResponse(Call<String> call, Response<String> response) {
//        if(response.isSuccessful() && response.body() != null)
//        {
//            MainActivity.authToken = response.body();
//
//        }
//        else
//        {
//            invalidLogin();
//        }
//    }

    private void invalidLogin() {
        vh.mEmailView.setError(getString(R.string.error_incorrect_email));
        vh.mPasswordView.setError(getString(R.string.error_incorrect_password));

        vh.mEmailView.requestFocus();
    }

//    @Override
////    public void onFailure(Call<String> call, Throwable t) {
////        Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
////    }

    private class LoginViewHolder
    {
        public AutoCompleteTextView mEmailView;
        public EditText mPasswordView;

        public Button mSignInButton;
        public Button mRegisterButton;

        public View mLoginFormView;
        public View mProgressView;

        private LoginActivity loginActivity;

        public LoginViewHolder(LoginActivity loginActivity)
        {
            this.loginActivity = loginActivity;

            mEmailView = findViewById(R.id.email);
            mPasswordView = findViewById(R.id.password);

            mSignInButton = findViewById(R.id.email_sign_in_button);
            mRegisterButton = findViewById(R.id.register);

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
    }

}

