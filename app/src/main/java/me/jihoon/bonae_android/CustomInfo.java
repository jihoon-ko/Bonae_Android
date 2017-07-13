package me.jihoon.bonae_android;

import java.util.List;

/**
 * Created by q on 2017-07-13.
 */

public class CustomInfo {
    public class User {
        private int id;
        private List<Room> hostRoom;
        private List<Room> guestRoom;
        private String nickName;
        private int account;
        private List<String> friend;

        public void setId (int ID) {
            id = ID;
        }
    }

    public class Room {
        private String hostUser;
        private List<String> guestUser;
        private int price;
        private int date;
        private String eat;
        private int receipt;
    }

}
