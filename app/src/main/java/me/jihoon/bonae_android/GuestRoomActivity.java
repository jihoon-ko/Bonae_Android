package me.jihoon.bonae_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GuestRoomActivity extends AppCompatActivity {
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;
    String roomInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_room);

        Intent intent = getIntent();
        Token = intent.getStringExtra("Token");
        Facebook_Id = intent.getStringExtra("Facebook_Id");
        Facebook_Name = intent.getStringExtra("Facebook_Name");
        roomInfo = intent.getStringExtra("roomInfo");

        try {
            JSONObject roomJSON = new JSONObject(roomInfo);
            JSONObject user_host = roomJSON.getJSONObject("user_host");

            ImageView Profile = (ImageView) findViewById(R.id.guestTab_hostProfile);
            TextView Name = (TextView) findViewById(R.id.guestTab_hostName);
            TextView Bank = (TextView) findViewById(R.id.guestTab_hostBank);
            TextView Number = (TextView) findViewById(R.id.guestTab_hostNumber);
            TextView date = (TextView) findViewById(R.id.guestTab_date);
            TextView Left = (TextView) findViewById(R.id.guestTab_debit_left);
            TextView content = (TextView) findViewById(R.id.guestTab_content);
            Name.setText(user_host.getString("name"));
            Bank.setText(user_host.getString("account_bank"));
            Number.setText(user_host.getString("account_number"));
            date.setText(roomJSON.getString("created_date"));
            Left.setText(Integer.toString(roomJSON.getInt("debit_left")));
            content.setText(roomJSON.getString("content_text"));

            // under header bar
            ImageView statusSignal = (ImageView) findViewById(R.id.guestTab_statussignal);
            TextView statusWord = (TextView) findViewById(R.id.guestTab_statusword);
            ImageView myProfile = (ImageView) findViewById(R.id.guestTab_myProfile);
            TextView myName = (TextView) findViewById(R.id.guestTab_myName);
            TextView myDebit = (TextView) findViewById(R.id.guestTab_myDebit);
            Button requestButton = (Button) findViewById(R.id.guestTab_requestButton);
            final EditText requestMoney = (EditText) findViewById(R.id.guestTab_requestMoney);
            final TextView errorMessage = (TextView) findViewById(R.id.guestTab_error);


            JSONArray debit_guests = roomJSON.getJSONArray("debit_guests");
            CustomInfoAdapter.GuestRoomguestAdapter guestRoomguestAdapter = new CustomInfoAdapter.GuestRoomguestAdapter();
            int len = debit_guests.length();
            for (int i = 0; i < len; i++) {
                JSONObject debit = debit_guests.getJSONObject(i);
                JSONObject user = debit.getJSONObject("user");
                String myid = user.getString("facebook_id");
                if (myid.equals(Facebook_Id)) {
                    // if guest is me
                    myName.setText(user.getString("name"));
                    myDebit.setText(debit.getString("price")+"원");
                    int status = debit.getInt("paidStatus");
                    if (status == 0) {
                        statusSignal.setImageResource(R.color.red);
                        statusWord.setText("정산안함");
                    } else if (status == 1) {
                        statusSignal.setImageResource(R.color.yellow);
                        statusWord.setText("승인대기");
                    } else if (status ==2) {
                        statusSignal.setImageResource(R.color.green);
                        statusWord.setText("정산완료");
                    }
                    requestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String money = requestMoney.getText().toString();
                            try {
                                int req_money = Integer.parseInt(money);
                                
                            } catch (Exception e) {
                                errorMessage.setText("숫자를 입력하세요");
                            }
                        }
                    });

                } else {
                    // another people
                    guestRoomguestAdapter.addGuestRoomguest(debit);
                }
            }
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
