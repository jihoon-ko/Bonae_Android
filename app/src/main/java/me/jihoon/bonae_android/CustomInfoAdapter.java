package me.jihoon.bonae_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

            CustomInfo.Room room = RoomList.get(position);
            ImageView room_image = (ImageView) convertView.findViewById(R.id.room_image);
            TextView roomTitle = (TextView) convertView.findViewById(R.id.roomTitle);
            TextView guestNumber = (TextView) convertView.findViewById(R.id.guestNumber);
            TextView foodType = (TextView) convertView.findViewById(R.id.foodType);
            TextView foodPrice = (TextView) convertView.findViewById(R.id.foodPrice);

            // room_image Glide로 이미지 추가
            String title = room.makeTitle();
            roomTitle.setText(title);
            guestNumber.setText(Integer.toString(room.getGuestUser().size()));
            foodType.setText(room.getEat());
            foodPrice.setText(Integer.toString(room.getPrice()));

            return convertView;
        }
        public void addRoom(String guestUser, int price, String eat) {
            CustomInfo.Room room = new CustomInfo.Room();
            room.setGuestUser(guestUser);
            room.setPrice(price);
            room.setEat(eat);

            RoomList.add(room);
        }
    }

}
