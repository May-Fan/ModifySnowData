package cn.skio.crms.controller;

import cn.skio.crms.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-12-03 14:40
 */
@RestController
public class ImportExcelController {

  private final ImportService importService;

  @Autowired
  public ImportExcelController(ImportService importService) {
    this.importService = importService;
  }

  /**
   * 修改租赁及退车时间
   * @param request 整个http请求头
   * @return 返回信息
   * @throws Exception none
   */
  @PostMapping(value = "/importExcel", consumes = "multipart/form-data")
  public String importExcel(HttpServletRequest request) throws Exception {
    //1.转换request，解析出request中的文件
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    MultipartFile file = multipartRequest.getFile("abc");
    if (file.isEmpty()) {
      return "文件不能为空！";
    }
    //2.解析导入文件，如合规则存入List集合
    InputStream inputStream = file.getInputStream();
    List<List<Object>> list = importService.getCarLeaseData(inputStream, file.getOriginalFilename());
    inputStream.close();
    //3.更新数据库
    importService.updateData(list);
    return "success！";
  }
}
