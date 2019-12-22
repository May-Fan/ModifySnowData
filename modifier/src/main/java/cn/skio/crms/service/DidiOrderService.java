package cn.skio.crms.service;

import cn.skio.crms.dao.persistence.CarLeaseEntityMapper;
import cn.skio.crms.utils.ParseExcelUtil;
import cn.skio.crms.utils.RemarkFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-12-20 10:56
 */
@Service
public class DidiOrderService {
  @Autowired
  private CarLeaseEntityMapper carLeaseEntityMapper;

  /**
   * 判断表头是否正确且对表中数据正确性校验，去除标题行后的新集合
   * @param inputStream  文件流，可以从中读取数据
   * @param fileName     文件名称
   * @return 去除标题行且筛选后的有效数据集合
   */
  public List getDiDiOrderList(InputStream inputStream, String fileName) throws Exception {
    List list = new ArrayList<>();
    List<Object> li;
    Integer count;
    String didiOrderNum;
    //1.调用工具类下的方法，判断excel版本并返回非空对象
    ParseExcelUtil parseExcelUtil = new ParseExcelUtil();
    List excelList = parseExcelUtil.getExcel(inputStream, fileName,3);
    //2.判断表头是否符合模板
    for(Object obj:excelList) {
      li = (List) obj;
      //2.1判断表头字段是否正确
      if(li.size()==4) {
        if(!li.get(0).equals("合约编号")) {
          throw new Exception("表头字段不正确！");
        }
        Object o = li.get(1);
        String s = (String)o;
        Boolean isTrue = s.equals("现有订单号");
        if(!li.get(1).equals("现有订单号")) {
          throw new Exception("表头字段不正确！");
        } if(!li.get(2).equals("修改后订单号")) {
          throw new Exception("表头字段不正确！");
        }
      }
      //3.符合表头字段后，判断合约存在
      //  滴滴订单号与sql中内容做校验
      else {
        String leaseNumber = (String)li.get(0);
        count = carLeaseEntityMapper.orderIsExist2(leaseNumber);
        if (count.equals(0)) {
//            logger.error("合约" + li.get(0)+ "不存在或未退车！");
          throw new Exception("合约" + li.get(0) + "不存在！");
        }
        didiOrderNum = carLeaseEntityMapper.getDiDiOrderNumber(leaseNumber);
        //校验excel表中的滴滴单号和sql中是否一致
        if(!li.get(1).equals(didiOrderNum)) {
          throw new Exception( leaseNumber +"合约编号与滴滴订单号不一致！");
        }
        list.add(li);
      }
    }
    return list;
  }

  /**
   * 遍历解析后的List集合，根据不同列的数据分别修改数据库
   * @param list 传入的有效数据集合
   */
  public void updateDiDiOrderNum(List list) {
    RemarkFactory remarkFactory = new RemarkFactory();
    String leaseNumber;
    String preRemark;
    String remark;
    List<Object> li;
    Map<String,String> param = new HashMap<>();
    for(Object obj:list) {
      li = (List) obj;
      leaseNumber = (String)li.get(0);
      param.put("leaseNumber",leaseNumber);
      param.put("didiOrderNum",(String)li.get(2));
      preRemark = carLeaseEntityMapper.getCarLeaseRemark2(leaseNumber);
      remark = remarkFactory.modifyRemark(preRemark,remarkFactory.getDiDiOderNumRemark((String)li.get(1),(String)li.get(2)));
      param.put("remark",remark);

      carLeaseEntityMapper.updateDiDiOrderNum(param);
    }
  }
}
