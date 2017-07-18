package me.jihoon.bonae_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
        public CustomInfo.User get(int pos){
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
                convertView = inflater.inflate(R.layout.user, parent, false);
            }
            ImageView user_image = (ImageView) convertView.findViewById(R.id.user_image);
            TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
            CustomInfo.User user = UserList.get(position);

            // user_image도 처리
            // Glide.with(getApplicationContext()).load(customAddress.getAdrsimage()).into(adrs_image);
            new imageTask(user_image).execute(user.getUser_id());
            user_name.setText(user.getNickName());

            return convertView;
        }
        public void addUser(int pos, String fbId, String name, String bank, String num) {
            CustomInfo.User user = new CustomInfo.User();
            user.setUser_id(fbId);
            user.setNickName(name);
            user.setAccountBank(bank);
            user.setAccountNumber(num);
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

    public static class HostRoomAdapter extends BaseAdapter {
        private ArrayList<CustomInfo.HostRoom> HostRoomList = new ArrayList<CustomInfo.HostRoom>();

        @Override
        public int getCount() {
            return HostRoomList.size();
        }

        @Override
        public Object getItem(int position) {
            return HostRoomList.get(position);
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
                convertView = inflater.inflate(R.layout.activity_host_room, parent, false);
            }

            ImageView Profile = (ImageView) convertView.findViewById(R.id.hostTab_hostProfile);
            TextView Name = (TextView) convertView.findViewById(R.id.hostTab_hostName);
            TextView Bank = (TextView) convertView.findViewById(R.id.hostTab_hostBank);
            TextView Number = (TextView) convertView.findViewById(R.id.hostTab_hostNumber);
            TextView date = (TextView) convertView.findViewById(R.id.hostTab_date);
            TextView content = (TextView) convertView.findViewById(R.id.hostTab_content);

            ListView payNo = (ListView) convertView.findViewById(R.id.hostTab_payNo);
            ListView payYes = (ListView) convertView.findViewById(R.id.hostTab_payYes);

            CustomInfoAdapter.HostRoomguestAdapter NoHostRoomguestAdapter = new CustomInfoAdapter.HostRoomguestAdapter();
            CustomInfoAdapter.HostRoomguestAdapter YesHostRoomguestAdapter = new CustomInfoAdapter.HostRoomguestAdapter();

            CustomInfo.HostRoom hostRoom = HostRoomList.get(position);
            Name.setText(hostRoom.getHostName());
            Bank.setText(hostRoom.getAccountBank());
            Number.setText(hostRoom.getAccountNumber());
            date.setText(hostRoom.getCreated_date());
            content.setText(hostRoom.getContent());

            JSONArray debits = hostRoom.getDebit_guests();
            int len = debits.length();
            for (int i = 0; i < len; i++) {
                try {
                    JSONObject object = debits.getJSONObject(i);
                    if (object.getInt("paidStatus") == 2) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            payNo.setAdapter(NoHostRoomguestAdapter);
            payYes.setAdapter(YesHostRoomguestAdapter);

            return convertView;
        }

        public void addHostRoom(String hostName, String hostBank, String hostNumber, String created_date, String content, JSONArray debit_guests) {
            CustomInfo.HostRoom hostRoom = new CustomInfo.HostRoom();
            hostRoom.setHostName(hostName);
            hostRoom.setAccountBank(hostBank);
            hostRoom.setAccountNumber(hostNumber);
            hostRoom.setCreated_date(created_date);
            hostRoom.setContent(content);
            hostRoom.setDebit_guests(debit_guests);

            HostRoomList.add(hostRoom);
        }
    }

    public static class HostRoomguestAdapter extends BaseAdapter {
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

            ImageView guestProfile = (ImageView) convertView.findViewById(R.id.hostTab_guestProfile);
            TextView guestName = (TextView) convertView.findViewById(R.id.hostTab_guestName);
            TextView guestMoney = (TextView) convertView.findViewById(R.id.hostTab_guestMoney);
            Button yesButton = (Button) convertView.findViewById(R.id.hostTab_yesButton);
            Button noButton = (Button) convertView.findViewById(R.id.hostTab_noButton);

            CustomInfo.HostRoomguest hostRoomguest = HostRoomguestList.get(position);
            guestName.setText(hostRoomguest.getGuestName());
            int price = hostRoomguest.getPrice();
            int paid = hostRoomguest.getPaid();
            String money = Integer.toString(price-paid);
            guestMoney.setText(money);

            return convertView;
        }

        public void addGuest(JSONObject debit_guest) {
            CustomInfo.HostRoomguest hostRoomguest = new CustomInfo.HostRoomguest();
            try {
                JSONObject user = debit_guest.getJSONObject("user");
                hostRoomguest.setGuestName(user.getString("name"));
                hostRoomguest.setGuestId(user.getString("facebook_id"));
                hostRoomguest.setPrice(debit_guest.getInt("price"));
                hostRoomguest.setPaid(debit_guest.getInt("paid"));
                hostRoomguest.setPaidStatus(debit_guest.getInt("paidStatus"));
                hostRoomguest.setPaidPending(debit_guest.getInt("paidPending"));
                HostRoomguestList.add(hostRoomguest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class GuestRoomAdapter extends BaseAdapter {
        private ArrayList<CustomInfo.GuestRoom> GuestRoomList = new ArrayList<CustomInfo.GuestRoom>();

        @Override
        public int getCount() {
            return GuestRoomList.size();
        }

        @Override
        public Object getItem(int position) {
            return GuestRoomList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
