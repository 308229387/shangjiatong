package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MyAwardResponse implements Serializable {

    private ArrayList<award> data;

    public ArrayList<award> getData() {
        return data;
    }

    public void setData(ArrayList<award> data) {
        this.data = data;
    }

    public class award implements Serializable {
        private String id;
        private int prize_claim_state;
        private String prize_describe;
        private String prize_name;
        private String win_time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getPrize_claim_state() {
            return prize_claim_state;
        }

        public void setPrize_claim_state(int prize_claim_state) {
            this.prize_claim_state = prize_claim_state;
        }

        public String getPrize_describe() {
            return prize_describe;
        }

        public void setPrize_describe(String prize_describe) {
            this.prize_describe = prize_describe;
        }

        public String getPrize_name() {
            return prize_name;
        }

        public void setPrize_name(String prize_name) {
            this.prize_name = prize_name;
        }

        public String getWin_time() {
            return win_time;
        }

        public void setWin_time(String win_time) {
            this.win_time = win_time;
        }
    }
}
