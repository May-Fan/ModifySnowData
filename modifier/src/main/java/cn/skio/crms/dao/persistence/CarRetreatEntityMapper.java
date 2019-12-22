package cn.skio.crms.dao.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface CarRetreatEntityMapper {

  String selectRetreatRecycleTime(String leaseNumber);

  String selectRetreatRemark(String leaseNumber);

  void updateCarRetreatReTime(Map<String, String> param);
}
