package me.jihoon.bonae_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by q on 2017-07-14.
 */

public class CustomInfoAdapter {
    public static class UserAdapter extends BaseAdapter {
        private ArrayList<CustomInfo.User> UserList;
        private ArrayList<CustomInfo.User> SearchList;
        private int adapter_id;
        private String keyword;
        public String token, facebookId, facebookName;

        public UserAdapter(int id, String _token, String _fbId, String _fbName) {
            UserList = new ArrayList<CustomInfo.User>();
            SearchList = new ArrayList<CustomInfo.User>();
            adapter_id = id;
            keyword = "";
            token = _token; facebookId = _fbId; facebookName = _fbName;
        }
        public String getKeyword(){
            return keyword;
        }
        @Override
        public int getCount() {
            if(adapter_id == 1) return SearchList.size();
            else return UserList.size();
        }

        public int userCnt(){
            return UserList.size();
        }
        public CustomInfo.User gett(int pos){
            return UserList.get(pos);
        }
        @Override
        public Object getItem(int position) {
            if(adapter_id == 1) return SearchList.get(position);
            else return UserList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(adapter_id == 1){
                    convertView = inflater.inflate(R.layout.user, parent, false);
                }else{
                    convertView = inflater.inflate(R.layout.user_add, parent, false);
                }
            }
            ImageView user_image = (ImageView) convertView.findViewById(R.id.user_image);
            TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
            CustomInfo.User user = (CustomInfo.User) getItem(position);

            // user_image도 처리
            // Glide.with(getApplicationContext()).load(customAddress.getAdrsimage()).into(adrs_image);
            new imageTask(user_image).execute(user.getUser_id());
            user_name.setText(user.getNickName());

