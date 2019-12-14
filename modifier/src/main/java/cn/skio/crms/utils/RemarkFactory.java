package cn.skio.crms.utils;
import cn.skio.crms.bean.RecycleTime;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @program: ModifySnowData
 * @description: 生成备注
 * @author: May
 * @create: 2019-11-07 13:11
 */
public class RemarkFactory {

  String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

  public RecycleTime updateRemark(String remark, RecycleTime recycleTime) {
    String recycleTimeBefore = recycleTime.getRecycleTimeBefore();
    String recycleTimeAfter = recycleTime.getRecycleTimeAfter();

    if(remark==null) {
      remark = "";
    }else {
      remark = remark + "；";
    }
    remark = remark +
            date + "业务要求将退车时间从" +
            recycleTimeBefore + "修改为" +
            recycleTimeAfter;
    recycleTime.setRemark(remark);
    return recycleTime;
  }
  /**
   * 在数据库获取到的remark基础上，加上日期及修改备注
   * @param remark 数据库获取到的原remark
   * @return 修改后的新remark
   */
  public String updateCarLeaseRemark(String remark,String beforeRecycleTime,String afterRecycleTime) {
    if(remark.equals("")) {
      remark = "";
    }else {
      remark = remark + ";";
    }
    remark = remark +
            date + "业务要求将退车时间从" +
            beforeRecycleTime + "修改为" +
            afterRecycleTime;
    return remark;
  }
}
