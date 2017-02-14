package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by songyongmeng on 2017/2/8.
 */

public class GetWelfareResponse implements Serializable {
    private data data;

    public data getData() {
        return data;
    }

    public void setData(data data) {
        this.data = data;
    }

    public class data implements Serializable {
        private int score;
        private int gainscore;
        private String opentime;
        private ArrayList<taskData> tasklist;
        private ArrayList<prizeData> prizeList;

        public ArrayList<prizeData> getPrizeList() {
            return prizeList;
        }

        public int getScore() {
            return score;
        }

        public int getGainscore() {
            return gainscore;
        }

        public String getOpentime() {
            return opentime;
        }

        public ArrayList<taskData> getTasklist() {
            return tasklist;
        }
    }

    public class taskData implements Serializable {
        public int id;
        public int state;
        public int task_score;
        public String task_name;
        public String process_code;
        public String task_describe;
        public String module_code;
        public String pic_url;

        public String getPic_url() {
            return pic_url;
        }

        public String getModule_code() {
            return module_code;
        }

        public int getId() {
            return id;
        }

        public int getState() {
            return state;
        }

        public int getTask_score() {
            return task_score;
        }

        public String getTask_name() {
            return task_name;
        }

        public String getProcess_code() {
            return process_code;
        }

        public String getTask_describe() {
            return task_describe;
        }
    }

    public class prizeData implements Serializable{
        public String prizeLevel;
        public String prizeName;

        public String getPrizeLevel() {
            return prizeLevel;
        }

        public String getPrizeName() {
            return prizeName;
        }
    }
}
