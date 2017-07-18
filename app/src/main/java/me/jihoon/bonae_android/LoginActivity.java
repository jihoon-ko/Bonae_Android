package me.jihoon.bonae_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity{
    static final int REQ_ADD_REGISTER = 2;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessToken mToken;
    GraphRequest request;

    private TextView loading;
    JSONObject NewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        mToken = AccessToken.getCurrentAccessToken();
//        Profile profile = Profile.getCurrentProfile(); facebook profileimg
//        profile.getProfilePictureUri(200, 200).toString();
        loading = (TextView) findViewById(R.id.login_loading);

        if(mToken == null) {
            loginButton = (LoginButton) findViewById(R.id.fblogin_button);
            loginButton.setReadPermissions("email", "public_profile", "user_friends");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    mToken = loginResult.getAccessToken();
                    Log.e("Token", mToken.getToken());
                    Log.e("UserId", mToken.getUserId());
                    Log.e("Permission lists", mToken.getPermissions()+"");

                    Bundle parameters = new Bundle();
//                    parameters.putString("fields", "NewUser");
                    parameters.putString("fields", "id,name,email,gender,birthday");

                    request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String fb_id = null;
                            String fb_name = null;
                            Log.e("TAG", "Facebook Login Result" + response.toString());
                            try {
                                fb_id = object.get("id").toString();
                                fb_name = object.get("name").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new HTTPLoginServer().execute("http://52.78.17.108:3000/auth/login/", fb_id, fb_name);
                        }
                    });
                    request.setParameters(parameters);
                    request.executeAsync();
                    Log.e("First Login", "here!!");
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "Login Canceled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    error.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Bundle parameters = new Bundle();
//            parameters.putString("fields", "NewUser");
            parameters.putString("fields", "id,name,email,gender");

            request = GraphRequest.newMeRequest(mToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    String fb_id = null;
                    String fb_name = null;
                    Log.e("TAG", "Facebook Login Result" + response.toString());
                    try {
                        fb_id = object.get("id").toString();
                        fb_name = object.get("name").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new HTTPLoginServer().execute("http://52.78.17.108:3000/auth/login/", fb_id, fb_name);
                }
            });

            request.setParameters(parameters);
            request.executeAsync();
            Log.e("already Login", "here!!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ADD_REGISTER) {
            if (resultCode == RESULT_OK) {
                String Token = data.getStringExtra("Token");
                String Facebook_Id = data.getStringExtra("Facebook_Id");
                String Facebook_Name = data.getStringExtra("Facebook_Name");

                new HTTPGetUserInfo().execute("http://52.78.17.108:3000/user/id/"+Facebook_Id+"/", Token, Facebook_Id, Facebook_Name);
            }
        }
    }

    private class HTTPLoginServer extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loading.setText("Facebook seerver is loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject bodyJSON = new JSONObject();
                bodyJSON.put("appid", getString(R.string.facebook_app_id));
                bodyJSON.put("id", params[1]);
                bodyJSON.put("name", params[2]);
                String http_req = bodyJSON.toString();

                System.out.println(http_req);
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(http_req);
                osw.flush();
                osw.close();

                int resCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == resCode) {
                    Log.e("Connect Complete", "HTTP_OK");
                    String http_res = getTextFrom(connection.getInputStream());

                    JSONObject resJSON = new JSONObject(http_res);
                    resJSON.put("fb_id", params[1]);
                    resJSON.put("fb_name", params[2]);
                    result = resJSON.toString();
                } else {
                    result = connection.getResponseCode() + "-" + connection.getResponseMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (connection != null) {
                connection.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            loading.setText("Facebook server login complete.");
            JSONObject JSONResponse = null;
            Boolean first = null;
            String facebook_id = null;
            String facebook_name = null;
            String token = null;
            try {
                JSONResponse = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                first = JSONResponse.getBoolean("first_login");
                facebook_id = JSONResponse.getString("fb_id");
                facebook_name = JSONResponse.getString("fb_name");
                token = JSONResponse.getString("token");
            } catch (JSONException e) {e.printStackTrace();}

            if (first == true) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("Token", token);
                intent.putExtra("Facebook_Id", facebook_id);
                intent.putExtra("Facebook_Name", facebook_name);
                startActivityForResult(intent, REQ_ADD_REGISTER);
            } else {
                new HTTPGetUserInfo().execute("http://52.78.17.108:3000/user/id/"+facebook_id+"/", token, facebook_id, facebook_name);
            }
        }
    }

    private class HTTPGetUserInfo extends AsyncTask<String, Void, String> {
        String Token = null;
        String Facebook_Id = null;
        String Facebook_Name = null;

        @Override
        protected void onPreExecute() {
            loading.setText("Getting User Info...");
        }

        @Override
        protected String doInBackground(String... params) {
            Token = params[1];
            Facebook_Id = params[2];
            Facebook_Name = params[3];

            String http_res = null;
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);
                connection.setDoInput(true);
                connection.setRequestProperty("x-access-token", params[1]);
                connection.setRequestProperty("x-access-id", params[2]);

                int resCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == resCode) {
                    Log.e("Connect Complete", "HTTP_OK");
                    http_res = getTextFrom(connection.getInputStream());

/*                    JSONObject resJSON = new JSONObject(http_res);
                    http_res = resJSON.toString();*/
                    Log.e("Response from Server", http_res);
                } else {
                    http_res = connection.getResponseCode() + "-" + connection.getResponseMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
            return http_res;
        }

        @Override
        protected void onPostExecute(String result) {
            loading.setText("Complete Getting User Info!");
            Intent intent = new Intent();
            intent.putExtra("user_info", result);
            intent.putExtra("Token", Token);
            intent.putExtra("Facebook_Id", Facebook_Id);
            intent.putExtra("Facebook_Name", Facebook_Name);

            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private String getTextFrom(InputStream in) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
