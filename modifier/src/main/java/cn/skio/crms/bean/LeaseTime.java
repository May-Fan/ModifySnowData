package cn.skio.crms.bean;

import lombok.Data;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-12-02 23:06
 */
@Data
public class LeaseTime {
  private String leaseNumber;
  private String leaseStartBefore;
  private String leaseStartAfter;
  private String leaseEndBefore;
  private String leaseEndAfter;
  private String remark;

}
