package me.jihoon.bonae_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by q on 2017-07-13.
 */

public class Tab4Fragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab4, container, false);

        JSONObject UserJSON = null;
        String UserInfo = this.getArguments().getString("user");
        try {
            UserJSON = new JSONObject(UserInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView myName = (TextView) view.findViewById(R.id.myName);
        TextView myaccountBank = (TextView) view.findViewById(R.id.myaccountBank);
        TextView myaccountNumber = (TextView) view.findViewById(R.id.myaccountNumber);

        String Name = "";
        String accountBank = "";
        String accountNumber = "";
        try {
            Name = UserJSON.getString("name");
            accountBank = UserJSON.getString("account_bank");
            accountNumber = UserJSON.getString("account_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myName.setText(Name);
        myaccountBank.setText(accountBank);
        myaccountNumber.setText(accountNumber);

        Button editButton = (Button) view.findViewById(R.id.editmyInfoButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        return view;
    }
}
