package cn.skio.crms.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: ModifySnowData
 * @description: 解析excel工具类
 * @author: May
 * @create: 2019-12-20 15:06
 */
public class ParseExcelUtil {

  public List getExcel(InputStream inputStream, String fileName, int titleLen) throws Exception {
    Workbook workbook = null;
    Sheet sheet;
    Row row;
    Cell cell;
    String str;
    int rowLen;
    List list = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //1.调用同类下的方法，判断excel版本并返回非空对象
    try {
      workbook = this.getWorkbook(inputStream,fileName);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //2.解析excel单元格中的内容，添加至List集合
    int k = workbook.getNumberOfSheets();
    for(int i=0;i<workbook.getNumberOfSheets();i++) {
      sheet = workbook.getSheetAt(i);
      if (sheet == null) {
        continue;
      }
      int l =sheet.getLastRowNum();
      for (int j = sheet.getFirstRowNum(); j < sheet.getLastRowNum() + 1; j++) {
        row = sheet.getRow(j);
        if (row == null) {
          continue;
        }
        if(j== sheet.getFirstRowNum()) {
          rowLen = row.getLastCellNum();
          //2.1判断表头列数是否正确
          if(rowLen != titleLen) {
            throw new Exception("表头列数不正确！");
          }
        }
        List<Object> li = new ArrayList<>();
        for (int x = row.getFirstCellNum(); x < row.getLastCellNum(); x++) {
          cell = row.getCell(x);
          if ((cell.getCellType() == CellType.STRING) || (cell.getCellType() == CellType.BLANK)) {
            str = cell.getStringCellValue();
            if(str.equals("")) {
              str =null;
            }
            li.add(str);
          } else if (cell.getCellType() == CellType.NUMERIC) {
            str = sdf.format(cell.getDateCellValue());
            if(str == "") {
              str =null;
            }
            li.add(str);
          }
        }
        //2.2标记标题行
        if(j == sheet.getFirstRowNum()) {
          li.add("标题行");
        }
        list.add(li);
      }
    }
    workbook.close();
    return list;
  }

  /**
   * 根据后缀名判断excel版本，使用不同的工具类
   * @param inStr    工作流
   * @param fileName 文件名
   * @return 返回解析后的工作表
   * @throws Exception 抛出文件格式错误异常
   */
  public Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
    String fileType = fileName.substring(fileName.lastIndexOf("."));
    Workbook workbook;
    if (fileType.equals(".xls")) {
      workbook = new HSSFWorkbook(inStr);
    } else if (fileType.equals(".xlsx")) {
      workbook = new XSSFWorkbook(inStr);
    } else {
//      logger.error("文件后缀名错误！");
      throw new Exception("后缀名错误，请上传excel格式的文件！");
    }
    if (workbook == null) {
      throw new Exception("excel内容不能为空！");
    }
    return workbook;
  }

}
