package me.jihoon.bonae_android;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by q on 2017-07-13.
 */

public class CustomInfo {
    public static class User {
        private String user_id;
        private List<Room> hostRoom;
        private List<Room> guestRoom;
        private String nickName;
        private int account;
        private List<String> friend;

        public void setUser_id (String ID) {
            this.user_id = ID;
        }
        public String getUser_id() {
            return this.user_id;
        }

        public void setHostRoom (Room HOSTROOM) {
            if (this.hostRoom == null) {
                this.hostRoom = new ArrayList<Room>();
            }
            this.hostRoom.add(HOSTROOM);
        }
        public List<Room> getHostRoom() {
            return this.hostRoom;
        }
        public Room getHostRoom(String roomID) {
            int len = this.hostRoom.size();
            for (int i = 0; i < len; i++) {
                if (this.hostRoom.get(i).getRoom_id() == roomID) {
                    return this.hostRoom.get(i);
                }
            }
            return null;
        }

        public void setGuestRoom (Room GUESTROOM) {
            if (this.guestRoom == null) {
                this.guestRoom = new ArrayList<Room>();
            }
            this.guestRoom.add(GUESTROOM);
        }
        public List<Room> getGuestRoom() {
            return this.guestRoom;
        }
        public Room getGuestRoom (String roomID) {
            int len = this.guestRoom.size();
            for (int i = 0; i < len; i++) {
                if (this.guestRoom.get(i).getRoom_id() == roomID) {
                    return this.guestRoom.get(i);
                }
            }
            return null;
        }

        public void setNickName (String NICKNAME) {
            this.nickName = NICKNAME;
        }
        public String getNickName() {
            return this.nickName;
        }

        public void setAccount (int ACOOUNT) {
            this.account = ACOOUNT;
        }
        public int getAccount() {
            return this.account;
        }

        public void setFriend (String FRIEND) {
            if (this.friend == null) {
                this.friend = new ArrayList<String>();
            }
            this.friend.add(FRIEND);
        }
        public List<String> getFriend() {
            return this.friend;
        }
    }

    public static class Room {
        private String room_id;
        private String hostUser;
        private List<String> guestUser;
        private int price;
        private int date;
        private String eat;
        private int receipt;

        public void setRoom_id (String ID) {
            this.room_id = ID;
        }
        public String getRoom_id() {
            return this.room_id;
        }

        public void setHostUser (String USER) {
            this.hostUser = USER;
        }
        private String getHostUser() {
            return this.hostUser;
        }

        public void setGuestUser (String USER) {
            if (this.guestUser == null) {
                this.guestUser = new ArrayList<String>();
            }
            this.guestUser.add(USER);
        }
        public List<String> getGuestUser() {
            return this.guestUser;
        }

        public void setPrice (int PRICE) {
            this.price = PRICE;
        }
        public int getPrice() {
            return this.price;
        }

        public void setDate (int DATE) {
            this.date = DATE;
        }
        public int getDate() {
            return this.date;
        }

        public void setEat (String EAT) {
            this.eat = EAT;
        }
        public String getEat() {
            return this.eat;
        }

        public void setReceipt (int RECEIPT) {
            this.receipt = RECEIPT;
        }
        public int getReceipt() {
            return this.receipt;
        }

        public String makeTitle () {
            if (this.guestUser.size() == 0) {
                return "No People";
            }
            String title = this.guestUser.get(0);
            for (int i = 1; i < this.guestUser.size(); i++) {
                title = title + "," + this.guestUser.get(i);
            }
            return title;
        }
    }
}
