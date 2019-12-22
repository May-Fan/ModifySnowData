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

    if (remark == null) {
      remark = "";
    } else {
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
   *
   * @param remark 数据库获取到的原remark
   * @return 修改后的新remark
   */
  public String modifyRemark(String preRemark, String remark) {
    if ((preRemark == null)||preRemark.equals("")) {
      preRemark = "";
    } else {
      preRemark = preRemark + ";";
    }
    remark = preRemark + remark;
    return remark;
  }

  public String getRecycleRemark(String recycleTimeBefore, String recycleTimeAfter) {
    return date + "业务要求将退车时间从" +
            recycleTimeBefore + "修改为" +
            recycleTimeAfter;
  }

  public String getLeaseStartRemark(String leaseStartBefore, String leaseStartAfter) {
    return date + "业务要求将租赁起始从" +
            leaseStartBefore + "修改为" +
            leaseStartAfter;
  }

  public String getLeaseEndRemark(String leaseEndBefore, String leaseEndAfter) {
    return date + "业务要求将租赁结束从" +
            leaseEndBefore + "修改为" +
            leaseEndAfter;
  }

  public String getDiDiOderNumRemark(String didiOrderNumBefore,String didiOrderNumAfter) {
    return date + "业务要求将滴滴订单号从" +
            didiOrderNumBefore + "修改为" +
            didiOrderNumAfter;
  }
}
