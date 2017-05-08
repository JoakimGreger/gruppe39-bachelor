package com.example.joakim.ceapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private AutoCompleteTextView mAgeView;
    private Spinner mGenderSpinner;
    private EditText mPasswordView;
    private EditText mConfirmPwView;
    private View mProgressView;
    private View mLoginFormView;
    private String emailText;
    private String usernameText;
    private String ageNumber;
    private String genderText;
    private String passwordText;
    private String confirmPwText;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        emailText = mEmailView.getText().toString();
        passwordText = mPasswordView.getText().toString();
        if (mEmailSignInButton.getText().toString().equals("Registrer")) {
            mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
            usernameText = mUsernameView.getText().toString();
            mAgeView = (AutoCompleteTextView) findViewById(R.id.age);
            ageNumber = mAgeView.getText().toString();
            mGenderSpinner = (Spinner) findViewById(R.id.gender_spinner);
            genderText = mGenderSpinner.getSelectedItem().toString();
            genderText = genderText.equals("Mann") ? "male" : "female";
            mConfirmPwView = (EditText) findViewById(R.id.confirm_password);
            confirmPwText = mConfirmPwView.getText().toString();
            if (passwordText.equals(confirmPwText)) {
                new RegisterUserTask().execute("http://webapp.bimorstad.tech/user/create");
            } else {

            }
        } else {
            new AttemptLoginTask().execute("http://webapp.bimorstad.tech/user/login");
        }
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class AttemptLoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(params[0] + "?email=" + emailText + "&password=" + passwordText).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Exception", result);
            try {
                JSONObject obj = new JSONObject(result);
                Log.e("Exception", "UUID:" + obj.getString("UUID"));
                SharedPreferences token = getApplicationContext().getSharedPreferences("session_token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = token.edit();
                editor.putString("session_token", obj.getString("UUID"));
                editor.putString("email", emailText);
                editor.commit();
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
                if (result.equals("Email not registered")) {
                    showRegisterForm();
                }
            }
        }
    }

    public void showRegisterForm() {
        findViewById(R.id.usernameLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.genederLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.ageLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.confirm_passwordLayout).setVisibility(View.VISIBLE);
        mEmailSignInButton.setText("Registrer");
    }


    private class RegisterUserTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {
                String url = params[0] + "?email=" + emailText + "&username=" + usernameText + "&age=" + ageNumber
                        + "&gender=" + genderText + "&password=" + passwordText + "&confirmPassword=" + confirmPwText;
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("POST");
                Log.e("Exception", "URL:" + url);
                httpURLConnection.setDoOutput(true);

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Exception", result);
            try {
                JSONObject obj = new JSONObject(result);
                Log.e("Exception", "UUID:" + obj.getString("UUID"));
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                //Henter tidliger score hvis den finnes
                SharedPreferences startscore = getApplicationContext().getSharedPreferences("qScore", Context.MODE_PRIVATE);
                int score = startscore.getInt("qScore", 0);
                //Henter antall besvareler
                SharedPreferences answers = getApplicationContext().getSharedPreferences("qAnswer", Context.MODE_PRIVATE);
                int answerAmount = answers.getInt("qAnswer", 0);

                SharedPreferences token = getApplicationContext().getSharedPreferences("session_token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = token.edit();
                editor.putString("session_token", obj.getString("UUID"));
                editor.putString("email", obj.getString("Email"));
                editor.commit();

                db.addUser(db.getWritableDatabase(), obj.getString("Email"), obj.getString("Username"), score, answerAmount, true);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

