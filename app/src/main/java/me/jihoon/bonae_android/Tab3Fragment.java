package me.jihoon.bonae_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by q on 2017-07-13.
 */

public class Tab3Fragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);

        Setting(inflater, container);

        ListView listView = (ListView) view.findViewById(R.id.listview_tab3);
        CustomInfoAdapter.RoomAdapter adapter = makeGuestRoom();
        listView.setAdapter(adapter);

        return view;
    }

    private CustomInfoAdapter.RoomAdapter makeGuestRoom() {
        CustomInfoAdapter.RoomAdapter adapter = new CustomInfoAdapter.RoomAdapter();
        adapter.addRoom("11223344", "도미노피자", "3000", "2017-07-20");
        try {
            JSONArray GuestRoom = UserJSON.getJSONArray("room_pending_guest");
            int len = GuestRoom.length();
            for (int i = len-1; i >= 0; i--) {
                JSONObject guestRoomJSON = GuestRoom.getJSONObject(i);
                String roomID = guestRoomJSON.getString("room");
                String keyword = guestRoomJSON.getString("keyword");
                String price = guestRoomJSON.getString("left");
                String Date = guestRoomJSON.getString("created_date");
                adapter.addRoom(roomID, keyword, price, Date);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.addRoom("12345678", "미스터피자", "6000", "2017-07-16");

        return adapter;
    }
}
