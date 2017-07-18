package me.jihoon.bonae_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by q on 2017-07-13.
 */

public class Tab1Fragment extends Fragment {

    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;

    public void Setting(LayoutInflater inflater, ViewGroup container) {
        Token = this.getArguments().getString("Token");
        Facebook_Id = this.getArguments().getString("Facebook_Id");
        Facebook_Name = this.getArguments().getString("Facebook_Name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        CustomInfoAdapter.UserAdapter adapter = new CustomInfoAdapter.UserAdapter();
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        Setting(inflater, container);

        ListView yes_list = (ListView) view.findViewById(R.id.listView_tab1yes);
        adapter.addUser("TestUser1");

        yes_list.setAdapter(adapter);

        return view;
    }
}
