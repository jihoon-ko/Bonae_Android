package me.jihoon.bonae_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}
