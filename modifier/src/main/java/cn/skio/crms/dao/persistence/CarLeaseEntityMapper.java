package cn.skio.crms.dao.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: ModifySnowData
 * @description: 映射xml文件中的crud语句
 * @author: May
 * @create: 2019-12-16 16:26
 */
@Mapper
@Component
public interface CarLeaseEntityMapper {
  Integer orderIsExist1(String leaseNumber);

  Integer orderIsExist2(String leaseNumber);

  String getCarNo(String leaseNumber);

  String getDriverName(String leaseNumber);

  String getDiDiOrderNumber(String leaseNumber);

  String getCarLeaseRemark1(String leaseNumber);

  String getCarLeaseRemark2(String leaseNumber);

  void updateCarLeaseReTime(Map<String, String> param);

  void updateCarLeaseStart(Map<String, String> param);

  void updateCarLeaseEnd(Map<String, String> param);

  void updateDiDiOrderNum(Map<String,String> param);
}
