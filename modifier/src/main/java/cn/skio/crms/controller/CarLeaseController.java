package cn.skio.crms.controller;

import cn.skio.crms.bean.RecycleTime;
import cn.skio.crms.utils.RemarkFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-10-30 10:55
 */
@RestController
public class CarLeaseController {
  @Autowired
  private SqlSessionTemplate template;
  RemarkFactory remarkFactory = new RemarkFactory();

  /**
   * 修改租赁和退车时间
   */
  @RequestMapping(value = "/modifyLeaseTime", method = RequestMethod.POST)
  public String modifyRecycleTime(@RequestBody RecycleTime recycleTime) {
    Integer count = template.selectOne("orderIsExist", recycleTime);
    if (count.equals(0)) {
      return "该合约不存在或该合约尚未退车！";
    } else {
      //修改car_leases表：获取car_lease中原备注信息，拼凑新备注并赋值给car_leases
      String preRemark = template.selectOne("getCarLeaseRemark", recycleTime);
      template.update("modifyCarLease", remarkFactory.updateRemark(preRemark, recycleTime));

      //如果car_leases中recycle_time不为bull，则退车订单一定存在
      //先判断退车表recycle_time是否为空：空，车管还没填退车时间，不必修改，直接返回退车时间已改完
      String retreatRecycleTime = template.selectOne("getRetreatRecycleTime", recycleTime);
      if (retreatRecycleTime == null) {
        return "退车时间修改成功！";
      }
      //退车表中recycle_time不为空，修改退车表中的退车时间及备注
      else {
        String retreatRemark = template.selectOne("getRetreatRemark", recycleTime);
        remarkFactory.updateRemark(retreatRemark, recycleTime);
        template.update("modifyRetreat", recycleTime);

        //修改退款表中的退车时间
        template.update("modifyReturn", recycleTime);
        return "退车时间修改成功！";
      }
    }
  }

}
