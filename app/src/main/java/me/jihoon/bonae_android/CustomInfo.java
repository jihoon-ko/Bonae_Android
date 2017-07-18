package me.jihoon.bonae_android;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by q on 2017-07-13.
 */

public class CustomInfo {
    public static class User {
        private String user_fbid;
        private String fbName;
        private String NickName;
        private Drawable profileImage;
        private String accountBank;
        private int accountNumber;
        private List<Room> hostRoom;
        private List<Room> guestRoom;
        private List<String> friend;

        public void setUser_id (String ID) {
            this.user_fbid = ID;
        }
        public String getUser_id() {
            return this.user_fbid;
        }

        public void setfbName (String NAME) {
            this.fbName = NAME;
        }
        public String getfbName() {
            return this.fbName;
        }

        public void setNickName (String NAME) {
            this.NickName = NAME;
        }
        public String getNickName() {
            return this.NickName;
        }

        public void setProfileImage (Drawable image) { this.profileImage = image; }
        public Drawable getProfileImage() { return  this.profileImage; }

        public void setAccountBank (String BANK) {
            this.accountBank = BANK;
        }
        public String getAccountBank() {
            return this.accountBank;
        }

        public void setAccountNumber (int NUMBER) {
            this.accountNumber = NUMBER;
        }
        public int getAccountNumber() {
            return this.accountNumber;
        }


        public void setAllHostRoom(List<Room> allHostRoom) {
            if (this.hostRoom == null) {
                this.hostRoom = new ArrayList<Room>();
            }
            int len = allHostRoom.size();
            for (int i = 0; i < len; i++) {
                this.hostRoom.add(allHostRoom.get(i));
            }
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
        public void setAllGuestRoom (List<Room> allGuestRoom) {
            if (this.guestRoom == null) {
                this.guestRoom = new ArrayList<Room>();
            }
            int len = allGuestRoom.size();
            for (int i = 0; i < len; i++) {
                this.guestRoom.add(allGuestRoom.get(i));
            }
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
        private String content;
        private String price;
        private String date;
        private String number;
        private int receipt;

        public void setRoom_id (String ID) {
            this.room_id = ID;
        }
        public String getRoom_id() {
            return this.room_id;
        }

        public void setContent(String CONTENT) {
            this.content = CONTENT;
        }
        public String getContent() {
            return this.content;
        }

        public void setPrice (String PRICE) {
            this.price = PRICE;
        }
        public String getPrice() {
            return this.price;
        }

        public void setDate (String DATE) {
            this.date = DATE;
        }
        public String getDate() {
            return this.date;
        }

        public void setNumber (String EAT) {
            this.number = EAT;
        }
        public String getNumber() {
            return this.number;
        }
/*
        public String makeTitle () {
            if (this.guestUser.size() == 0) {
                return "No People";
            }
            String title = this.guestUser.get(0);
            for (int i = 1; i < this.guestUser.size(); i++) {
                title = title + "," + this.guestUser.get(i);
            }
            return title;
        }*/
    }
    public static class HostRoom {
        private String roomId;
        private String hostName;
        private String hostId;
        private String accountBank;
        private String accountNumber;
        private JSONArray debit_guests;
        private String created_date;
        private String content;
        private int receipt;

        public void setDebit_guests (JSONArray Debit) {
            this.debit_guests = Debit;
        }
        public JSONArray getDebit_guests() {
            return this.debit_guests;
        }

        public void setRoomId (String ID) {
            this.roomId = ID;
        }
        public String getRoomId () {
            return this.roomId;
        }

        public void setHostName (String name) { this.hostName = name; }
        public String getHostName() { return this.hostName; }

        public void setHostId (String ID) { this.hostId = ID; }
        public String getHostId() { return this.hostId; }

        public void setAccountBank (String Bank) { this.accountBank = Bank; }
        public String getAccountBank() { return this.accountBank; }

        public void setAccountNumber (String Number) { this.accountNumber = Number; }
        public String getAccountNumber() { return this.accountNumber; }

        public void setCreated_date (String date) { this.created_date = date; }
        public String getCreated_date() { return this.created_date; }

        public void setContent (String content) { this.content = content; }
        public String getContent() { return this.content; }

        public void setReceipt (int RECEIPT) {
            this.receipt = RECEIPT;
        }
        public int getReceipt() {
            return this.receipt;
        }
    }

    public static class HostRoomguest {
        private String guestName;
        private String guestId;
        private int price;
        private int paid;
        private int paidStatus;
        private int paidPending;

        public void setGuestName (String name) {this.guestName = name; }
        public String getGuestName() {return this.guestName; }

        public void setGuestId (String id) {this.guestId = id; }
        public String getGuestId() {return this.guestId; }

        public void setPrice (int Price) {this.price = Price; }
        public int getPrice() {return this.price; }

        public void setPaid (int Paid) {this.paid = Paid; }
        public int getPaid() {return this.paid; }

        public void setPaidStatus (int paidstatus) {this.paidStatus = paidstatus; }
        public int getPaidStatus() {return this.paidStatus; }

        public void setPaidPending (int paidpending) {this.paidPending = paidpending; }
        public int getPaidPending() {return this.paidPending; }
    }

    public static class GuestRoom {
        private String roomId;
        private String hostName;
        private String hostId;
        private String accountBank;
        private String accountNumber;
        private List<String> guestName;
        private List<String> guestId;
        private List<String> price;
        private List<String> paid;
        private String created_date;
        private String content;
        private int receipt;

        public void setRoomId (String ID) {
            this.roomId = ID;
        }
        public String getRoomId () {
            return this.roomId;
        }

        public void setHostName (String name) { this.hostName = name; }
        public String getHostName() { return this.hostName; }

        public void setHostId (String ID) { this.hostId = ID; }
        public String getHostId() { return this.hostId; }

        public void setAccountBank (String Bank) { this.accountBank = Bank; }
        public String getAccountBank() { return this.accountBank; }

        public void setAccountNumber (String Number) { this.accountNumber = Number; }
        public String getAccountNumber() { return this.accountNumber; }

        public void setGuestName (List<String> guestname) {
            if (this.guestName == null) {
                this.guestName = new ArrayList<String>();
            }
            int len = guestname.size();
            for (int i = 0; i < len; i++) {
                this.guestName.add(guestname.get(i));
            }
        }
        public List<String> getGuestName() { return this.guestName; }

        public void setGuestId (List<String> guestid) {
            if (this.guestId == null) {
                this.guestId = new ArrayList<String>();
            }
            int len = guestid.size();
            for (int i = 0; i < len; i++) {
                this.guestId.add(guestid.get(i));
            }
        }
        public List<String> getGuestId() { return this.guestId; }

        public void setPrice (List<String> Price) {
            if (this.price == null) {
                this.price = new ArrayList<String>();
            }
            int len = Price.size();
            for (int i = 0; i < len; i++) {
                this.price.add(Price.get(i));
            }
        }
        public List<String> getPrice() { return this.price; }

        public void setPaid (List<String> Paid) {
            if (this.paid == null) {
                this.paid = new ArrayList<String>();
            }
            int len = Paid.size();
            for (int i = 0; i < len; i++) {
                this.paid.add(Paid.get(i));
            }
        }
        public List<String> getPaid() { return this.paid; }

        public void setCreated_date (String date) { this.created_date = date; }
        public String getCreated_date() { return this.created_date; }

        public void setContent (String content) { this.content = content; }
        public String getContent() { return this.content; }

        public void setReceipt (int RECEIPT) {
            this.receipt = RECEIPT;
        }
        public int getReceipt() {
            return this.receipt;
        }
    }

}
