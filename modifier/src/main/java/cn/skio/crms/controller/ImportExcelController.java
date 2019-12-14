package cn.skio.crms.controller;

import cn.skio.crms.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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
@Controller
public class ImportExcelController {

  @Autowired
  private ImportService importService;

  @RequestMapping(value = "/importExcel",method = RequestMethod.POST)
  @ResponseBody
  public String importExcel(HttpServletRequest request) throws Exception {
    // 转换request，解析出request中的文件
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

    MultipartFile file = multipartRequest.getFile("abc");
    if(file.isEmpty()) {
      return "文件不能为空！";
    }
    InputStream inputStream = file.getInputStream();
    List<List<Object>> list = importService.getExcelData(inputStream,file.getOriginalFilename());
    inputStream.close();

    importService.updateData(list);
    return "success！";
  }
}
