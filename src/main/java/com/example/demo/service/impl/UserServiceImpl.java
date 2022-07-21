package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ApplyGroup;
import com.example.demo.entity.ApplyInfo;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.utils.CommonUtil;
import com.example.demo.utils.EncryptUtil;
import com.example.demo.utils.IdCardUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public int insertUserBasicInfo(User user) {
        // 基本信息包括：姓名，身份证号，电话。
        /*
        1. 判断user表中是否包含该用户信息（使用身份证进行查询），如果没有，进行步骤2；
        2. 进行插入操作，判断格式正确，其中电话，身份证号要进行加密，然后存储到数据库中
         */
        String idNo = user.getIDNo();
        if(idNo.length() == 15){
            idNo = IdCardUtil.convertIdCardTo18(idNo);
        }
        int i = 0;
        User curUser = getUserBasicInfoByIDNo(user.getIDNo());
        if(EncryptUtil.encrypt(idNo) != null){
            user.setIDNo(EncryptUtil.encrypt(idNo));
        }else{
            return 0;
        }
        if(curUser != null){
            // 对其进行更新
            if(curUser.getName().equals(user.getName()) && curUser.getPhone().equals(user.getPhone())){
                //相同，则返回保存成功按钮
                return 1;
            }else{
                // 有一项是不同的
                user.setId(curUser.getId());
                try{
                    i = updateUserBasicInfoById(user);
                }catch (Exception e) {
                    logger.error("update user error!");
                    i = 0;
                }
                return i;
            }
        }else{
            user.setPhone(EncryptUtil.encrypt(user.getPhone()));
            user.setName(EncryptUtil.encrypt(user.getName()));
            try{
                i = userMapper.insertUserBasicInfo(user);
            }catch (Exception e){
                logger.error("insert user error!");
                i = 0;
            }

            return i;
        }
    }

    @Override
    public User getUserBasicInfoByIDNo(String idNo) {
        /**
         * 1. 判断是否为15位身份证，如果是则将其转为18位
         * 1. IDNo加密后，查询数据，如果存在返回，不存在返回null
         */
        if(idNo.length() == 15){
            idNo = IdCardUtil.convertIdCardTo18(idNo);
        }
        idNo = EncryptUtil.encrypt(idNo);
        User user = userMapper.getUserBasicInfoByIDNo(idNo);
        if(user != null){
            user.setIDNo(EncryptUtil.decrypt(user.getIDNo()));
            user.setPhone(EncryptUtil.decrypt(user.getPhone()));
            user.setName(EncryptUtil.decrypt(user.getName()));
        }
        return user;
    }

    @Override
    public int updateUserBasicInfoById(User user) {
        if(user.getPhone() != null && user.getPhone().length() != 0){
            user.setPhone(EncryptUtil.encrypt(user.getPhone()));
        }
        if(user.getName() != null && user.getName().length() != 0){
            user.setName(EncryptUtil.encrypt(user.getName()));
        }
        int i = 0;
        try{
            i = userMapper.updateUserBasicInfoById(user);
        }catch (Exception e) {
            logger.error("method:update user error");
        }
        return i;
    }

    @Override
    public User updateUserInfo(JSONObject info) {
        User user = new User();
        if (info == null) {
            logger.error("json info is null at updateUserInfo");
            return null;

        }
        String homeAddress = info.getString("homeAd");
        String companyAddress = info.getString("address");
        user.setHomeAddress(EncryptUtil.encrypt(homeAddress));
        user.setCompanyAddress(companyAddress);
        user.setCompanyName(info.getString("companyName"));
        user.setEducation(info.getString("education"));
        user.setIDNo(EncryptUtil.encrypt(info.getString("IDNo")));
        user.setMaritalStatus(info.getString("maritalStatus"));
        user.setWorkingYears(info.getString("workingYears"));
        user.setJob(info.getString("job"));

//        if((user.getHomeAddress()==null)||(user.getCompanyAddress()==null)||(user.getCompanyName()==null)
//                ||(user.getEducation()==null)||(user.getIDNo()==null)||(user.getIDNo().isEmpty())
//                ||(user.getMaritalStatus()==null)||(user.getWorkingYears()==null)||(user.getJob()==null)) {
//            logger.error("some required key is null at updateUserInfo");
//            return null;
//        } else if((user.getHomeAddress().trim().isEmpty())||user.getCompanyAddress().trim().isEmpty()||(user.getCompanyName().trim().isEmpty())
//                ||(user.getEducation().trim().isEmpty())||(user.getMaritalStatus().trim().isEmpty())||(user.getJob().trim().isEmpty())) {
//            logger.error("Invalid: some keys are empty at updateUserInfo");
//            return null;
//        } else if((user.getJob().length()>50)||(user.getCompanyName().length()>50)||(user.getCompanyAddress().length()>100)||
//                (user.getHomeAddress().length()>75)) {
//            logger.error("Some keys are too long at updateUserInfo");
//            return null;
//        }
//
//        if((!CommonUtil.isValidWorkTime(user.getWorkingYears()))||(!CommonUtil.isValidEducationBackground(user.getEducation()))
//                ||(!CommonUtil.isValidMarriedStatus(user.getMaritalStatus()))) {
//            logger.error("invalid input at updateUserInfo");
//            return null;
//        }

        userMapper.updateUser(user);
        user = userMapper.getAllUserInfoByID(user.getIDNo());
        return user;
    }

    @Override
    // 从数据库中获取所有用户信息，
    public List<User> ReportPrintUser(List<Integer> userId){

        List<User> user = new ArrayList<>();
        for(int i=0;i<userId.size();i++){
            user.add(userMapper.getAddedUser(userId.get(i)));
        }

        for(int i=0;i<user.size();i++){
            String name = user.get(i).getName();
            user.get(i).setName(EncryptUtil.decrypt(name));

            String IDNo = user.get(i).getIDNo();
            user.get(i).setIDNo(EncryptUtil.decrypt(IDNo));

            String phone = user.get(i).getPhone();
            user.get(i).setPhone(EncryptUtil.decrypt(phone));

            String homeAddress = user.get(i).getHomeAddress();
            user.get(i).setHomeAddress(EncryptUtil.decrypt(homeAddress));
        }

        return user;
    }

    @Override
    public List<User> ReportPrintUserNoApply(){
        List<User> user = userMapper.getUserNoApply();
        for(int i=0;i<user.size();i++){
            String name = user.get(i).getName();
            user.get(i).setName(EncryptUtil.decrypt(name));

            String IDNo = user.get(i).getIDNo();
            user.get(i).setIDNo(EncryptUtil.decrypt(IDNo));

            String phone = user.get(i).getPhone();
            user.get(i).setPhone(EncryptUtil.decrypt(phone));
        }
        return user;
    }

    @Override
    public List<List<String>> UserReportColumn(){
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>(); head0.add("编号");
        List<String> head1 = new ArrayList<>(); head1.add("姓名");
        List<String> head2 = new ArrayList<>(); head2.add("身份证号");
        List<String> head3 = new ArrayList<>(); head3.add("手机号");
        List<String> head4 = new ArrayList<>(); head4.add("公司名");
        List<String> head5 = new ArrayList<>(); head5.add("工作");
        List<String> head6 = new ArrayList<>(); head6.add("公司地址");
        List<String> head7 = new ArrayList<>(); head7.add("工作年限");
        List<String> head8 = new ArrayList<>(); head8.add("家庭住址");
        List<String> head9 = new ArrayList<>(); head9.add("教育背景");
        List<String> head10 = new ArrayList<>(); head10.add("婚姻状态");
        list.add(head0);list.add(head1);list.add(head2);list.add(head3);list.add(head4);list.add(head5);
        list.add(head6);list.add(head7);list.add(head8);list.add(head9);list.add(head10);

        return list;
    }

    @Override
    public List<List<String>> ApplyInfoReportColumn(){
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>(); head0.add("编号");
        List<String> head1 = new ArrayList<>(); head1.add("用户编号");
        List<String> head2 = new ArrayList<>(); head2.add("流水号");
        List<String> head3 = new ArrayList<>(); head3.add("申请时间");
        List<String> head4 = new ArrayList<>(); head4.add("申请状态编号");
        List<String> head5 = new ArrayList<>(); head5.add("卡号");
        List<String> head6 = new ArrayList<>(); head6.add("更新时间");
        list.add(head0);list.add(head1);list.add(head2);list.add(head3);
        list.add(head4);list.add(head5);list.add(head6);

        return list;
    }

    @Override
    public List<String> getFilename(Date date){
        List<String> fileName = new ArrayList<>();

        List<String> userDate = getStringDateShort(date);
        String path = "../"+ userDate.get(1) +"/"+userDate.get(0);
        String user = "../"+ userDate.get(1) +"/"+userDate.get(0)+"/User.xlsx";
        String applyInfo = "../"+ userDate.get(1) +"/"+userDate.get(0)+"/ApplyInfo.xlsx";
        String group = "../"+ userDate.get(1) +"/"+userDate.get(0)+"/GroupByCardNo.xlsx";
        String noApply = "../"+ userDate.get(1) +"/"+userDate.get(0)+"/UserNoApply.xlsx";
        fileName.add(user);
        fileName.add(applyInfo);
        fileName.add(group);
        fileName.add(noApply);

        File myPath = new File( path );
        if ( !myPath.exists()){//若此目录不存在，则创建之
            myPath.mkdirs();
            System.out.println("创建文件夹路径为："+ path);
        }

        return fileName;
    }

    public List<String> getStringDateShort(Date date) {
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormatter = new SimpleDateFormat("yyyy-MM");
        String dayString = dayFormatter.format(date);
        String monthString = monthFormatter.format(date);
        List<String> res = new ArrayList<>();
        res.add(dayString);res.add(monthString);

        return res;
    }

    public ApplyGroup GroupByCard(List<ApplyInfo> applyInfo){
        ApplyGroup group = new ApplyGroup();
        List<String> cardNo = new ArrayList<>();
        List<Integer> num = new ArrayList<>();
        for(int i=0;i<applyInfo.size();i++){
            String temp = applyInfo.get(i).getCardNo();
            if (!cardNo.contains(temp)){ cardNo.add(temp); num.add(1); } // 若卡号还不存在，
            else {
                for(int j=0;j< cardNo.size();j++){
                    // 若卡号已经存在，在对应位置的数上+1
                    if (cardNo.get(j).equals(temp)){ int now = num.get(j)+1;num.set(j,now); }
                }
            }
        }
        group.setCardNo(cardNo);
        group.setNum(num);
        return group;
    }

    public List<List<String>> GroupColumn(){
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>(); head0.add("CardNo");
        List<String> head1 = new ArrayList<>(); head1.add("新增申请量");
        list.add(head0);list.add(head1);

        return list;
    }
}
