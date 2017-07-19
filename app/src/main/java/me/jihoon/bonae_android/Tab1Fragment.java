package me.jihoon.bonae_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static me.jihoon.bonae_android.Tab1Fragment.Facebook_Id;
import static me.jihoon.bonae_android.Tab1Fragment.Token;
import static me.jihoon.bonae_android.Tab1Fragment.adapter;
import static me.jihoon.bonae_android.Tab1Fragment.mSwipeRefreshLayout;

/**
 * Created by q on 2017-07-13.
 */

public class Tab1Fragment extends Fragment {
    public static String Token = null;
    public static String Facebook_Id = null;
    public static String Facebook_Name = null;
    private ListView yes_list;
    private EditText searchBox;
    private FloatingActionButton fab;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static CustomInfoAdapter.UserAdapter adapter = null;
    public Tab1Fragment(){
         adapter = new CustomInfoAdapter.UserAdapter(1, Token, Facebook_Id, Facebook_Name);
    }

    public void Setting(LayoutInflater inflater, ViewGroup container) {
        boolean user_changed = false;
        String new_id = this.getArguments().getString("Facebook_Id");
        /*if(Facebook_Id.compareTo(new_id) != 0){
            user_changed = true;
        }*/
        Facebook_Id = new_id;
        Token = this.getArguments().getString("Token");
        Facebook_Name = this.getArguments().getString("Facebook_Name");
        if(new_id != null && new_id.compareTo("") != 0){
            adapter.token = Token; adapter.facebookName = Facebook_Name; adapter.facebookId = Facebook_Id;
            new getFriendListTask(false).execute();
        }
        System.out.println(Facebook_Id + " " + Token + " " + Facebook_Name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        Setting(inflater, container);
        yes_list = (ListView) view.findViewById(R.id.listView_tab1yes);
        yes_list.setAdapter(adapter);
        yes_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final CustomInfo.User user = (CustomInfo.User) adapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("친구를 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                new deleteFriendTask().execute(user.getUser_id());
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
        //adapter.addUser("TestUser1");
        searchBox = (EditText) view.findViewById(R.id.editText1);
        searchBox.setText(adapter.getKeyword());
        adapter.changeData(adapter.getKeyword());
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.changeData(searchBox.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout1);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getFriendListTask(true).execute();
            }
        });
        fab = (FloatingActionButton) view.findViewById(R.id.fab_tab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.putExtra("token", Token);
                intent.putExtra("fbId", Facebook_Id);
                intent.putExtra("fbName", Facebook_Name);
                startActivityForResult(intent, 284);
            }
        });
        return view;
    }
    public static void onSuccess(int requestCode, int resultCode, Intent data) {
        if(requestCode == 284){
            if (resultCode == RESULT_OK) {
                System.out.println("Success");
                new getFriendListTask(false).execute();
            }else{
                System.out.println("Failed");
            }
        }
    }
    private class deleteFriendTask extends AsyncTask<String, Void, String>{
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private OkHttpClient client;
        private String send(String json, String url) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", Token)
                    .addHeader("x-access-id", Facebook_Id)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        @Override
        protected String doInBackground(String... str){
            System.out.println("!!");
            try{
                return send("{\"friend_id\":\"" + str[0] + "\"}", "http://52.78.17.108:3000/user/id/"+Facebook_Id+"/friends/delete/");
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
                        new getFriendListTask(false).execute();
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
}

class getFriendListTask extends AsyncTask<String, Void, String>{
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;
    private boolean isSwipe;
    public getFriendListTask(boolean _swipe){
        isSwipe = _swipe;
    }
    @Override
    protected void onPreExecute(){
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
    protected String doInBackground(String... str){
        try{
            String res = send("http://52.78.17.108:3000/user/id/" + Facebook_Id + "/friends/");
            return res;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(String res){
        if(res != null){
            super.onPostExecute(res);
            adapter.clearUser();
            try {
                System.out.println(res);
                JSONArray jsonArray = new JSONArray(res);
                System.out.println(jsonArray);
                int n = jsonArray.length();
                System.out.println(n);
                boolean need_cmp = true;
                int idx = 0;
                for(int i=0;i<n;i++){
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    String fbId = jsonObj.getString("facebook_id");
                    String name = jsonObj.getString("name");
                    String bank = jsonObj.getString("account_bank");
                    String num = jsonObj.getString("account_number");
                    if(need_cmp && idx == adapter.userCnt()){
                        need_cmp = false;
                    }
                    if(need_cmp){
                        String old_fbId = ((CustomInfo.User) adapter.gett(idx)).getUser_id();
                        System.out.println(fbId + " " + old_fbId);
                        int cmp_res = old_fbId.compareTo(fbId);
                        if(cmp_res == 0){
                            CustomInfo.User user = ((CustomInfo.User) adapter.gett(idx));
                            user.setNickName(name); user.setAccountBank(bank); user.setAccountNumber(num);
                            idx += 1;
                            adapter.changeData(adapter.getKeyword());
                        }else if(cmp_res < 0){
                            adapter.removeUser(idx);
                            i--;
                        }else{
                            adapter.addUser(idx, fbId, name, bank, num, true);
                            idx += 1;
                        }
                    }else{
                        adapter.addUser(-1, fbId, name, bank, num, true);
                        idx += 1;
                    }
                }
                while(idx != adapter.userCnt()){
                    adapter.removeUser(idx);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
};
