package com.example.demo.mapper;

import com.example.demo.entity.ApplyInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 申请信息表Dao
 */
//@Repository
@Mapper
public interface ApplyInfoMapper {
    void insertApplyInfo(ApplyInfo applyInfo);
    void updateApplyInfo(Integer userId, String serialNo, String code, String updateTime);

    List<ApplyInfo> getAllApplyInfo();
}
