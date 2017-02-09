package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by songyongmeng on 2017/2/8.
 */

public class GetTask implements Serializable {
    private data data;

    public data getData() {
        return data;
    }

    public void setData(data data) {
        this.data = data;
    }

    public class data implements Serializable {
        private int doscore;
        private int score;
        private int gainscore;
        private String opentime;
        private ArrayList<taskData> tasklist;

        public int getDoscore() {
            return doscore;
        }

        public void setDoscore(int doscore) {
            this.doscore = doscore;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getGainscore() {
            return gainscore;
        }

        public void setGainscore(int gainscore) {
            this.gainscore = gainscore;
        }

        public String getOpentime() {
            return opentime;
        }

        public void setOpentime(String opentime) {
            this.opentime = opentime;
        }

        public ArrayList<taskData> getTasklist() {
            return tasklist;
        }

        public void setTasklist(ArrayList<taskData> tasklist) {
            this.tasklist = tasklist;
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getTask_score() {
            return task_score;
        }

        public void setTask_score(int task_score) {
            this.task_score = task_score;
        }

        public String getTask_name() {
            return task_name;
        }

        public void setTask_name(String task_name) {
            this.task_name = task_name;
        }

        public String getProcess_code() {
            return process_code;
        }

        public void setProcess_code(String process_code) {
            this.process_code = process_code;
        }

        public String getTask_describe() {
            return task_describe;
        }

        public void setTask_describe(String task_describe) {
            this.task_describe = task_describe;
        }

        public String getModule_code() {
            return module_code;
        }

        public void setModule_code(String module_code) {
            this.module_code = module_code;
        }

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }
    }
}
