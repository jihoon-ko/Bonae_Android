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

public class Tab1Fragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
        CustomInfoAdapter.UserAdapter adapter = new CustomInfoAdapter.UserAdapter();
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        ListView yes_list = (ListView) view.findViewById(R.id.listView_tab1yes);
        adapter.addUser("TestUser1");

        yes_list.setAdapter(adapter);

        return view;
    }
}
