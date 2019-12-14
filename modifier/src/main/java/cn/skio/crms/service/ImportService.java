package cn.skio.crms.service;

import cn.skio.crms.utils.RemarkFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Date;
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
  private SqlSessionTemplate template_1;
  /**
   * 解析excel文件
   * @param inputStream 文件流，可以从中读取数据
   * @param fileName 文件名称
   * @return
   */
  public List getExcelData(InputStream inputStream,String fileName) throws Exception{
    List list = new ArrayList<>();
    //调用同类下的方法，判断excel版本并返回
    Workbook work = this.getWorkbook(inputStream,fileName);
    if(work == null) {
      throw new Exception("excel内容不能为空！");
    }
    Sheet sheet = null;
    Row row = null;
    Cell cell = null;
    String str = null;
    Double dbl = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    Integer count = null;
    String driverName = null;
    String carNo = null;

    for(int i=0;i<work.getNumberOfSheets();i++) {
      sheet = work.getSheetAt(i);
      if(sheet == null) {
        continue;
      }
      for(int j=sheet.getFirstRowNum();j<sheet.getLastRowNum()+1;j++) {
        row = sheet.getRow(j);
        if(row == null) {
          continue;
        }
        List<Object> li = new ArrayList<>();

        if(j==sheet.getFirstRowNum()) {
          int rowLen = row.getLastCellNum();
          if(rowLen!=5) {
            throw new Exception("表头列数不正确！");
          }
          for(int x=row.getFirstCellNum();x<row.getLastCellNum();x++) {
            cell = row.getCell(x);
            li.add(cell.getStringCellValue());
          }
          if(!li.get(0).equals("合约编号")) {
            throw new Exception("表头字段不正确！");
          }else if(!li.get(1).equals("车牌")) {
            throw new Exception("表头字段不正确！");
          }else if(!li.get(2).equals("司机姓名")) {
            throw new Exception("表头字段不正确！");
          }else if(!li.get(3).equals("错误退车日期")) {
            throw new Exception("表头字段不正确！");
          }else if(!li.get(4).equals("正确退车日期")) {
            throw new Exception("表头字段不正确！");
          }
        }
        else {
          for(int y=row.getFirstCellNum();y<row.getLastCellNum();y++) {
            cell = row.getCell(y);
            if((cell.getCellType() == CellType.STRING)||(cell.getCellType() ==CellType.BLANK)) {
              str = cell.getStringCellValue();
              li.add(str);
            }
            else if(cell.getCellType()== CellType.NUMERIC) {
              str = sdf.format(cell.getDateCellValue());
//             li.add(sdf.parse(str));
              li.add(str);
            }
        }
          count = template_1.selectOne("orderIsExist1",li.get(0));
          if(count.equals(0)) {
            throw new Exception("合约"+str+"不存在或未退车！");
          }
          carNo = template_1.selectOne("getCarNo",li.get(0));
          driverName = template_1.selectOne("getDriverName",li.get(0));
          if((!li.get(1).equals(carNo)) ||(!li.get(2).equals(driverName))) {
            throw new Exception("合约编号与司机、车牌不一致！");
          }
          list.add(li);
        }
      }
    }
    work.close();
    return list;
  }

  /**
   * 根据后缀名判断excel版本，使用不同的工具类
   * @param inStr 工作流
   * @param fileName 文件名
   * @return
   */
  public Workbook getWorkbook (InputStream inStr,String fileName) throws Exception {
    String fileType = fileName.substring(fileName.lastIndexOf("."));
    Workbook workbook = null;
    if(fileType.equals(".xls") ) {
      workbook = new HSSFWorkbook(inStr);
    } else if (fileType.equals(".xlsx")) {
      workbook = new XSSFWorkbook(inStr);
    }
    else {
      throw new Exception("后缀名错误，请上传excel格式的文件！");
    }
    return workbook;
  }

  /**
   * 根据解析excel表中的单元格是否为空，判断需要调用哪一个方法
   * @param list
   */
  public void updateData(List list) {
    List<Object> li = null;
    for(Object obj : list) {
      li = (List) obj;
      //正确退车日期列不为空，修改退车日期
      if(li.get(4)!=null) {
        this.updateRecycleTime(li);
      }
    }
  }

  /**
   *根据获取到的List集合，修改退车时间
   */
  public void updateRecycleTime(List<Object> li) {
    RemarkFactory remarkFactory = new RemarkFactory();
    String preRemark = null;
    String remark = null;
      preRemark = template_1.selectOne("getCarLeaseRemark1",li.get(0));
      if(preRemark == null) {
        preRemark = "";
      }
      remark = remarkFactory.updateCarLeaseRemark(preRemark,(String) li.get(3),(String) li.get(4));
      Map<String,Object> param = new HashMap<>();
      param.put("leaseNumber",li.get(0));
      param.put("recycleTime",li.get(4));
      param.put("remark",remark);
      template_1.update("updateCarLeaseReTime",param);
  }
}
