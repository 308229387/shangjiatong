package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by songyongmeng on 2017/2/14.
 */

public class IntegralRecordResponse implements Serializable {
    public ArrayList<dataInfo> data;

    public ArrayList<dataInfo> getData() {
        return data;
    }

    public void setData(ArrayList<dataInfo> data) {
        this.data = data;
    }

    public class dataInfo implements Serializable{
        public long id;
        public String description;
        public String score;
        public String info_complete_time;
        public int type;

        public int getType() {
            return type;
        }

        public long getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getScore() {
            return score;
        }

        public String getInfo_complete_time() {
            return info_complete_time;
        }
    }
}
