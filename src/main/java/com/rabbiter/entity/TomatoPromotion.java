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
@ApiModel(value="Tencent对象", description="")
public class TomatoPromotion implements Serializable {

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

    @ApiModelProperty(value = "番茄付费消耗")
    @ExcelProperty("番茄付费消耗")
    private float fanqiefufeiexpend;

    @ApiModelProperty(value = "番茄付费充值")
    @ExcelProperty("番茄付费充值")
    private float fanqiefufeirecharge;

    @ApiModelProperty(value = "番茄付费ROI")
    @ExcelProperty("番茄付费ROI")
    private float fanqiefufeiroi;

    @ApiModelProperty(value = "番茄付费盈亏")
    @ExcelProperty("番茄付费盈亏")
    private float fanqiefufeimargin;

    @ApiModelProperty(value = "番茄付费毛利")
    @ExcelProperty("番茄付费毛利")
    private float fanqiefufeiprofit;

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

    public TomatoPromotion() {
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

    public float getfanqiemianfeiexpend() {
        return fanqiemianfeiexpend;
    }

    public void setfanqiemianfeiexpend(float fanqiemianfeiexpend) {
        this.fanqiemianfeiexpend = fanqiemianfeiexpend;
    }

    public float getfanqiemianfeirecharge() {
        return fanqiemianfeirecharge;
    }

    public void setfanqiemianfeirecharge(float fanqiemianfeirecharge) {
        this.fanqiemianfeirecharge = fanqiemianfeirecharge;
    }

    public float getfanqiemianfeiroi() {
        return fanqiemianfeiroi;
    }

    public void setfanqiemianfeiroi(float fanqiemianfeiroi) {
        this.fanqiemianfeiroi = fanqiemianfeiroi;
    }

    public float getfanqiemianfeimargin() {
        return fanqiemianfeimargin;
    }

    public void setfanqiemianfeimargin(float fanqiemianfeimargin) {
        this.fanqiemianfeimargin = fanqiemianfeimargin;
    }

    public float getfanqiemianfeiprofit() {
        return fanqiemianfeiprofit;
    }

    public void setfanqiemianfeiprofit(float fanqiemianfeiprofit) {
        this.fanqiemianfeiprofit = fanqiemianfeiprofit;
    }

    public float getfanqiefufeiexpend() {
        return fanqiefufeiexpend;
    }

    public void setfanqiefufeiexpend(float fanqiefufeiexpend) {
        this.fanqiefufeiexpend = fanqiefufeiexpend;
    }

    public float getfanqiefufeirecharge() {
        return fanqiefufeirecharge;
    }

    public void setfanqiefufeirecharge(float fanqiefufeirecharge) {
        this.fanqiefufeirecharge = fanqiefufeirecharge;
    }

    public float getfanqiefufeiroi() {
        return fanqiefufeiroi;
    }

    public void setfanqiefufeiroi(float fanqiefufeiroi) {
        this.fanqiefufeiroi = fanqiefufeiroi;
    }

    public float getfanqiefufeimargin() {
        return fanqiefufeimargin;
    }

    public void setfanqiefufeimargin(float fanqiefufeimargin) {
        this.fanqiefufeimargin = fanqiefufeimargin;
    }

    public float getfanqiefufeiprofit() {
        return fanqiefufeiprofit;
    }

    public void setfanqiefufeiprofit(float fanqiefufeiprofit) {
        this.fanqiefufeiprofit = fanqiefufeiprofit;
    }

    public float getZongexpend() {
        return zongexpend;
    }

    public void setZongexpend(float zongexpend) {
        this.zongexpend = zongexpend;
    }

    public float getZongroi() {
        return zongroi;
    }

    public void setZongroi(float zongroi) {
        this.zongroi = zongroi;
    }

    public float getZongmargin() {
        return zongmargin;
    }

    public void setZongmargin(float zongmargin) {
        this.zongmargin = zongmargin;
    }

    public float getZongprofit() {
        return zongprofit;
    }

    public void setZongprofit(float zongprofit) {
        this.zongprofit = zongprofit;
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
}