            if(adapter_id == 2){
                ImageView friend_image = (ImageView) convertView.findViewById(R.id.user_can_add);
                if(user.getf()) friend_image.setVisibility(View.INVISIBLE);
                else friend_image.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
        public void addUser(int pos, String fbId, String name, String bank, String num, boolean is_friend) {
            CustomInfo.User user = new CustomInfo.User();
            user.setUser_id(fbId);
            user.setNickName(name);
            user.setAccountBank(bank);
            user.setAccountNumber(num);
            user.setf(is_friend);
            if(pos == -1) {
                UserList.add(user);
            }else{
                UserList.add(pos, user);
            }
            changeData(keyword);
        }
        public void removeUser(int pos){
            UserList.remove(pos);
            changeData(keyword);
        }
        public void clearUser(){
            UserList.clear();
            changeData(keyword);
        }
        public boolean admit(String str1, String str2){
            str1 = str1.toLowerCase().replaceAll(" ", "");
            str2 = str2.toLowerCase().replaceAll(" ", "");
            return str1.contains(str2);
        }
        public void changeData(String new_keyword){
            keyword = new_keyword;
            if(adapter_id == 1) {
                SearchList.clear();
                notifyDataSetChanged();
                for (int i = 0; i < UserList.size(); i++) {
                    if (admit(UserList.get(i).getNickName(), keyword)) {
                        SearchList.add(UserList.get(i));
                        notifyDataSetChanged();
                    }
                }
            }
            notifyDataSetChanged();
        }
        private class imageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView iv;
            private OkHttpClient client;
            public imageTask(ImageView _iv){
                iv = _iv;
                client = new OkHttpClient();
            }
            private String send(String url) throws IOException {
                //RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("x-access-token", token)
                        .addHeader("x-access-id", facebookId)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            @Override
            protected Bitmap doInBackground(String... str){
                try {
                    System.out.println("id: " + adapter_id);
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

    public static class RoomAdapter extends BaseAdapter {
        private ArrayList<CustomInfo.Room> RoomList = new ArrayList<CustomInfo.Room>();

        public RoomAdapter() {

        }

        @Override
        public int getCount() {
            return RoomList.size();
        }

        @Override
        public Object getItem(int position) {
            return RoomList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.room, parent, false);
            }

            ImageView room_image = (ImageView) convertView.findViewById(R.id.room_image);
            TextView Title = (TextView) convertView.findViewById(R.id.roomTitle);
            TextView Number = (TextView) convertView.findViewById(R.id.guestNumber);
            TextView Price = (TextView) convertView.findViewById(R.id.foodPrice);
            TextView Date = (TextView) convertView.findViewById(R.id.roomCreateDate);

            CustomInfo.Room room = RoomList.get(position);
            Title.setText(room.getContent());
            Number.setText(room.getNumber());
            Price.setText(room.getPrice());
            Date.setText(room.getDate());

            return convertView;
        }
        public void addRoom(String roomID, String content, String number, String price, String date) {
            CustomInfo.Room room = new CustomInfo.Room();
            room.setRoom_id(roomID);
            room.setContent(content);
            room.setNumber(number);
            room.setPrice(price);
            room.setDate(date);

            RoomList.add(room);
        }
        public void addRoom(String roomID, String content, String price, String date) {
            CustomInfo.Room room = new CustomInfo.Room();
            room.setRoom_id(roomID);
            room.setContent(content);
            room.setPrice(price);
            room.setDate(date);

            RoomList.add(room);
        }
    }

    public static class HostRoomguestAdapter extends BaseAdapter {
        TextView loading = null;
        ImageView guestStatus = null;
        TextView requestMessage = null;
        Button yesButton = null;
        Button noButton = null;
        String mydebit = null;
        String RoomId = null;
        String Token = null;
        String Facebook_Id = null;
        String Facebook_Name = null;
        String keyword = null;

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

        private ArrayList<CustomInfo.HostRoomguest> HostRoomguestList = new ArrayList<CustomInfo.HostRoomguest>();

        @Override
        public int getCount() {
            return HostRoomguestList.size();
        }

        @Override
        public Object getItem(int position) {
            return HostRoomguestList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.hostroom, parent, false);
            }
            guestStatus = (ImageView) convertView.findViewById(R.id.hostTab_guestStatus);
            ImageView guestProfile = (ImageView) convertView.findViewById(R.id.hostTab_guestProfile);
            TextView guestName = (TextView) convertView.findViewById(R.id.hostTab_guestName);
            TextView guestMoney = (TextView) convertView.findViewById(R.id.hostTab_guestMoney);
            requestMessage = (TextView) convertView.findViewById(R.id.hostTab_requestMessage);
            yesButton = (Button) convertView.findViewById(R.id.hostTab_yesButton);
            noButton = (Button) convertView.findViewById(R.id.hostTab_noButton);
            loading = (TextView) convertView.findViewById(R.id.hostTab_loading);

            CustomInfo.HostRoomguest hostRoomguest = HostRoomguestList.get(position);
            final int status = hostRoomguest.getPaidStatus();
            final String debit_id = hostRoomguest.getDebitId();

            String profile = hostRoomguest.getGuestId();

            new imageTask(guestProfile).execute(profile);
            RoomId = hostRoomguest.getRoomId();
            Token = hostRoomguest.getToken();
            Facebook_Id = hostRoomguest.getFacebook_Id();
            Facebook_Name = hostRoomguest.getFacebook_Name();
            if (status == 0) {
                guestStatus.setImageResource(R.color.red);
                yesButton.setVisibility(View.VISIBLE);
                noButton.setVisibility(View.INVISIBLE);
                requestMessage.setText("");
                loading.setText("");
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HTTPAccept().execute("http://52.78.17.108:3000/room/id/"+RoomId+"/accept", Token, Facebook_Id, Facebook_Name, debit_id);
                    }
                });
            } else if (status == 1) {
                guestStatus.setImageResource(R.color.yellow);
                yesButton.setVisibility(View.VISIBLE);
                noButton.setVisibility(View.VISIBLE);
                int pending = hostRoomguest.getPaidPending();
                String msg = Integer.toString(pending);
                requestMessage.setText(msg + "원 요청!");
                loading.setText("");
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HTTPAccept().execute("http://52.78.17.108:3000/room/id/"+RoomId+"/accept", Token, Facebook_Id, Facebook_Name, debit_id);
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new HTTPReject().execute("http://52.78.17.108:3000/room/id/"+RoomId+"/reject", Token, Facebook_Id, Facebook_Name, debit_id);
                    }
                });

            } else if (status == 2) {
                guestStatus.setImageResource(R.color.green);
                yesButton.setVisibility(View.INVISIBLE);
                noButton.setVisibility(View.INVISIBLE);
                requestMessage.setText("");
                loading.setText("");
            }
            guestName.setText(hostRoomguest.getGuestName());
            int price = hostRoomguest.getPrice();
            int paid = hostRoomguest.getPaid();
            String money = Integer.toString(price-paid) + "원";
            guestMoney.setText(money);
