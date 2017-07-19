package me.jihoon.bonae_android;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static me.jihoon.bonae_android.Tab2Fragment.Facebook_Id;
import static me.jihoon.bonae_android.Tab2Fragment.Token;
import static me.jihoon.bonae_android.Tab2Fragment.adapter;
import static me.jihoon.bonae_android.Tab3Fragment.listView;

/**
 * Created by q on 2017-07-13.
 */

public class Tab2Fragment extends Fragment{
    static final int REQ_CREATE_ROOM = 3;
    static final int REQ_CONNECT_HOST_ROOM = 4;

    JSONObject UserJSON = null;
    public static String Token = null;
    public static String Facebook_Id = null;
    public static String Facebook_Name = null;
    public static CustomInfoAdapter.RoomAdapter adapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        Setting(inflater, container);

        Button createRoomButton = (Button) view.findViewById(R.id.createRoomButton);
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateRoomActivity.class);
                intent.putExtra("Token", Token);
                intent.putExtra("Facebook_Id", Facebook_Id);
                intent.putExtra("Facebook_Name", Facebook_Name);
//                getActivity().startActivityFromFragment(Tab2Fragment.this, intent, REQ_CREATE_ROOM);
                getActivity().startActivityForResult(intent, REQ_CREATE_ROOM);
            }
        });

        listView = (ListView) view.findViewById(R.id.listview_tab2);
        //CustomInfoAdapter.RoomAdapter adapter = makeHostRoom();
        adapter = new CustomInfoAdapter.RoomAdapter();
        listView.setAdapter(adapter);
        new getHostTask().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String HostRoomId = null;
                String keyword = null;
                try {
                    //JSONArray hostRoom = UserJSON.getJSONArray("room_pending_host");
                    //int len = hostRoom.length();
                    //JSONObject hostRoomJSON = hostRoom.getJSONObject(len-1-position);
                    CustomInfo.Room target_obj = (CustomInfo.Room) adapter.getItem(position);
                    HostRoomId = target_obj.getRoom_id();
                    keyword = target_obj.getContent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new HTTPConnectHostRoom().execute("http://52.78.17.108:3000/room/id/"+HostRoomId+"/", Token, Facebook_Id, Facebook_Name, keyword);
            }
        });
        return view;
    }
    public static void onSuccess(){
        new getHostTask().execute();
    }
    /*
    private CustomInfoAdapter.RoomAdapter makeHostRoom() {
        CustomInfoAdapter.RoomAdapter adapter = new CustomInfoAdapter.RoomAdapter();
        try {
            JSONArray HostRoom = UserJSON.getJSONArray("room_pending_host");
            int len = HostRoom.length();
            for (int i = len-1; i >= 0; i--) {
                JSONObject HostRoomJSON = HostRoom.getJSONObject(i);
                String roomID = HostRoomJSON.getString("room");
                String keyword = HostRoomJSON.getString("keyword");
                int left = HostRoomJSON.getJSONObject("guest_number").getInt("left");
                int total = HostRoomJSON.getJSONObject("guest_number").getInt("total");
                String number = Integer.toString(left) + " / " + Integer.toString(total);
                String price = HostRoomJSON.getString("left");
                String Date = HostRoomJSON.getString("created_date");
                adapter.addRoom(roomID, keyword, number, price, Date);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return adapter;
    }
    */

    public class HTTPConnectHostRoom extends AsyncTask<String, Void, String> {
        String Token = null;
        String Facebook_Id = null;
        String Facebook_Name = null;
        String keyword = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "i-th hostroom connecting...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            Token = params[1];
            Facebook_Id = params[2];
            Facebook_Name = params[3];
            keyword = params[4];

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
            Toast.makeText(getContext(), "Hostroom connection complete", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), HostRoomActivity.class);
            intent.putExtra("Token", Token);
            intent.putExtra("Facebook_Id", Facebook_Id);
            intent.putExtra("Facebook_Name", Facebook_Name);
            try {
                JSONObject roomJSON = new JSONObject(result);
                roomJSON.put("keyword", keyword);
                intent.putExtra("roomInfo", roomJSON.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            startActivityForResult(intent, REQ_CONNECT_HOST_ROOM);
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

class getHostTask extends AsyncTask<String, Void, String>{
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
                JSONArray target = jsonObj.getJSONArray("room_pending_host");
                int len = target.length();
                for (int i = len-1; i >= 0; i--) {
                    JSONObject HostRoomJSON = target.getJSONObject(i);
                    String roomID = HostRoomJSON.getString("room");
                    String keyword = HostRoomJSON.getString("keyword");
                    int left = HostRoomJSON.getJSONObject("guest_number").getInt("left");
                    int total = HostRoomJSON.getJSONObject("guest_number").getInt("total");
                    String number = Integer.toString(left) + " / " + Integer.toString(total);
                    String price = HostRoomJSON.getString("left");
                    String Date = HostRoomJSON.getString("created_date");
                    adapter.addRoom(roomID, keyword, number, price, Date);
                }
                listView.invalidateViews();
                listView.postInvalidate();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
};