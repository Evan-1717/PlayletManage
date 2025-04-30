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
@ApiModel(value="Novel对象", description="")
public class Novel implements Serializable {

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

    @ApiModelProperty(value = "番茄消耗")
    @ExcelProperty("番茄消耗")
    private float fanqieexpend;

    @ApiModelProperty(value = "番茄充值")
    @ExcelProperty("番茄充值")
    private float fanqierecharge;

    @ApiModelProperty(value = "番茄ROI")
    @ExcelProperty("番茄ROI")
    private float fanqieroi;

    @ApiModelProperty(value = "番茄盈亏")
    @ExcelProperty("番茄盈亏")
    private float fanqiemargin;

    @ApiModelProperty(value = "番茄毛利")
    @ExcelProperty("番茄毛利")
    private float fanqieprofit;

    @ApiModelProperty(value = "黑岩消耗")
    @ExcelProperty("黑岩消耗")
    private float heiyanexpend;

    @ApiModelProperty(value = "黑岩充值")
    @ExcelProperty("黑岩充值")
    private float heiyanrecharge;

    @ApiModelProperty(value = "黑岩ROI")
    @ExcelProperty("黑岩ROI")
    private float heiyanroi;

    @ApiModelProperty(value = "黑岩盈亏")
    @ExcelProperty("黑岩盈亏")
    private float heiyanmargin;

    @ApiModelProperty(value = "黑岩毛利")
    @ExcelProperty("黑岩毛利")
    private float heiyanprofit;

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

    @ApiModelProperty(value = "七猫消耗")
    @ExcelProperty("七猫消耗")
    private float qimaoexpend;

    @ApiModelProperty(value = "七猫充值")
    @ExcelProperty("七猫充值")
    private float qimaorecharge;

    @ApiModelProperty(value = "七猫ROI")
    @ExcelProperty("七猫ROI")
    private float qimaoroi;

    @ApiModelProperty(value = "七猫盈亏")
    @ExcelProperty("七猫盈亏")
    private float qimaomargin;

    @ApiModelProperty(value = "七猫毛利")
    @ExcelProperty("七猫毛利")
    private float qimaoprofit;

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

    public Novel() {
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

    public float getFanqieexpend() {
        return fanqieexpend;
    }

    public void setFanqieexpend(float fanqieexpend) {
        this.fanqieexpend = fanqieexpend;
    }

    public float getFanqierecharge() {
        return fanqierecharge;
    }

    public void setFanqierecharge(float fanqierecharge) {
        this.fanqierecharge = fanqierecharge;
    }

    public float getFanqieroi() {
        return fanqieroi;
    }

    public void setFanqieroi(float fanqieroi) {
        this.fanqieroi = fanqieroi;
    }

    public float getFanqiemargin() {
        return fanqiemargin;
    }

    public void setFanqiemargin(float fanqiemargin) {
        this.fanqiemargin = fanqiemargin;
    }

    public float getFanqieprofit() {
        return fanqieprofit;
    }

    public void setFanqieprofit(float fanqieprofit) {
        this.fanqieprofit = fanqieprofit;
    }

    public float getHeiyanexpend() {
        return heiyanexpend;
    }

    public void setHeiyanexpend(float heiyanexpend) {
        this.heiyanexpend = heiyanexpend;
    }

    public float getHeiyanrecharge() {
        return heiyanrecharge;
    }

    public void setHeiyanrecharge(float heiyanrecharge) {
        this.heiyanrecharge = heiyanrecharge;
    }

    public float getHeiyanroi() {
        return heiyanroi;
    }

    public void setHeiyanroi(float heiyanroi) {
        this.heiyanroi = heiyanroi;
    }

    public float getHeiyanmargin() {
        return heiyanmargin;
    }

    public void setHeiyanmargin(float heiyanmargin) {
        this.heiyanmargin = heiyanmargin;
    }

    public float getHeiyanprofit() {
        return heiyanprofit;
    }

    public void setHeiyanprofit(float heiyanprofit) {
        this.heiyanprofit = heiyanprofit;
    }

    public float getDianzhongexpend() {
        return dianzhongexpend;
    }

    public void setDianzhongexpend(float dianzhongexpend) {
        this.dianzhongexpend = dianzhongexpend;
    }

    public float getDianzhongrecharge() {
        return dianzhongrecharge;
    }

    public void setDianzhongrecharge(float dianzhongrecharge) {
        this.dianzhongrecharge = dianzhongrecharge;
    }

    public float getDianzhongroi() {
        return dianzhongroi;
    }

    public void setDianzhongroi(float dianzhongroi) {
        this.dianzhongroi = dianzhongroi;
    }

    public float getDianzhongmargin() {
        return dianzhongmargin;
    }

    public void setDianzhongmargin(float dianzhongmargin) {
        this.dianzhongmargin = dianzhongmargin;
    }

    public float getDianzhongprofit() {
        return dianzhongprofit;
    }

    public void setDianzhongprofit(float dianzhongprofit) {
        this.dianzhongprofit = dianzhongprofit;
    }

    public float getQimaoexpend() {
        return qimaoexpend;
    }

    public void setQimaoexpend(float qimaoexpend) {
        this.qimaoexpend = qimaoexpend;
    }

    public float getQimaorecharge() {
        return qimaorecharge;
    }

    public void setQimaorecharge(float qimaorecharge) {
        this.qimaorecharge = qimaorecharge;
    }

    public float getQimaoroi() {
        return qimaoroi;
    }

    public void setQimaoroi(float qimaoroi) {
        this.qimaoroi = qimaoroi;
    }

    public float getQimaomargin() {
        return qimaomargin;
    }

    public void setQimaomargin(float qimaomargin) {
        this.qimaomargin = qimaomargin;
    }

    public float getQimaoprofit() {
        return qimaoprofit;
    }

    public void setQimaoprofit(float qimaoprofit) {
        this.qimaoprofit = qimaoprofit;
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
