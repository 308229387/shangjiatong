package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class AwardHistoryResponse {

    private ArrayList<AwardHistory> data;

    public ArrayList<AwardHistory> getData() {
        return data;
    }

    public void setData(ArrayList<AwardHistory> data) {
        this.data = data;
    }

    public class AwardHistory implements Serializable {
        private String id;
        private String prize_name;
        private String user_name;
        private String win_time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrize_name() {
            return prize_name;
        }

        public void setPrize_name(String prize_name) {
            this.prize_name = prize_name;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getWin_time() {
            return win_time;
        }

        public void setWin_time(String win_time) {
            this.win_time = win_time;
        }
    }
}