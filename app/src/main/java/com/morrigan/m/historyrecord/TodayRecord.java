package com.morrigan.m.historyrecord;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fei on 2016/10/16.
 */

public class TodayRecord implements Serializable {
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    public String date = sf.format(new Date());
    public int [] records=new int[24];
    //public int[] records = new int[]{1, 379, 8, 5, 9, 5, 1, 37, 8, 5, 9, 5, 1, 37, 8, 5, 9, 5, 1, 37, 8, 5, 9, 5, 1, 37, 8, 5, 9, 5, 1, 37, 8, 5, 9, 5};
}
