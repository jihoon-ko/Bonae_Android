package me.jihoon.bonae_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFriendActivity extends AppCompatActivity {
    private EditText searchBox;
    private Button searchButton;
    private CustomInfoAdapter.UserAdapter adapter;
    private String token, fbId, fbName;
    private ListView listView_add;
    private TextView showMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_add_friend);
        this.setTitle("친구 찾기");
        token = intent.getExtras().getString("token");
        fbId = intent.getExtras().getString("fbId");
        fbName = intent.getExtras().getString("fbName");

        showMsg = (TextView) findViewById(R.id.showMsg_add);
        showMsg.setVisibility(View.VISIBLE);
        adapter = new CustomInfoAdapter.UserAdapter(2, token, fbId, fbName);
        listView_add = (ListView) findViewById(R.id.peopleList_add);
        listView_add.setAdapter(adapter);
        listView_add.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomInfo.User user = (CustomInfo.User) adapter.getItem(position);
                if(user.getf()){
                    Toast.makeText(AddFriendActivity.this, "이미 친구입니다", Toast.LENGTH_SHORT).show();
                }else{
                    new addFriendTask().execute(user.getUser_id());
                }
            }
        });
        searchBox = (EditText) findViewById(R.id.editText_add);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.clearUser();
                showMsg.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchButton = (Button) findViewById(R.id.searchButton_add);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new searchTask().execute(searchBox.getText().toString());
            }
        });
    }
    private class addFriendTask extends AsyncTask<String, Void, String>{
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private OkHttpClient client;
        private String send(String json, String url) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", token)
                    .addHeader("x-access-id", fbId)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        @Override
        protected String doInBackground(String... str){
            System.out.println("!!");
            try{
                return send("{\"friend_id\":\"" + str[0] + "\"}", "http://52.78.17.108:3000/user/id/"+fbId+"/friends/add/");
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String res){
            if(res != null){
                try{
                    JSONObject jsonObj = new JSONObject(res);
                    if(jsonObj.getInt("ok") == 1){
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }else{
                        System.out.println("Error while handling...");
                    }
                }catch(Exception e){
                    System.out.println("Error while handling...");
                    e.printStackTrace();
                }
            }
        }
    };
    private class searchTask extends AsyncTask<String, Void, String>{
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private OkHttpClient client;
        private String send(String url) throws IOException {
            //RequestBody body = RequestBody.create(JSON, json);
            client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", token)
                    .addHeader("x-access-id", fbId)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        @Override
        protected void onPreExecute(){
            adapter.clearUser();
            showMsg.setVisibility(View.GONE);
        }
        @Override
        protected String doInBackground(String... str){
            System.out.println("!!");
            try{
                return send("http://52.78.17.108:3000/user/search/?name="+str[0]);
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String res){
            System.out.println("!!!");
            if(res != null){
                super.onPostExecute(res);
                try {
                    System.out.println("!!!!");
                    JSONArray jsonArray = new JSONArray(res);
                    for(int i=0;i<jsonArray.length();i++){
                        System.out.println("@");
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String fb_id = jsonObj.getString("facebook_id");
                        String fb_name = jsonObj.getString("name");
                        String bank = jsonObj.getString("account_bank");
                        String number = jsonObj.getString("account_number");
                        boolean is_friend = jsonObj.getBoolean("is_friend");
                        adapter.addUser(-1, fb_id, fb_name, bank, number, is_friend);
                        System.out.println("@@");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    };
}
