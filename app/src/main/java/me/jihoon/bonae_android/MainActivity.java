package me.jihoon.bonae_android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQ_ADD_LOGIN = 1;
    static final int REQ_CREATE_ROOM = 3;
    static final int REQ_EDIT_PROFILE = 6;
    String Token = null;
    String Facebook_Id = null;
    String Facebook_Name = null;
    String UserInfo = null;

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, REQ_ADD_LOGIN);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.commit();
    }

    private void setupViewPager(ViewPager viewpager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "친구");
        adapter.addFragment(new Tab2Fragment(), "받을 돈");
        adapter.addFragment(new Tab3Fragment(), "줄 돈");
        adapter.addFragment(new Tab4Fragment(), "설정");
        viewpager.setAdapter(adapter);
    }

    public class SectionsPageAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            Bundle User = new Bundle();
            User.putString("user", UserInfo);
            User.putString("Token", Token);
            User.putString("Facebook_Id", Facebook_Id);
            User.putString("Facebook_Name", Facebook_Name);
            fragment.setArguments(User);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public SectionsPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        requestCode = requestCode & 0xFFFF;
        if(requestCode == 284) {
            Tab1Fragment.onSuccess(requestCode, resultCode, intent);
            return;
        }
        UserInfo = intent.getStringExtra("user_info");
        Token = intent.getStringExtra("Token");
        Facebook_Id = intent.getStringExtra("Facebook_Id");
        Facebook_Name = intent.getStringExtra("Facebook_Name");
        if (requestCode == REQ_ADD_LOGIN) {
            if (resultCode == RESULT_OK) {

                mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

                mViewPager = (ViewPager) findViewById(R.id.container);
                setupViewPager(mViewPager);

                TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
                tablayout.setupWithViewPager(mViewPager);
            }
        } else if (requestCode == REQ_CREATE_ROOM) {
            if (resultCode == RESULT_OK) {
                String foodType = intent.getStringExtra("foodType");
                String foodPrice = intent.getStringExtra("foodPrice");
                Boolean divide = intent.getExtras().getBoolean("divide");

                new HTTPCreateRoom().execute("http://52.78.17.108:3000/room/create/", Token, Facebook_Id, Facebook_Name, foodType, foodPrice, divide.toString());
            }
        }
    }

    private class HTTPCreateRoom extends AsyncTask<String, Void, String> {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_create_room, null, false);
        TextView loading = (TextView) view.findViewById(R.id.saveRoomLoading);
        @Override
        protected void onPreExecute() {
            loading.setText("Creating Room...");
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);
                connection.setRequestProperty("x-access-token", Token);
                connection.setRequestProperty("x-access-id", Facebook_Id);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject bodyJson = new JSONObject();
                bodyJson.put("host", Facebook_Id);

                String name = "JihoonKo";
                List<String> guests = new ArrayList<String>();
                name = "!" + name;
                guests.add(name);
                //"[!JihoonKo]" -> ["!JihoonKo"]
                JSONArray guest = new JSONArray(guests);
                bodyJson.put("guests", guest);
                bodyJson.put("keyword", params[4]);
                if ("true".equals(params[6])) {
                    bodyJson.put("divide", true);
                } else if ("false".equals(params[6])) {
                    bodyJson.put("divide", false);
                }
                bodyJson.put("price", params[5]);

                String http_req = bodyJson.toString();

                System.out.println(http_req);
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(http_req);
                osw.flush();
                osw.close();

                int resCode = connection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == resCode) {
                    Log.e("Connect Complete", "HTTP_OK");
                    String http_res = getTextFrom(connection.getInputStream());

                    JSONObject resJson = new JSONObject(http_res);
                    result = resJson.toString();
                    Log.e("Response from Server", result);
                } else {
                    result = connection.getResponseCode() + "-" + connection.getResponseMessage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            loading.setText("Room Create Complete!");
            JSONObject JSONResponse = null;
            String Room_Id = null;
            try {
                JSONResponse = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Room_Id = JSONResponse.getString("id");
                Log.e("RoomID", Room_Id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra("Room_Id", Room_Id);
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
