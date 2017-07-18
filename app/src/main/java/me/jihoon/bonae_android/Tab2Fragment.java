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

/**
 * Created by q on 2017-07-13.
 */

public class Tab2Fragment extends Fragment{
    static final int REQ_CREATE_ROOM = 3;
    static final int REQ_CONNECT_HOST_ROOM = 4;

    JSONObject UserJSON = null;
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;

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

        ListView listView = (ListView) view.findViewById(R.id.listview_tab2);
        CustomInfoAdapter.RoomAdapter adapter = makeHostRoom();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String HostRoomId = null;
                try {
                    JSONArray hostRoom = UserJSON.getJSONArray("room_pending_host");
                    JSONObject hostRoomJSON = hostRoom.getJSONObject(position);
                    HostRoomId = hostRoomJSON.getString("room");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new HTTPConnectHostRoom().execute("http://52.78.17.108:3000/room/id/"+HostRoomId+"/", Token, Facebook_Id, Facebook_Name);
            }
        });
        return view;
    }

    private CustomInfoAdapter.RoomAdapter makeHostRoom() {
        CustomInfoAdapter.RoomAdapter adapter = new CustomInfoAdapter.RoomAdapter();
        adapter.addRoom("99887766", "네네치킨", "3 / 4", "5000", "2017-07-20");
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

        adapter.addRoom("98765432", "큰통치킨", "2 / 3", "6000", "2017-07-16");

        return adapter;
    }

    public class HTTPConnectHostRoom extends AsyncTask<String, Void, String> {
        String Token = null;
        String Facebook_Id = null;
        String Facebook_Name = null;

        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "i-th hostroom connecting...", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Hostroom connection complete", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), HostRoomActivity.class);
            intent.putExtra("Token", Token);
            intent.putExtra("Facebook_Id", Facebook_Id);
            intent.putExtra("Facebook_Name", Facebook_Name);
            intent.putExtra("roomInfo", result);

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
