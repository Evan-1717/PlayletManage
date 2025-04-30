package com.rabbiter.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Data
@ApiModel(value="Storage对象", description="")
public class AdvertiserCost implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "日期")
    @ExcelProperty("日期")
    private String date;

    @ApiModelProperty(value = "更新日期")
    @ExcelProperty("更新日期")
    private String update_date;

    @ApiModelProperty(value = "advertiser_id")
    @ExcelProperty("advertiser_id")
    private String advertiser_id;

    @ApiModelProperty(value = "media_advertiser_nick")
    @ExcelProperty("media_advertiser_nick")
    private String media_advertiser_nick;

    @ApiModelProperty(value = "media_account_id")
    @ExcelProperty("media_account_id")
    private String media_account_id;

    @ApiModelProperty(value = "当日消耗")
    @ExcelProperty("当日消耗")
    private String fund_cost;

    @ApiModelProperty(value = "当日充值")
    @ExcelProperty("当日充值")
    private String fund_recharge;

    @ApiModelProperty(value = "daily_cost")
    @ExcelProperty("daily_cost")
    private List<String> daily_cost;

    @ApiModelProperty(value = "daily_recharge")
    @ExcelProperty("daily_recharge")
    private List<String> daily_recharge;

    @ApiModelProperty(value = "总消耗")
    @ExcelProperty("总消耗")
    private String stat_cost;

    @ApiModelProperty(value = "pay_amount_roi")
    @ExcelProperty("pay_amount_roi")
    private String pay_amount_roi;

    @ApiModelProperty(value = "stat_pay_amount")
    @ExcelProperty("stat_pay_amount")
    private String stat_pay_amount;

    @ApiModelProperty(value = "media_account_name")
    @ExcelProperty("media_account_name")
    private String media_account_name;

    @ApiModelProperty(value = "media_source")
    @ExcelProperty("media_source")
    private String media_source;

    @ApiModelProperty(value = "creater")
    @ExcelProperty("creater")
    private String creater;

    @Override
    public String toString() {
        return "AdvertiserCost{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", update_date='" + update_date + '\'' +
                ", advertiser_id='" + advertiser_id + '\'' +
                ", media_advertiser_nick='" + media_advertiser_nick + '\'' +
                ", media_account_id='" + media_account_id + '\'' +
                ", fund_cost='" + fund_cost + '\'' +
                ", fund_recharge='" + fund_recharge + '\'' +
                ", daily_cost=" + daily_cost +
                ", daily_recharge=" + daily_recharge +
                ", stat_cost='" + stat_cost + '\'' +
                ", pay_amount_roi='" + pay_amount_roi + '\'' +
                ", stat_pay_amount='" + stat_pay_amount + '\'' +
                ", media_account_name='" + media_account_name + '\'' +
                ", media_source='" + media_source + '\'' +
                ", creater='" + creater + '\'' +
                '}';
    }

    public String getFund_recharge() {
        return fund_recharge;
    }

    public void setFund_recharge(String fund_recharge) {
        this.fund_recharge = fund_recharge;
    }

    public List<String> getDaily_recharge() {
        return daily_recharge;
    }

    public void setDaily_recharge(List<String> daily_recharge) {
        this.daily_recharge = daily_recharge;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAdvertiser_id() {
        return advertiser_id;
    }

    public void setAdvertiser_id(String advertiser_id) {
        this.advertiser_id = advertiser_id;
    }

    public String getMedia_advertiser_nick() {
        return media_advertiser_nick;
    }

    public void setMedia_advertiser_nick(String media_advertiser_nick) {
        this.media_advertiser_nick = media_advertiser_nick;
    }

    public String getMedia_account_id() {
        return media_account_id;
    }

    public void setMedia_account_id(String media_account_id) {
        this.media_account_id = media_account_id;
    }

    public String getFund_cost() {
        return fund_cost;
    }

    public void setFund_cost(String fund_cost) {
        this.fund_cost = fund_cost;
    }

    public List<String> getDaily_cost() {
        return daily_cost;
    }

    public void setDaily_cost(List<String> daily_cost) {
        this.daily_cost = daily_cost;
    }

    public String getStat_cost() {
        return stat_cost;
    }

    public void setStat_cost(String stat_cost) {
        this.stat_cost = stat_cost;
    }

    public String getPay_amount_roi() {
        return pay_amount_roi;
    }

    public void setPay_amount_roi(String pay_amount_roi) {
        this.pay_amount_roi = pay_amount_roi;
    }

    public String getStat_pay_amount() {
        return stat_pay_amount;
    }

    public void setStat_pay_amount(String stat_pay_amount) {
        this.stat_pay_amount = stat_pay_amount;
    }

    public String getMedia_account_name() {
        return media_account_name;
    }

    public void setMedia_account_name(String media_account_name) {
        this.media_account_name = media_account_name;
    }

    public String getMedia_source() {
        return media_source;
    }

    public void setMedia_source(String media_source) {
        this.media_source = media_source;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }
}
