package me.jihoon.bonae_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;
    Boolean ValidName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        Token = intent.getStringExtra("Token");
        Facebook_Id = intent.getStringExtra("Facebook_Id");
        Facebook_Name = intent.getStringExtra("Facebook_Name");

        TextView fb_name = (TextView)findViewById(R.id.facebook_name);
        final EditText nickName = (EditText)findViewById(R.id.nickname);
        final EditText accountBank = (EditText)findViewById(R.id.accountBank);
        final EditText accountNumber = (EditText)findViewById(R.id.accountNumber);
        final Button checkDupButton = (Button)findViewById(R.id.checkdupbutton);
        final Button registerButton = (Button)findViewById(R.id.registerButton);
        final TextView loading = (TextView)findViewById(R.id.register_loading);

        fb_name.setText(Facebook_Name);
        checkDupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nickName.getText().toString();
                if ("".equals(nickname)) {
                    Toast.makeText(RegisterActivity.this, "No NickName Input", Toast.LENGTH_SHORT).show();
                } else {
                    new HTTPCheckDupName().execute("http://52.78.17.108:3000/user/check/?name="+nickname, Token, Facebook_Id);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidName == false) {
                    loading.setText("중복된 이름이 있습니다. 다시 입력하세요");
                } else {
                    String name = nickName.getText().toString();
                    String account_bank = accountBank.getText().toString();
                    String account_num = accountNumber.getText().toString();
                    new HTTPCreateUser().execute("http://52.78.17.108:3000/user/changeInfo/" + Facebook_Id + "/", Token, Facebook_Id, name, account_bank, account_num);
                }
            }
        });
    }

    private class HTTPCreateUser extends AsyncTask<String, Void, String> {
        TextView loading = (TextView)findViewById(R.id.register_loading);
        @Override
        protected void onPreExecute() {
            loading.setText("Register User Info...");
        }

        @Override
        protected String doInBackground(String... params) {
            String http_res = null;
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);
                connection.setRequestProperty("x-access-token", params[1]);
                connection.setRequestProperty("x-access-id", params[2]);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject bodyJson = new JSONObject();
                bodyJson.put("name", params[3]);
                bodyJson.put("account_bank", params[4]);
                bodyJson.put("account_num", params[5]);
                String http_req = bodyJson.toString();

                System.out.println(http_req);
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(http_req);
                osw.flush();
                osw.close();

                int resCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == resCode) {
                    Log.e("Connect Complete", "HTTP_OK");
                    http_res = getTextFrom(connection.getInputStream());
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
            loading.setText("Complete register!");
            JSONObject JSONResponse = null;
            String ok = null;
            try {
                JSONResponse = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                ok = JSONResponse.getString("ok");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ("1".equals(ok)) {
                Intent intent = new Intent();
                intent.putExtra("Token", Token);
                intent.putExtra("Facebook_Id", Facebook_Id);
                intent.putExtra("Facebook_Name", Facebook_Name);

                setResult(RESULT_OK, intent);
                finish();
            } else {
                Log.e("There are Error!", result);
            }
        }
    }

    private class HTTPCheckDupName extends AsyncTask<String, Void, String> {
        TextView loading = (TextView)findViewById(R.id.register_loading);
        @Override
        protected void onPreExecute() {
            loading.setText("Checking Duplicate NickName");
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
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
                    String http_res = getTextFrom(connection.getInputStream());

                    JSONObject resJson = new JSONObject(http_res);
                    result = resJson.toString();
                    Log.e("Response from Server", result);
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
            loading.setText("Valid NickName");
            JSONObject JSONResponse = null;
            try {
                JSONResponse = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String ok = JSONResponse.getString("ok");
                if ("1".equals(ok)) {
                    ValidName = true;
                    loading.setText("Valid NickName");
                } else {
                    ValidName = false;
                    loading.setText("There already same NickName, write another one.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
