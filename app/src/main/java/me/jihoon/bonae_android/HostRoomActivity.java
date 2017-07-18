package me.jihoon.bonae_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HostRoomActivity extends AppCompatActivity {
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;
    String roomInfo = null;

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

            String name = user_host.getString("name");
            String account_bank = user_host.getString("account_bank");
            String account_number = user_host.getString("account_number");

            String date = user_host.getString("created_date");
            String content = user_host.getString("context_text");
            JSONArray debit_guests = roomJSON.getJSONArray("debit_guests");

            CustomInfoAdapter.HostRoomAdapter hostRoomAdapter = new CustomInfoAdapter.HostRoomAdapter();
            hostRoomAdapter.addHostRoom(name, account_bank, account_number, date, content, debit_guests);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView hostName = (TextView) findViewById(R.id.hostTab_hostName);
        TextView hostBank = (TextView) findViewById(R.id.hostTab_hostBank);
        TextView hostNumber = (TextView) findViewById(R.id.hostTab_hostNumber);
    }
}
