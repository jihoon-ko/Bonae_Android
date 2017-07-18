package me.jihoon.bonae_android;

import android.content.Context;
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

import java.util.ArrayList;

/**
 * Created by q on 2017-07-14.
 */

public class CustomInfoAdapter {
    public static class UserAdapter extends BaseAdapter {
        private ArrayList<CustomInfo.User> UserList = new ArrayList<CustomInfo.User>();

        public UserAdapter() {
        }

        @Override
        public int getCount() {
            return UserList.size();
        }

        @Override
        public Object getItem(int position) {
            return UserList.get(position);
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
            user_name.setText(user.getNickName());

            return convertView;
        }
        public void addUser(String name) {
            CustomInfo.User user = new CustomInfo.User();
            user.setNickName(name);

            UserList.add(user);
        }
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