//            requestMessage.setText();

            return convertView;
        }

        public void addHostRoomguest(JSONObject debit_guest, String RoomId, String Token, String Facebook_Id, String Facebook_Name) {
            CustomInfo.HostRoomguest hostRoomguest = new CustomInfo.HostRoomguest();
            try {
                JSONObject user = debit_guest.getJSONObject("user");
                hostRoomguest.setGuestName(user.getString("name"));
                hostRoomguest.setGuestId(user.getString("facebook_id"));
                hostRoomguest.setPrice(debit_guest.getInt("price"));
                hostRoomguest.setPaid(debit_guest.getInt("paid"));
                hostRoomguest.setPaidStatus(debit_guest.getInt("paidStatus"));
                hostRoomguest.setPaidPending(debit_guest.getInt("paidPending"));
                hostRoomguest.setDebitId(debit_guest.getString("debit_id"));
                hostRoomguest.setRoomId(RoomId);
                hostRoomguest.setToken(Token);
                hostRoomguest.setFacebook_Id(Facebook_Id);
                hostRoomguest.setFacebook_Name(Facebook_Name);
                HostRoomguestList.add(hostRoomguest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void editHostRoomguest(JSONObject JSON, String Debit) {
            int len = HostRoomguestList.size();
            for (int i = 0; i < len; i++) {
                CustomInfo.HostRoomguest hostRoomguest = HostRoomguestList.get(i);
                String debit_id = hostRoomguest.getDebitId();
                if (Debit.equals(debit_id)) {
                    try {
                        JSONArray debit_guests = JSON.getJSONArray("debit_guests");
                        int lenJSON = debit_guests.length();
                        for (int j = 0; j < lenJSON; j++) {
                            JSONObject debit = debit_guests.getJSONObject(j);
                            String dbt = debit.getString("debit_id");
                            if (debit_id.equals(dbt)) {
                                hostRoomguest.setPaid(debit.getInt("paid"));
                                hostRoomguest.setPaidStatus(debit.getInt("paidStatus"));
                                hostRoomguest.setPaidPending(debit.getInt("paidPending"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HostRoomguestList.set(i, hostRoomguest);
                    notifyDataSetChanged();
                }
            }
        }

        private class HTTPAccept extends AsyncTask<String, Void, String> {
            String Debit = null;

            @Override
            protected void onPreExecute() {
                loading.setText("취소요청중...");
            }

            @Override
            protected String doInBackground(String... params) {
                String http_res = null;
                URL url = null;
                Debit = params[4];
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
                        String result = getTextFrom(connection.getInputStream());
                        JSONObject resJSON = new JSONObject(result);
                        resJSON.put("debit_id", Debit);
                        http_res = resJSON.toString();
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
                    String dbt = JSONResponse.getString("debit_id");

                    if ("1".equals(ok)) {
                        new HTTPConnectHostRoom().execute("http://52.78.17.108:3000/room/id/"+RoomId+"/", Token, Facebook_Id, Facebook_Name, dbt);
                    } else {
                        Log.e("There are Error!", result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private class HTTPReject extends AsyncTask<String, Void, String> {
            String Debit = null;

            @Override
            protected void onPreExecute() {
                loading.setText("취소요청중...");
            }

            @Override
            protected String doInBackground(String... params) {
                String http_res = null;
                URL url = null;
                Debit = params[4];
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
                        String result = getTextFrom(connection.getInputStream());
                        JSONObject resJSON = new JSONObject(result);
                        resJSON.put("debit_id", Debit);
                        http_res = resJSON.toString();
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
                    String dbt = JSONResponse.getString("debit_id");

                    if ("1".equals(ok)) {
                        new HTTPConnectHostRoom().execute("http://52.78.17.108:3000/room/id/"+RoomId+"/", Token, Facebook_Id, Facebook_Name, dbt);
                    } else {
                        Log.e("There are Error!", result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public class HTTPConnectHostRoom extends AsyncTask<String, Void, String> {
            String Token = null;
            String Facebook_Id = null;
            String Facebook_Name = null;
            String Debit = null;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... params) {
                Token = params[1];
                Facebook_Id = params[2];
                Facebook_Name = params[3];
                Debit = params[4];

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
                try {
                    JSONObject roomJSON = new JSONObject(result);
                    editHostRoomguest(roomJSON, Debit);

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

    public static class GuestRoomguestAdapter extends BaseAdapter {
        private ArrayList<CustomInfo.GuestRoomguest> GuestRoomguestList = new ArrayList<CustomInfo.GuestRoomguest>();

        @Override
        public int getCount() {
            return GuestRoomguestList.size();
        }

        @Override
        public Object getItem(int position) {
            return GuestRoomguestList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.guestroom, parent, false);
            }

            ImageView guestStatus = (ImageView) convertView.findViewById(R.id.guestTab_guestStatus);
            ImageView guestProfile = (ImageView) convertView.findViewById(R.id.guestTab_guestProfile);
            TextView guestName = (TextView) convertView.findViewById(R.id.guestTab_guestName);
            TextView guestMoney = (TextView) convertView.findViewById(R.id.guestTab_guestMoney);

            CustomInfo.GuestRoomguest guestRoomguest = GuestRoomguestList.get(position);
            int status = guestRoomguest.getPaidStatus();
            if (status == 0) {
                guestStatus.setImageResource(R.color.red);
            } else if (status == 1) {
                guestStatus.setImageResource(R.color.yellow);
            } else if (status == 2) {
                guestStatus.setImageResource(R.color.green);
            }

            guestName.setText(guestRoomguest.getGuestName());
            int price = guestRoomguest.getPrice();
            int paid = guestRoomguest.getPaid();
            String money = Integer.toString(price-paid) + "원";
            guestMoney.setText(money);

            return convertView;
        }

        public void addGuestRoomguest(JSONObject debit_guest) {
            CustomInfo.GuestRoomguest guestRoomguest = new CustomInfo.GuestRoomguest();
            try {
                JSONObject user = debit_guest.getJSONObject("user");
                guestRoomguest.setGuestName(user.getString("name"));
                guestRoomguest.setGuestId(user.getString("facebook_id"));
                guestRoomguest.setPrice(debit_guest.getInt("price"));
                guestRoomguest.setPaid(debit_guest.getInt("paid"));
                guestRoomguest.setPaidStatus(debit_guest.getInt("paidStatus"));
                GuestRoomguestList.add(guestRoomguest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
