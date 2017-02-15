package com.merchantplatform.bean;

import java.io.Serializable;

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
        private int score;

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

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

    }
}