package com.rabbiter.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class Shouzhi implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "日期")
    @ExcelProperty("日期")
    private String date;

    @ApiModelProperty(value = "角色")
    @ExcelProperty("角色")
    private String role;

    @ApiModelProperty(value = "位置")
    private List<String> location;

    @ApiModelProperty(value = "内容")
    private List<String> content;

    @ApiModelProperty(value = "入职时间")
    private String hiredate;

    @ApiModelProperty(value = "番茄抖小消耗")
    @ExcelProperty("番茄抖小消耗")
    private float fanqiedouxiaoexpend;

    @ApiModelProperty(value = "番茄抖小充值")
    @ExcelProperty("番茄抖小充值")
    private float fanqiedouxiaorecharge;

    @ApiModelProperty(value = "番茄抖小ROI")
    @ExcelProperty("番茄抖小ROI")
    private float fanqiedouxiaoroi;

    @ApiModelProperty(value = "番茄抖小盈亏")
    @ExcelProperty("番茄抖小盈亏")
    private float fanqiedouxiaomargin;

    @ApiModelProperty(value = "番茄抖小毛利")
    @ExcelProperty("番茄抖小毛利")
    private float fanqiedouxiaoprofit;

    @ApiModelProperty(value = "番茄微小消耗")
    @ExcelProperty("番茄微小消耗")
    private float fanqieweixiaoexpend;

    @ApiModelProperty(value = "番茄微小充值")
    @ExcelProperty("番茄微小充值")
    private float fanqieweixiaorecharge;

    @ApiModelProperty(value = "番茄微小ROI")
    @ExcelProperty("番茄微小ROI")
    private float fanqieweixiaoroi;

    @ApiModelProperty(value = "番茄微小盈亏")
    @ExcelProperty("番茄微小盈亏")
    private float fanqieweixiaomargin;

    @ApiModelProperty(value = "番茄微小毛利")
    @ExcelProperty("番茄微小毛利")
    private float fanqieweixiaoprofit;

    @ApiModelProperty(value = "点众消耗")
    @ExcelProperty("点众消耗")
    private float dianzhongexpend;

    @ApiModelProperty(value = "点众充值")
    @ExcelProperty("点众充值")
    private float dianzhongrecharge;

    @ApiModelProperty(value = "点众ROI")
    @ExcelProperty("点众ROI")
    private float dianzhongroi;

    @ApiModelProperty(value = "点众盈亏")
    @ExcelProperty("点众盈亏")
    private float dianzhongmargin;

    @ApiModelProperty(value = "点众毛利")
    @ExcelProperty("点众毛利")
    private float dianzhongprofit;

    @ApiModelProperty(value = "番茄免费消耗")
    @ExcelProperty("番茄免费消耗")
    private float fanqiemianfeiexpend;

    @ApiModelProperty(value = "番茄免费充值")
    @ExcelProperty("番茄免费充值")
    private float fanqiemianfeirecharge;

    @ApiModelProperty(value = "番茄免费ROI")
    @ExcelProperty("番茄免费ROI")
    private float fanqiemianfeiroi;

    @ApiModelProperty(value = "番茄免费盈亏")
    @ExcelProperty("番茄免费盈亏")
    private float fanqiemianfeimargin;

    @ApiModelProperty(value = "番茄免费毛利")
    @ExcelProperty("番茄免费毛利")
    private float fanqiemianfeiprofit;

    @ApiModelProperty(value = "美光消耗")
    @ExcelProperty("美光消耗")
    private float meiguangexpend;

    @ApiModelProperty(value = "美光充值")
    @ExcelProperty("美光充值")
    private float meiguangrecharge;

    @ApiModelProperty(value = "美光ROI")
    @ExcelProperty("美光ROI")
    private float meiguangroi;

    @ApiModelProperty(value = "美光盈亏")
    @ExcelProperty("美光盈亏")
    private float meiguangmargin;

    @ApiModelProperty(value = "美光毛利")
    @ExcelProperty("美光毛利")
    private float meiguangprofit;

    @ApiModelProperty(value = "触摸消耗")
    @ExcelProperty("触摸消耗")
    private float chumoexpend;

    @ApiModelProperty(value = "触摸充值")
    @ExcelProperty("触摸充值")
    private float chumorecharge;

    @ApiModelProperty(value = "触摸ROI")
    @ExcelProperty("触摸ROI")
    private float chumoroi;

    @ApiModelProperty(value = "触摸盈亏")
    @ExcelProperty("触摸盈亏")
    private float chumomargin;

    @ApiModelProperty(value = "触摸毛利")
    @ExcelProperty("触摸毛利")
    private float chumoprofit;

    @ApiModelProperty(value = "总消耗")
    @ExcelProperty("总消耗")
    private float zongexpend;

    @ApiModelProperty(value = "总ROI")
    @ExcelProperty("总ROI")
    private float zongroi;

    @ApiModelProperty(value = "总盈亏")
    @ExcelProperty("总盈亏")
    private float zongmargin;


    @ApiModelProperty(value = "总毛利")
    @ExcelProperty("总毛利")
    private float zongprofit;

    @ApiModelProperty(value = "记录时间")
    @ExcelProperty("记录时间")
    private String time;

    @ApiModelProperty(value = "记录人")
    @ExcelProperty("记录人")
    private String creater;

    public Shouzhi() {
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

    public float getFanqiedouxiaoexpend() {
        return fanqiedouxiaoexpend;
    }

    public void setFanqiedouxiaoexpend(float fanqiedouxiaoexpend) {
        this.fanqiedouxiaoexpend = fanqiedouxiaoexpend;
    }

    public float getDianzhongexpend() {
        return dianzhongexpend;
    }

    public void setDianzhongexpend(float dianzhongexpend) {
        this.dianzhongexpend = dianzhongexpend;
    }

    public float getFanqieweixiaoexpend() {
        return fanqieweixiaoexpend;
    }

    public void setFanqieweixiaoexpend(float fanqieweixiaoexpend) {
        this.fanqieweixiaoexpend = fanqieweixiaoexpend;
    }

    public float getFanqiedouxiaorecharge() {
        return fanqiedouxiaorecharge;
    }

    public void setFanqiedouxiaorecharge(float fanqiedouxiaorecharge) {
        this.fanqiedouxiaorecharge = fanqiedouxiaorecharge;
    }

    public float getDianzhongrecharge() {
        return dianzhongrecharge;
    }

    public void setDianzhongrecharge(float dianzhongrecharge) {
        this.dianzhongrecharge = dianzhongrecharge;
    }

    public float getFanqieweixiaorecharge() {
        return fanqieweixiaorecharge;
    }

    public void setFanqieweixiaorecharge(float fanqieweixiaorecharge) {
        this.fanqieweixiaorecharge = fanqieweixiaorecharge;
    }

    public float getFanqiedouxiaoroi() {
        return fanqiedouxiaoroi;
    }

    public void setFanqiedouxiaoroi(float fanqiedouxiaoroi) {
        this.fanqiedouxiaoroi = fanqiedouxiaoroi;
    }

    public float getDianzhongroi() {
        return dianzhongroi;
    }

    public void setDianzhongroi(float dianzhongroi) {
        this.dianzhongroi = dianzhongroi;
    }

    public float getFanqieweixiaoroi() {
        return fanqieweixiaoroi;
    }

    public void setFanqieweixiaoroi(float fanqieweixiaoroi) {
        this.fanqieweixiaoroi = fanqieweixiaoroi;
    }

    public float getZongroi() {
        return zongroi;
    }

    public void setZongroi(float zongroi) {
        this.zongroi = zongroi;
    }

    public float getFanqiedouxiaoprofit() {
        return fanqiedouxiaoprofit;
    }

    public void setFanqiedouxiaoprofit(float fanqiedouxiaoprofit) {
        this.fanqiedouxiaoprofit = fanqiedouxiaoprofit;
    }

    public float getDianzhongprofit() {
        return dianzhongprofit;
    }

    public void setDianzhongprofit(float dianzhongprofit) {
        this.dianzhongprofit = dianzhongprofit;
    }

    public float getFanqieweixiaoprofit() {
        return fanqieweixiaoprofit;
    }

    public void setFanqieweixiaoprofit(float fanqieweixiaoprofit) {
        this.fanqieweixiaoprofit = fanqieweixiaoprofit;
    }

    public float getZongprofit() {
        return zongprofit;
    }

    public void setZongprofit(float zongprofit) {
        this.zongprofit = zongprofit;
    }

    public float getZongexpend() {
        return zongexpend;
    }

    public void setZongexpend(float zongexpend) {
        this.zongexpend = zongexpend;
    }

    public float getFanqiedouxiaomargin() {
        return fanqiedouxiaomargin;
    }

    public void setFanqiedouxiaomargin(float fanqiedouxiaomargin) {
        this.fanqiedouxiaomargin = fanqiedouxiaomargin;
    }

    public float getFanqieweixiaomargin() {
        return fanqieweixiaomargin;
    }

    public void setFanqieweixiaomargin(float fanqieweixiaomargin) {
        this.fanqieweixiaomargin = fanqieweixiaomargin;
    }

    public float getDianzhongmargin() {
        return dianzhongmargin;
    }

    public void setDianzhongmargin(float dianzhongmargin) {
        this.dianzhongmargin = dianzhongmargin;
    }

    public float getZongmargin() {
        return zongmargin;
    }

    public void setZongmargin(float zongmargin) {
        this.zongmargin = zongmargin;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String getHiredate() {
        return hiredate;
    }

    public void setHiredate(String hiredate) {
        this.hiredate = hiredate;
    }

    public float getFanqiemianfeiexpend() {
        return fanqiemianfeiexpend;
    }

    public void setFanqiemianfeiexpend(float fanqiemianfeiexpend) {
        this.fanqiemianfeiexpend = fanqiemianfeiexpend;
    }

    public float getFanqiemianfeirecharge() {
        return fanqiemianfeirecharge;
    }

    public void setFanqiemianfeirecharge(float fanqiemianfeirecharge) {
        this.fanqiemianfeirecharge = fanqiemianfeirecharge;
    }

    public float getFanqiemianfeiroi() {
        return fanqiemianfeiroi;
    }

    public void setFanqiemianfeiroi(float fanqiemianfeiroi) {
        this.fanqiemianfeiroi = fanqiemianfeiroi;
    }

    public float getFanqiemianfeimargin() {
        return fanqiemianfeimargin;
    }

    public void setFanqiemianfeimargin(float fanqiemianfeimargin) {
        this.fanqiemianfeimargin = fanqiemianfeimargin;
    }

    public float getFanqiemianfeiprofit() {
        return fanqiemianfeiprofit;
    }

    public void setFanqiemianfeiprofit(float fanqiemianfeiprofit) {
        this.fanqiemianfeiprofit = fanqiemianfeiprofit;
    }

    public float getMeiguangexpend() {
        return meiguangexpend;
    }

    public void setMeiguangexpend(float meiguangexpend) {
        this.meiguangexpend = meiguangexpend;
    }

    public float getMeiguangrecharge() {
        return meiguangrecharge;
    }

    public void setMeiguangrecharge(float meiguangrecharge) {
        this.meiguangrecharge = meiguangrecharge;
    }

    public float getMeiguangroi() {
        return meiguangroi;
    }

    public void setMeiguangroi(float meiguangroi) {
        this.meiguangroi = meiguangroi;
    }

    public float getMeiguangmargin() {
        return meiguangmargin;
    }

    public void setMeiguangmargin(float meiguangmargin) {
        this.meiguangmargin = meiguangmargin;
    }

    public float getMeiguangprofit() {
        return meiguangprofit;
    }

    public void setMeiguangprofit(float meiguangprofit) {
        this.meiguangprofit = meiguangprofit;
    }

    public float getChumoexpend() {
        return chumoexpend;
    }

    public void setChumoexpend(float chumoexpend) {
        this.chumoexpend = chumoexpend;
    }

    public float getChumorecharge() {
        return chumorecharge;
    }

    public void setChumorecharge(float chumorecharge) {
        this.chumorecharge = chumorecharge;
    }

    public float getChumoroi() {
        return chumoroi;
    }

    public void setChumoroi(float chumoroi) {
        this.chumoroi = chumoroi;
    }

    public float getChumomargin() {
        return chumomargin;
    }

    public void setChumomargin(float chumomargin) {
        this.chumomargin = chumomargin;
    }

    public float getChumoprofit() {
        return chumoprofit;
    }

    public void setChumoprofit(float chumoprofit) {
        this.chumoprofit = chumoprofit;
    }
}
