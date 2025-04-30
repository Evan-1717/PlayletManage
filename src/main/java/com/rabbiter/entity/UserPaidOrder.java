package com.rabbiter.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;

public class UserPaidOrder {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "是否是充值活动")
    private String activity;

    @ApiModelProperty(value = "公众号/快应用/小程序id")
    private String app_id;

    @ApiModelProperty(value = "公众号/快应用/小程序名称")
    private String app_name;

    @ApiModelProperty(value = "染色推广链书籍类型")
    private String book_category;

    @ApiModelProperty(value = "染色推广链书籍性别")
    private String book_gender;

    @ApiModelProperty(value = "染色推广链书籍Id")
    private String book_id;

    @ApiModelProperty(value = "染色推广链书籍名称")
    private String book_name;

    @ApiModelProperty(value = "脱敏后的用户设备ID")
    private String device_id;

    @ApiModelProperty(value = "distributor_id")
    private String distributor_id;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "用户openid")
    private String open_id;

    @ApiModelProperty(value = "第三方订单ID")
    private String out_trade_no;

    @ApiModelProperty(value = "付费金额，单位分")
    private String pay_amount;

    @ApiModelProperty(value = "付费时间")
    private String pay_timestamp;

    @ApiModelProperty(value = "推广链id")
    private String promotion_id;

    @ApiModelProperty(value = "推广链名称")
    private String promotion_name;

    @ApiModelProperty(value = "用户染色时间戳")
    private String register_time;

    @ApiModelProperty(value = "脱敏后的订单ID")
    private String trade_no;

    @ApiModelProperty(value = "未脱敏的订单ID（未加密）")
    private String trade_no_raw;

    @ApiModelProperty(value = "用户在微信/抖音开放平台下的唯一id")
    private String union_id;

    @ApiModelProperty(value = "用户点击推广链时的UA")
    private String user_agent;

    @Override
    public String toString() {
        return "UserPaidOrder{" +
                "id=" + id +
                ", activity='" + activity + '\'' +
                ", app_id='" + app_id + '\'' +
                ", app_name='" + app_name + '\'' +
                ", book_category='" + book_category + '\'' +
                ", book_gender='" + book_gender + '\'' +
                ", book_id='" + book_id + '\'' +
                ", book_name='" + book_name + '\'' +
                ", device_id='" + device_id + '\'' +
                ", distributor_id='" + distributor_id + '\'' +
                ", ip='" + ip + '\'' +
                ", open_id='" + open_id + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", pay_amount='" + pay_amount + '\'' +
                ", pay_timestamp='" + pay_timestamp + '\'' +
                ", promotion_id='" + promotion_id + '\'' +
                ", promotion_name='" + promotion_name + '\'' +
                ", register_time='" + register_time + '\'' +
                ", trade_no='" + trade_no + '\'' +
                ", trade_no_raw='" + trade_no_raw + '\'' +
                ", union_id='" + union_id + '\'' +
                ", user_agent='" + user_agent + '\'' +
                '}';
    }

    public String getPromotion_name() {
        return promotion_name;
    }

    public void setPromotion_name(String promotion_name) {
        this.promotion_name = promotion_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getBook_category() {
        return book_category;
    }

    public void setBook_category(String book_category) {
        this.book_category = book_category;
    }

    public String getBook_gender() {
        return book_gender;
    }

    public void setBook_gender(String book_gender) {
        this.book_gender = book_gender;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDistributor_id() {
        return distributor_id;
    }

    public void setDistributor_id(String distributor_id) {
        this.distributor_id = distributor_id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(String pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getPay_timestamp() {
        return pay_timestamp;
    }

    public void setPay_timestamp(String pay_timestamp) {
        this.pay_timestamp = pay_timestamp;
    }

    public String getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(String promotion_id) {
        this.promotion_id = promotion_id;
    }

    public String getRegister_time() {
        return register_time;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getTrade_no_raw() {
        return trade_no_raw;
    }

    public void setTrade_no_raw(String trade_no_raw) {
        this.trade_no_raw = trade_no_raw;
    }

    public String getUnion_id() {
        return union_id;
    }

    public void setUnion_id(String union_id) {
        this.union_id = union_id;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }
}
