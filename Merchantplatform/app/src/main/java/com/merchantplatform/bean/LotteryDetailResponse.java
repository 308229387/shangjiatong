package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class LotteryDetailResponse implements Serializable {

    private data data;

    public LotteryDetailResponse.data getData() {
        return data;
    }

    public void setData(LotteryDetailResponse.data data) {
        this.data = data;
    }

    public class data implements Serializable {
        private int isVip;
        private ArrayList<String> description;
        private ArrayList<award> prizeList;
        private int score;
        private String openTime;
        private String endTime;

        public int getIsVip() {
            return isVip;
        }

        public void setIsVip(int isVip) {
            this.isVip = isVip;
        }

        public ArrayList<String> getDescription() {
            return description;
        }

        public void setDescription(ArrayList<String> description) {
            this.description = description;
        }

        public ArrayList<award> getPrizeList() {
            return prizeList;
        }

        public void setPrizeList(ArrayList<award> prizeList) {
            this.prizeList = prizeList;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getOpenTime() {
            return openTime;
        }

        public void setOpenTime(String openTime) {
            this.openTime = openTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }

    public class award implements Serializable {
        private String prizeLevel;
        private String prizeName;
        private String prizeDescribe;

        public String getPrizeLevel() {
            return prizeLevel;
        }

        public void setPrizeLevel(String prizeLevel) {
            this.prizeLevel = prizeLevel;
        }

        public String getPrizeName() {
            return prizeName;
        }

        public void setPrizeName(String prizeName) {
            this.prizeName = prizeName;
        }

        public String getPrizeDescribe() {
            return prizeDescribe;
        }

        public void setPrizeDescribe(String prizeDescribe) {
            this.prizeDescribe = prizeDescribe;
        }
    }
}