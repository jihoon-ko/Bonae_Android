package me.jihoon.bonae_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HostRoomActivity extends AppCompatActivity {
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;
    String roomInfo = null;
    String RoomId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_room);

        Intent intent = getIntent();
        Token = intent.getStringExtra("Token");
        Facebook_Id = intent.getStringExtra("Facebook_Id");
        Facebook_Name = intent.getStringExtra("Facebook_Name");
        roomInfo = intent.getStringExtra("roomInfo");
        try {
            JSONObject roomJSON = new JSONObject(roomInfo);
            JSONObject user_host = roomJSON.getJSONObject("user_host");
            RoomId = roomJSON.getString("id");

            ImageView Profile = (ImageView) findViewById(R.id.hostTab_hostProfile);
            TextView Name = (TextView) findViewById(R.id.hostTab_hostName);
            TextView Bank = (TextView) findViewById(R.id.hostTab_hostBank);
            TextView Number = (TextView) findViewById(R.id.hostTab_hostNumber);
            TextView date = (TextView) findViewById(R.id.hostTab_date);
            TextView Left = (TextView) findViewById(R.id.hostTab_debit_left);
            TextView Keyword = (TextView) findViewById(R.id.hostTab_keyword);
            TextView content = (TextView) findViewById(R.id.hostTab_content);
            Name.setText(user_host.getString("name"));
            Bank.setText(user_host.getString("account_bank"));
            Number.setText(user_host.getString("account_number"));
            date.setText(roomJSON.getString("created_date"));
            Left.setText(Integer.toString(roomJSON.getInt("debit_left")));
            Keyword.setText(roomJSON.getString("keyword"));
            content.setText(roomJSON.getString("content_text"));

            ListView guests = (ListView) findViewById(R.id.hostTab_guests);

            JSONArray debit_guests = roomJSON.getJSONArray("debit_guests");
            CustomInfoAdapter.HostRoomguestAdapter hostRoomguestAdapter = new CustomInfoAdapter.HostRoomguestAdapter();
            int len = debit_guests.length();
            for (int i = 0; i < len; i++) {
                JSONObject debit = debit_guests.getJSONObject(i);
                hostRoomguestAdapter.addHostRoomguest(debit, RoomId, Token, Facebook_Id, Facebook_Name);
                hostRoomguestAdapter.notifyDataSetChanged();
            }
            guests.setAdapter(hostRoomguestAdapter);
            new imageTask(Profile).execute(Facebook_Id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
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
}
