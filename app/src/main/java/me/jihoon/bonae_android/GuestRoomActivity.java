package me.jihoon.bonae_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class GuestRoomActivity extends AppCompatActivity {
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;
    String roomInfo = null;
    TextView loading = null;
    String debit_id = null;
    String mydebit_id = null;
    ImageView statusSignal = null;
    TextView statusWord = null;
    TextView won = null;
    Button requestButton = null;
    TextView errorMessage = null;
    EditText requestMoney = null;
    String RoomId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_room);

        Intent intent = getIntent();
        Token = intent.getStringExtra("Token");
        Facebook_Id = intent.getStringExtra("Facebook_Id");
        Facebook_Name = intent.getStringExtra("Facebook_Name");
        roomInfo = intent.getStringExtra("roomInfo");

        loading = (TextView) findViewById(R.id.guestTab_loading);

        try {
            JSONObject roomJSON = new JSONObject(roomInfo);
            JSONObject user_host = roomJSON.getJSONObject("user_host");
            RoomId = roomJSON.getString("id");

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
            statusSignal = (ImageView) findViewById(R.id.guestTab_statussignal);
            statusWord = (TextView) findViewById(R.id.guestTab_statusword);
            ImageView myProfile = (ImageView) findViewById(R.id.guestTab_myProfile);
            TextView myName = (TextView) findViewById(R.id.guestTab_myName);
            TextView myDebit = (TextView) findViewById(R.id.guestTab_myDebit);
            won = (TextView) findViewById(R.id.guestTab_requestwon);
            requestButton = (Button) findViewById(R.id.guestTab_requestButton);
            requestMoney = (EditText) findViewById(R.id.guestTab_requestMoney);
            errorMessage = (TextView) findViewById(R.id.guestTab_error);
            errorMessage.setText("");
            loading.setText("");

            JSONArray debit_guests = roomJSON.getJSONArray("debit_guests");
            CustomInfoAdapter.GuestRoomguestAdapter guestRoomguestAdapter = new CustomInfoAdapter.GuestRoomguestAdapter();
            int len = debit_guests.length();
            for (int i = 0; i < len; i++) {
                JSONObject debit = debit_guests.getJSONObject(i);
                JSONObject user = debit.getJSONObject("user");
                try {
                    debit_id = debit.getString("debit_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String myid = user.getString("facebook_id");
                if (myid.equals(Facebook_Id)) {
                    // if guest is me
                    mydebit_id = debit.getString("debit_id");
                    myName.setText(user.getString("name"));
                    int price = debit.getInt("price");
                    int paid = debit.getInt("paid");
                    myDebit.setText(Integer.toString(price-paid)+"원");
                    final int status = debit.getInt("paidStatus");
                    if (status == 0) {
                        statusSignal.setImageResource(R.color.red);
                        statusWord.setText("정산안함");
                    } else if (status == 1) {
                        statusSignal.setImageResource(R.color.yellow);
                        statusWord.setText("승인대기");
                        requestButton.setText("취소");
                        requestMoney.setVisibility(View.INVISIBLE);
                        won.setText("");
                    } else if (status ==2) {
                        statusSignal.setImageResource(R.color.green);
                        statusWord.setText("정산완료");
                        requestButton.setVisibility(View.INVISIBLE);
                        requestMoney.setVisibility(View.INVISIBLE);
                        won.setText("");
                    }
                    requestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (status == 0) {
                                String money = requestMoney.getText().toString();
                                try {
                                    int req_money = Integer.parseInt(money);

                                    new HTTPRequest().execute("http://52.78.17.108:3000/room/id/"+RoomId+"/request", Token, Facebook_Id, Facebook_Name, mydebit_id, money);

                                } catch (Exception e) {
                                    errorMessage.setText("숫자를 입력하세요");
                                }
                            } else if (status == 1) {
                                new HTTPCancel().execute("http://52.78.17.108:3000/room/id/"+RoomId+"/cancel", Token, Facebook_Id, Facebook_Name, mydebit_id);
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

    private class HTTPRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loading.setText("승인요청중...");
        }

        @Override
        protected String doInBackground(String... params) {
            String http_res = null;
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);
                connection.setRequestProperty("x-access-token", params[1]);
                connection.setRequestProperty("x-access-id", params[2]);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                int money = Integer.parseInt(params[5]);
                JSONObject bodyJSON = new JSONObject();
                bodyJSON.put("id", params[4]);
                bodyJSON.put("amount", money);
                String http_req = bodyJSON.toString();

                System.out.println(http_req);
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(http_req);
                osw.flush();
                osw.close();

                int resCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == resCode) {
                    Log.e("Connect Complete", "HTTP_OK");
                    http_res = getTextFrom(connection.getInputStream());
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
            JSONObject JSONResponse = null;
            String ok = null;
            try {
                JSONResponse = new JSONObject(result);
                ok = JSONResponse.getString("ok");

                if ("1".equals(ok)) {
                    loading.setText("");
                    statusSignal.setImageResource(R.color.yellow);
                    statusWord.setText("승인대기");
                    requestButton.setText("취소");
                    requestMoney.setVisibility(View.INVISIBLE);
                    won.setText("");
                } else {
                    Log.e("There are Error!", result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class HTTPCancel extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loading.setText("취소요청중...");
        }

        @Override
        protected String doInBackground(String... params) {
            String http_res = null;
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);
                connection.setRequestProperty("x-access-token", params[1]);
                connection.setRequestProperty("x-access-id", params[2]);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject bodyJSON = new JSONObject();
                bodyJSON.put("id", params[4]);
                String http_req = bodyJSON.toString();

                System.out.println(http_req);
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(http_req);
                osw.flush();
                osw.close();

                int resCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == resCode) {
                    Log.e("Connect Complete", "HTTP_OK");
                    http_res = getTextFrom(connection.getInputStream());
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
            JSONObject JSONResponse = null;
            String ok = null;
            try {
                JSONResponse = new JSONObject(result);
                ok = JSONResponse.getString("ok");

                if ("1".equals(ok)) {
                    loading.setText("");
                    statusSignal.setImageResource(R.color.red);
                    statusWord.setText("정산안함");
                    requestButton.setText("승인요청");
                    requestMoney.setVisibility(View.VISIBLE);
                    won.setText("원");
                } else {
                    Log.e("There are Error!", result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
