package cn.skio.crms.bean;

import lombok.Data;

/**
 * @program: ModifySnowData
 * @description:
 * @author: May
 * @create: 2019-10-30 09:46
 */
@Data
public class User {
  private Integer id;
  private String username;
  private String password;
  private String email;
  private String create_time;
}
