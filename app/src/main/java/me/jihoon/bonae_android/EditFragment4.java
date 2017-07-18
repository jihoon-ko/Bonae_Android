package me.jihoon.bonae_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditFragment4 extends AppCompatActivity {
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fragment4);

        Intent intent = getIntent();
        Token = intent.getStringExtra("Token");
        Facebook_Id = intent.getStringExtra("Facebook_Id");
        Facebook_Name = intent.getStringExtra("Facebook_Name");

        ImageView myProfile = (ImageView) findViewById(R.id.editmyProfile);
        final EditText myname = (EditText) findViewById(R.id.editmyName);
        final EditText mybank = (EditText) findViewById(R.id.editmyaccountBank);
        final EditText mynumber = (EditText) findViewById(R.id.editmyaccountNumber);

        new imageTask(myProfile).execute(Facebook_Id);
        Button editButton = (Button) findViewById(R.id.editmyInfoButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName = myname.getText().toString();
                String myBank = mybank.getText().toString();
                String myNumber = mynumber.getText().toString();

                new HTTPChangemyInfo().execute("http://52.78.17.108:3000/user/changeInfo/" + Facebook_Id + "/", Token, Facebook_Id, myName, myBank, myNumber);
            }
        });
    }

    private class imageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView iv;
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private OkHttpClient client;
        public imageTask(ImageView _iv){
            iv = _iv;
            client = new OkHttpClient();
        }
        private String send(String url) throws IOException {
            //RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", Token)
                    .addHeader("x-access-id", Facebook_Id)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        @Override
        protected Bitmap doInBackground(String... str){
            try {
                String urlJson = send("http://52.78.17.108:3000/user/id/" + str[0] + "/picture/");
                JSONObject jsonObj = new JSONObject(urlJson);
                String url = jsonObj.getString("url");
                Bitmap res = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    res = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap res){
            //System.out.println(res);
            if(res != null){
                super.onPostExecute(res);
                iv.setImageBitmap(res);
            }
        }
    };

    private class HTTPChangemyInfo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
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
            Intent intent = getIntent();
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

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
