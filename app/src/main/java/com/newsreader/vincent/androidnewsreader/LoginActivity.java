package com.newsreader.vincent.androidnewsreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity
{
    // UI references.
    //private AutoCompleteTextView mUsernameView;
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
        //mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);

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
        vh.mContinueNoLogin.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                continueWithoutLogin();
            }
        });

    }

    private void continueWithoutLogin()
    {
        Intent intent = new Intent(LoginActivity.this, NewsOverviewActivity.class);
        startActivity(intent);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin()
    {
        // Reset errors.
        vh.mUsernameView.setError(null);
        vh.mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = vh.mUsernameView.getText().toString();
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
//            vh.mUsernameView.setError(getString(R.string.error_field_required));
//            focusView = vh.mUsernameView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            vh.mUsernameView.setError(getString(R.string.error_invalid_email));
//            focusView = vh.mUsernameView;
//            cancel = true;
//        }

        if (validateForm(username, password)) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Utils.hideSoftKeyboard(this);
            loginAsync(new User(username, password));
        }
    }

    private boolean validateForm(String username, String password)
    {
        View focusView = null;
        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            vh.mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = vh.mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            vh.mUsernameView.setError(getString(R.string.error_field_required));
            focusView = vh.mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            vh.mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = vh.mUsernameView;
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
        vh.mUsernameView.setError(null);
        vh.mPasswordView.setError(null);

        String username = vh.mUsernameView.getText().toString();
        String password = vh.mPasswordView.getText().toString();

        if(validateForm(username, password))
        {
            showProgress(true);

            registerAsync(new User(username, password));
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    private void loginAsync(User user)
    {
        try
        {
            NewsReaderApplication.getApiService().login(user).enqueue(new Callback<AuthTokenHttpResponse>()
            {
                @Override
                public void onResponse(Call<AuthTokenHttpResponse> call, Response<AuthTokenHttpResponse> response)
                {
                    if(response.isSuccessful() && response.body() != null)
                    {
                         NewsReaderApplication.authToken = response.body().authToken;
                         NewsReaderApplication.getPreferences().edit().putString(NewsReaderApplication.authKey, response.body().authToken).apply();
                         //Start new activity
                         Intent intent = new Intent(LoginActivity.this, NewsOverviewActivity.class);
                         startActivity(intent);
                     }
                    else
                    {
                        showProgress(false);
                        invalidLogin();
                    }
                }

                @Override
                public void onFailure(Call<AuthTokenHttpResponse> call, Throwable t)
                {
                    showProgress(false);
                    Log.e(LoginActivity.class.toString(), "An excpetion ocurred",t);
                    Toast.makeText(LoginActivity.this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception e)
        {
            showProgress(false);
            Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerAsync(final User user)
    {
        try
        {
            NewsReaderApplication.getApiService().register(user).enqueue(new Callback<CustomHttpResponse>()
            {
                @Override
                public void onResponse(Call<CustomHttpResponse> call, Response<CustomHttpResponse> response)
                {
                    if(response.isSuccessful() && response.body() != null)
                    {
                        if(response.body().Success)
                        {
                              Toast.makeText(LoginActivity.this, R.string.registered, Toast.LENGTH_SHORT).show();
//                            MainActivity.preferences.edit().putString(MainActivity.authKey, response.body().Message);
//                            //Start new activity
//                            Intent intent = new Intent(LoginActivity.this, NewsOverviewActivity.class);
//                            startActivity(intent);
                        }
                        else
                        {
                            showProgress(false);
                            invalidLogin();
                        }
                    }
                    else
                    {
                        showProgress(false);
                        invalidLogin();
                    }
                }

                @Override
                public void onFailure(Call<CustomHttpResponse> call, Throwable t)
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
        }
        catch(Exception ex)
        {
            Log.e(LoginActivity.class.toString(), ex.toString());
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
        vh.mUsernameView.setError(getString(R.string.error_incorrect_username));
        vh.mPasswordView.setError(getString(R.string.error_incorrect_password));


        vh.mUsernameView.requestFocus();
    }

//    @Override
////    public void onFailure(Call<String> call, Throwable t) {
////        Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
////    }

    private class LoginViewHolder
    {
        public AutoCompleteTextView mUsernameView;
        public EditText mPasswordView;

        public Button mSignInButton;
        public Button mRegisterButton;

        public View mLoginFormView;
        public View mProgressView;

        public Button mContinueNoLogin;

        private LoginActivity loginActivity;

        public LoginViewHolder(LoginActivity loginActivity)
        {
            this.loginActivity = loginActivity;

            mUsernameView = findViewById(R.id.username);
            mPasswordView = findViewById(R.id.password);

            mSignInButton = findViewById(R.id.username_sign_in_button);
            mRegisterButton = findViewById(R.id.register);

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);

            mContinueNoLogin = findViewById(R.id.continue_no_login);
        }
    }

}

