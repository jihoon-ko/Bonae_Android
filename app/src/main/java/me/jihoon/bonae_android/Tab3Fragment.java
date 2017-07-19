package me.jihoon.bonae_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.jihoon.bonae_android.Tab3Fragment.Facebook_Id;
import static me.jihoon.bonae_android.Tab3Fragment.Token;
import static me.jihoon.bonae_android.Tab3Fragment.adapter;
import static me.jihoon.bonae_android.Tab3Fragment.listView;

/**
 * Created by q on 2017-07-13.
 */

public class Tab3Fragment extends Fragment {
    static final int REQ_CONNECT_GUEST_ROOM = 5;

    JSONObject UserJSON = null;
    public static String Token = null;
    public static String Facebook_Id = null;
    public static String Facebook_Name = null;
    public static CustomInfoAdapter.RoomAdapter adapter = null;
    public static ListView listView;

    public void Setting(LayoutInflater inflater, ViewGroup container) {
        Token = this.getArguments().getString("Token");
        Facebook_Id = this.getArguments().getString("Facebook_Id");
        Facebook_Name = this.getArguments().getString("Facebook_Name");
        String UserInfo = this.getArguments().getString("user");
        try {
            UserJSON = new JSONObject(UserInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void onSuccess(){
        new getGuestTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);

        Setting(inflater, container);

        listView = (ListView) view.findViewById(R.id.listview_tab3);
        adapter = new CustomInfoAdapter.RoomAdapter();//makeGuestRoom();
        listView.setAdapter(adapter);
        new getGuestTask().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String GuestRoomId = null;
                try {
                    //JSONArray guestRoom = UserJSON.getJSONArray("room_pending_guest");
                    //int len = guestRoom.length();
                    //JSONObject guestRoomJSON = guestRoom.getJSONObject(len-1-position);
                    CustomInfo.Room target_obj = (CustomInfo.Room) adapter.getItem(position);
                    GuestRoomId = target_obj.getRoom_id();
                    //guestRoomJSON.getString("room");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new HTTPConnectGuestRoom().execute("http://52.78.17.108:3000/room/id/"+GuestRoomId+"/", Token, Facebook_Id, Facebook_Name);
            }
        });

        return view;
    }
    /*
    private CustomInfoAdapter.RoomAdapter makeGuestRoom() {
        CustomInfoAdapter.RoomAdapter adapter = new CustomInfoAdapter.RoomAdapter();
        try {
            JSONArray GuestRoom = UserJSON.getJSONArray("room_pending_guest");
            int len = GuestRoom.length();
            for (int i = len-1; i >= 0; i--) {
                JSONObject guestRoomJSON = GuestRoom.getJSONObject(i);
                String roomID = guestRoomJSON.getString("room");
                String keyword = guestRoomJSON.getString("keyword");
                String price = guestRoomJSON.getString("left");
                String Date = guestRoomJSON.getString("created_date");
                adapter.addRoom(roomID, keyword, price, Date);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return adapter;
    }
    */
    public class HTTPConnectGuestRoom extends AsyncTask<String, Void, String> {
        String Token = null;
        String Facebook_Id = null;
        String Facebook_Name = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "i-th guestroom connecting...", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Guestroom connection complete", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), GuestRoomActivity.class);
            intent.putExtra("Token", Token);
            intent.putExtra("Facebook_Id", Facebook_Id);
            intent.putExtra("Facebook_Name", Facebook_Name);
            intent.putExtra("roomInfo", result);

            startActivityForResult(intent, REQ_CONNECT_GUEST_ROOM);
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

class getGuestTask extends AsyncTask<String, Void, String>{
    @Override
    protected void onPreExecute(){
        adapter.clearItem();
    }
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;
    private String send(String url) throws IOException {
        //RequestBody body = RequestBody.create(JSON, json);
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-access-token", Token)
                .addHeader("x-access-id", Facebook_Id)
                //.post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    @Override
    protected String doInBackground(String... str){
        System.out.println("!!");
        try{
            return send("http://52.78.17.108:3000/user/id/"+Facebook_Id+"/");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(String res){
        if(res != null){
            super.onPostExecute(res);
            try {
                JSONObject jsonObj = new JSONObject(res);
                JSONArray target = jsonObj.getJSONArray("room_pending_guest");
                int len = target.length();
                for (int i = len-1; i >= 0; i--) {
                    JSONObject guestRoomJSON = target.getJSONObject(i);
                    String roomID = guestRoomJSON.getString("room");
                    String keyword = guestRoomJSON.getString("keyword");
                    String price = guestRoomJSON.getString("left");
                    String Date = guestRoomJSON.getString("created_date");
                    adapter.addRoom(roomID, keyword, price, Date);
                }
                listView.invalidateViews();
                listView.postInvalidate();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
};