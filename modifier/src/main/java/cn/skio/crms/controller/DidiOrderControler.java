package cn.skio.crms.controller;

import cn.skio.crms.service.DidiOrderService;
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
 * @create: 2019-12-20 10:54
 */
@RestController
public class DidiOrderControler {

  @Autowired
  DidiOrderService didiService;

  @PostMapping(value = "/modifyDidi",consumes = "multipart/form-data")
  public String modifyDiDi(HttpServletRequest request) throws Exception {
    //1.转换request，解析出request中的文件
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    MultipartFile file = multipartRequest.getFile("a");
    if(file.isEmpty()) {
      return "请上传文件！";
    }
    //2.解析导入文件，如合规则存入List集合
    InputStream inputStream = file.getInputStream();
    List<List<Object>> list = didiService.getDiDiOrderList(inputStream,file.getOriginalFilename());
    inputStream.close();
    //3.更新数据库
    didiService.updateDiDiOrderNum(list);
    return "success!";
  }
}
