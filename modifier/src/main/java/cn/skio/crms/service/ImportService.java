package cn.skio.crms.service;

import cn.skio.crms.dao.persistence.CarLeaseEntityMapper;
import cn.skio.crms.dao.persistence.CarRetreatEntityMapper;
import cn.skio.crms.dao.persistence.ReturnCashEntityMapper;
import cn.skio.crms.utils.ParseExcelUtil;
import cn.skio.crms.utils.RemarkFactory;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ModifySnowData
 * @description: 先调用同一类下的方法判断excel文件格式，格式正确则进行解析，解析后判断数据是否正常
 * @author: May
 * @create: 2019-12-06 11:07
 */
@Service
public class ImportService {
  @Autowired
  private CarLeaseEntityMapper carLeaseEntityMapper;

  @Autowired
  private CarRetreatEntityMapper carRetreatEntityMapper;

  @Autowired
  private ReturnCashEntityMapper returnCashEntityMapper;

  /**
   * 判断表头是否正确且对表中数据正确性校验，去除标题行后的新集合
   * @param inputStream 文件流，可以从中读取数据
   * @param fileName    文件名称
   * @return 去除标题行且筛选后的有效数据集合
   */
  public List getCarLeaseData(InputStream inputStream, String fileName) throws Exception {
    List list = new ArrayList<>();
    List<Object> li;
    Integer count;
    String driverName;
    String carNo;
    //1.调用工具类下的方法，判断excel版本并返回非空对象
    ParseExcelUtil parseExcelUtil = new ParseExcelUtil();
    List excelList = parseExcelUtil.getExcel(inputStream, fileName,9);
    //2.判断表头字段是否符合模板
    for(Object obj:excelList) {
      li = (List) obj;
      if(li.size()==10) {
        if (!li.get(0).equals("合约编号")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：合约编号");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(1).equals("车牌")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：车牌");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(2).equals("司机姓名")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：司机姓名");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(3).equals("错误租赁起始时间")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：错误租赁起始时间");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(4).equals("正确租赁起始时间")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：正确租赁起始时间");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(5).equals("错误租赁结束时间")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：错误租赁结束时间");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(6).equals("正确租赁结束时间")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：正确租赁结束时间");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(7).equals("错误退车日期")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：错误退车日期");
          throw new Exception("表头字段不正确！");
        } else if (!li.get(8).equals("正确退车日期")) {
//        logger.error("第"+i+"个sheet表头不正确！错误表头：正确退车日期");
          throw new Exception("表头字段不正确！");
        }
      }
      //3.符合表头字段后，判断合约存在且已退车
      //  合约编号、姓名、车牌号与sql中内容做校验
      else {
        String leaseNumber = (String)li.get(0);
        count = carLeaseEntityMapper.orderIsExist1(leaseNumber);
        if (count.equals(0)) {
//            logger.error("合约" + li.get(0)+ "不存在或未退车！");
          throw new Exception("合约" + leaseNumber + "不存在或未退车！");
        }
        carNo = carLeaseEntityMapper.getCarNo(leaseNumber);
        driverName = carLeaseEntityMapper.getDriverName(leaseNumber);
        //校验excel中的司机车辆信息和sql表中是否一致
        if ((!li.get(1).equals(carNo)) || (!li.get(2).equals(driverName))) {
//            logger.error(li.get(0) +"合约编号与司机、车牌不一致！");
          throw new Exception( leaseNumber +"合约编号与司机、车牌不一致！");
        }
        list.add(li);
      }
    }
    return list;
  }

  /**
   * 遍历解析后的List集合，根据不同列的数据分别修改数据库
   * @param list 传入解析后的List集合
   */
  public void updateData(List list) {
    RemarkFactory remarkFactory = new RemarkFactory();
    String leaseNumber;
    String preRemark;
    String leaseStartRemark;
    String leaseEndRemark;
    String recycleRemark;
    String remark;
    List<Object> li;
    Map<String, String> param = new HashMap<>();

    for (Object obj : list) {
      li = (List) obj;
      leaseNumber = (String) li.get(0);
      param.put("leaseNumber", leaseNumber);
      //1.正确退车时间列不为空，修改退车时间
      if(li.get(8) != null) {
        preRemark = carLeaseEntityMapper.getCarLeaseRemark1(leaseNumber);
        recycleRemark = remarkFactory.getRecycleRemark((String) li.get(7), (String) li.get(8));
        remark = remarkFactory.modifyRemark(preRemark, recycleRemark);
        param.put("recycleTime", (String) li.get(8));
        param.put("carLeaseRecycleRemark", remark);
        carLeaseEntityMapper.updateCarLeaseReTime(param);

        //判断car_retreat_order中的退车时间，如不为空则进行修改
        if (carRetreatEntityMapper.selectRetreatRecycleTime(leaseNumber) != null) {
          preRemark = carRetreatEntityMapper.selectRetreatRemark(leaseNumber);
          remark = remarkFactory.modifyRemark(preRemark, recycleRemark);
          param.put("retreatRemark", remark);
          carRetreatEntityMapper.updateCarRetreatReTime(param);
          returnCashEntityMapper.updateReturnCashReTime(param);
//          logger.info("合约编号："+leaseNumber+"修改退车时间成功！");
        }
      }
      //2.正确租赁起始列不为空，修改租赁起始时间
      if (li.get(4) != null) {
        preRemark = carLeaseEntityMapper.getCarLeaseRemark1(leaseNumber);
        leaseStartRemark = remarkFactory.getLeaseStartRemark((String) li.get(3), (String) li.get(4));
        remark = remarkFactory.modifyRemark(preRemark, leaseStartRemark);

        param.put("leaseStart", (String) li.get(4));
        param.put("carLeaseStartRemark", remark);
        carLeaseEntityMapper.updateCarLeaseStart(param);
//        logger.info("合约编号："+leaseNumber+"修改租赁起始成功！");
      }
      //3.正确租赁结束列不为空，修改租赁结束时间
      if (li.get(6) != null) {
        preRemark = carLeaseEntityMapper.getCarLeaseRemark1(leaseNumber);
        leaseEndRemark = remarkFactory.getLeaseEndRemark((String) li.get(5), (String) li.get(6));
        remark = remarkFactory.modifyRemark(preRemark, leaseEndRemark);

        param.put("leaseEnd", (String) li.get(6));
        param.put("carLeaseEndRemark", remark);
        carLeaseEntityMapper.updateCarLeaseEnd(param);
//        logger.info("合约编号："+leaseNumber+"修改租赁结束成功！");
      }
    }
  }
}
