package com.extrace.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extrace.net.OkHttpClientManager;
import com.extrace.ui.R;
import com.extrace.ui.entity.UserInfo;
import com.extrace.ui.service.ActivityCollector;
import com.extrace.ui.service.LoginService;
import com.google.gson.JsonArray;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;
import static com.extrace.net.OkHttpClientManager.BASE_URL;

/**
 * 一个登录屏幕，提供通过电子邮件/密码登录。
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world","zhn@qq.com:lalala"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private String data; //登录请求数据

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private CheckBox checkBox;
    private TextView tv_empty;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();

            }
        });

        checkBox = findViewById(R.id.ch);
        tv_empty = findViewById(R.id.empty);
        tv_empty.setOnClickListener(new TextView.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoginService.saveUserInfo(getApplicationContext(), "","", checkBox.isChecked());
                LoginService.saveUserInfo(getApplicationContext(),null,false,false);
            }
        });
        //显示用户此前录入的数据
        SharedPreferences sPreferences=getSharedPreferences("USER_INFO", MODE_PRIVATE);
        String username=sPreferences.getString("username", "");
        String password =sPreferences.getString("password", "");
        Boolean ch =sPreferences.getBoolean("or", false);
        mEmailView.setText(username);
        mPasswordView.setText(password);
        checkBox.setChecked(ch);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_empty_password));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            return getSingleCabCollect(mEmail, mPassword);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                if (data != null && !data.isEmpty()){
                    try {
                        Log.e("lalal",data);
                        JSONObject json = new JSONObject(data);
                        String lala = json.getString("validate");
                        if (lala.equals("true")){
                            JSONArray info = json.getJSONArray("info");
                            JSONObject infoObject = info.getJSONObject(0);
                            UserInfo userInfo = new UserInfo();
                            userInfo.setUid(infoObject.getInt("uid"));
                            userInfo.setName(infoObject.getString("name"));
                            userInfo.setPwd(infoObject.getString("pwd"));
                            userInfo.setUrull(infoObject.getInt("urull"));
                            userInfo.setTelcode(infoObject.getString("telcode"));
                            userInfo.setStatus(infoObject.getInt("status"));
                            userInfo.setDptid(infoObject.getString("dptid"));
                            userInfo.setReceivepackageid(infoObject.getString("receivepackageid"));
                            userInfo.setDelivepackageid(infoObject.getString("delivepackageid"));
                            userInfo.setTranspackageid(infoObject.getString("transpackageid"));

                            LoginService.saveUserInfo(getApplicationContext(), userInfo, checkBox.isChecked(),true);

                            //Toast.makeText(LoginActivity.this, "账号"+mEmail+"登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            intent.putExtra("permission_check",infoObject.getInt("urull"));
                            intent.putExtra("info_username",mEmail);
                            setResult(RESULT_OK,intent);
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "账号/密码错误", Toast.LENGTH_SHORT).show();
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                Toast.makeText(LoginActivity.this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

//    String name=mEmailView.getText().toString();
//    String pass=mPasswordView.getText().toString();
//    GetSingleCabCollect(name,pass);

    public Boolean getSingleCabCollect(String name,String pass) {
//        HttpPost httpPost = new HttpPost(BASE_URL+"/ExtraceSystem/validateRegion");
//        JSONObject jsonParam = new JSONObject();
//        try {
//            jsonParam.put("loginName", name);
//            jsonParam.put("loginPwd", pass);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        StringEntity entity = null;//解决中文乱码问题
//        try {
//            entity = new StringEntity(jsonParam.toString(), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        if (entity != null) {
//            entity.setContentEncoding("UTF-8");
//            entity.setContentType("application/json");
//        }
//        httpPost.setEntity(entity);
//        HttpClient httpClient = new DefaultHttpClient();
//        // 获取HttpResponse实例
//        HttpResponse httpResp = null;
//        try {
//            httpResp = httpClient.execute(httpPost);
//        } catch (IOException e) {
//            Log.e("lalala","抛出IO异常"+e.toString());
//            e.printStackTrace();
//        }catch (Exception e1){
//            Log.e("lalala","抛出异常"+e1.toString());
//        }
//        // 判断是够请求成功
//        if (httpResp != null) {
//            if (httpResp.getStatusLine().getStatusCode() == 200) {
//                // 获取返回的数据
//                String result = null;
//                try {
//                    result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
//                    Log.e("lalal", "HttpPost方式请求成功，返回数据如下："+result);
//                    return result;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.e("lalal", "打印数据 HttpPost方式请求失败" + httpResp.getStatusLine().getStatusCode());
//            }
//        }

        try {
            Response response =OkHttpClientManager.post(BASE_URL + "/ExtraceSystem/validateRegion",new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("key_username",name),
                    new OkHttpClientManager.Param("key_password",pass)}
                    );
            if (response.code() == 200){
                data =response.body().string();
                return true;
            }else {
                Log.e("lalal", "HttpPost方式请求失败："+response.code()+response.body().toString());
                return false;
            }
        } catch (IOException e) {
            Log.e("lalal", "HttpPost方式请异常："+e.toString());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}

