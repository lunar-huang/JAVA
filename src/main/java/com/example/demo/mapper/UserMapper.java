package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//@Repository
@Mapper
public interface UserMapper {

    // 插入部分用户信息
    int insertUserBasicInfo(User user);

    User getUserBasicInfoByIDNo(String IDNo);
    //
    int updateUserBasicInfoById(User user);

    void updateUser(User user);

    User getAllUserInfoByID(String IDNo);

    List<User> getAllUser();

    User getAddedUser(Integer id);

    List<User> getUserNoApply();
}
