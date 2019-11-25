package cn.skio.crms.utils;

import cn.skio.crms.bean.RecycleTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-11-07 13:11
 */
public class RemarkFactory {

  public static void updateRemark(String remark, RecycleTime recycleTime) {
    String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    String recycleTimeBefore = recycleTime.getRecycleTimeBefore();
    String recycleTimeAfter = recycleTime.getRecycleTimeAfter();

    if(remark.equals(null)) {
      remark = "";
    }else {
      remark = remark + "；";
    }
    remark = remark +
            date + "业务要求将退车时间从" +
            recycleTimeBefore + "修改为" +
            recycleTimeAfter;
    recycleTime.setRemark(remark);

  }

}
