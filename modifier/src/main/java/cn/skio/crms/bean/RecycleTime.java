package cn.skio.crms.bean;

import lombok.Data;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-10-30 11:21
 */
@Data
public class RecycleTime {
  private String leaseNumber;
  private String recycleTimeBefore;
  private String recycleTimeAfter;
  private String remark;
}
