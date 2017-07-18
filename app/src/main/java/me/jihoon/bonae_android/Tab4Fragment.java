package me.jihoon.bonae_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by q on 2017-07-13.
 */

public class Tab4Fragment extends Fragment{
    static final int REQ_EDIT_PROFILE = 6;

    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;

    public void Setting(LayoutInflater inflater, ViewGroup container) {
        Token = this.getArguments().getString("Token");
        Facebook_Id = this.getArguments().getString("Facebook_Id");
        Facebook_Name = this.getArguments().getString("Facebook_Name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab4, container, false);

        Setting(inflater, container);

        JSONObject UserJSON = null;
        String UserInfo = this.getArguments().getString("user");
        try {
            UserJSON = new JSONObject(UserInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView myProfile = (ImageView) view.findViewById(R.id.myProfile);
        TextView myName = (TextView) view.findViewById(R.id.myName);
        final TextView myaccountBank = (TextView) view.findViewById(R.id.myaccountBank);
        final TextView myaccountNumber = (TextView) view.findViewById(R.id.myaccountNumber);

        String Name = "";
        String accountBank = "";
        String accountNumber = "";
        try {
            Name = UserJSON.getString("name");
            accountBank = UserJSON.getString("account_bank");
            accountNumber = UserJSON.getString("account_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myName.setText(Name);
        myaccountBank.setText(accountBank);
        myaccountNumber.setText(accountNumber);

        Button editButton = (Button) view.findViewById(R.id.editmyInfoButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditFragment4.class);
                intent.putExtra("Token", Token);
                intent.putExtra("Facebook_Id", Facebook_Id);
                intent.putExtra("Facebook_Name", Facebook_Name);

                startActivityForResult(intent, REQ_EDIT_PROFILE);
            }
        });
        new imageTask(myProfile).execute(Facebook_Id);

        return view;
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
