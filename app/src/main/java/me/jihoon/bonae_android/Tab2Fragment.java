package me.jihoon.bonae_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by q on 2017-07-13.
 */

public class Tab2Fragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        CustomInfoAdapter.RoomAdapter adapter = new CustomInfoAdapter.RoomAdapter();
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listview_tab2);
        adapter.addRoom("jihoon Ko", 5000, "치킨");

        listView.setAdapter(adapter);

        return view;
    }
}
