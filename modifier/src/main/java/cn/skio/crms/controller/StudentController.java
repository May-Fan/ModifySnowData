package cn.skio.crms.controller;

import cn.skio.crms.bean.Student;
import cn.skio.crms.bean.User;
import io.swagger.annotations.ApiOperation;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-10-29 17:10
 */
@RestController
public class StudentController {
  @Autowired
  private SqlSessionTemplate template;

  /**
   * 新增用户
   */
  @RequestMapping(value = "addUser", method = RequestMethod.POST)
  public String addUser(@RequestBody User user) {
    template.insert("addUser",user);
    return "新增用户成功！";
  }

}
