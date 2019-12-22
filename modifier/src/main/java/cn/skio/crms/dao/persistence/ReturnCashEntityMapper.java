package cn.skio.crms.dao.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Mapper
@Component
public interface ReturnCashEntityMapper {
  void updateReturnCashReTime(Map<String, String> param);
}
