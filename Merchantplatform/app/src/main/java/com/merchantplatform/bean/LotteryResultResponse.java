package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by MengZhiYuan on 2017/2/10.
 */

public class LotteryResultResponse implements Serializable {

    private data data;

    public LotteryResultResponse.data getData() {
        return data;
    }

    public void setData(LotteryResultResponse.data data) {
        this.data = data;
    }

    public class data implements Serializable {
        private int prizeDrawState;
        private String msg;
        private String prizeName;
        private int score;
        private String openTime;
        private String endTime;

        public int getPrizeDrawState() {
            return prizeDrawState;
        }

        public void setPrizeDrawState(int prizeDrawState) {
            this.prizeDrawState = prizeDrawState;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getPrizeName() {
            return prizeName;
        }

        public void setPrizeName(String prizeName) {
            this.prizeName = prizeName;
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
}
