package com.rabbiter.kaoya.entity;

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
public class KaoyaShouzhi implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "日期")
    @ExcelProperty("日期")
    private String date;

    @ApiModelProperty(value = "临潼美团1收入")
    @ExcelProperty("临潼美团1收入")
    private float linmei1recharge;

    @ApiModelProperty(value = "临潼饿了么1收入")
    @ExcelProperty("临潼饿了么1收入")
    private float line1recharge;

    @ApiModelProperty(value = "临潼美团2收入")
    @ExcelProperty("临潼美团2收入")
    private float linmei2recharge;

    @ApiModelProperty(value = "临潼饿了么2收入")
    @ExcelProperty("临潼饿了么2收入")
    private float line2recharge;

    @ApiModelProperty(value = "临潼鸭子消耗")
    @ExcelProperty("临潼鸭子消耗")
    private float linyaziexpend;

    @ApiModelProperty(value = "临潼买菜消耗")
    @ExcelProperty("临潼买菜消耗")
    private float linmaicaiexpend;

    @ApiModelProperty(value = "临潼餐具消耗")
    @ExcelProperty("临潼餐具消耗")
    private float lincanjvexpend;

    @ApiModelProperty(value = "临潼肠粉消耗")
    @ExcelProperty("临潼肠粉消耗")
    private float linchangfenexpend;

    @ApiModelProperty(value = "临潼配送费消耗")
    @ExcelProperty("临潼配送费消耗")
    private float linpeisongfeiexpend;

    @ApiModelProperty(value = "临潼其他消耗")
    @ExcelProperty("临潼其他消耗")
    private float linotherexpend;

    @ApiModelProperty(value = "临潼描述")
    @ExcelProperty("临潼详情")
    private String lindetail;

    @ApiModelProperty(value = "阎良美团收入")
    @ExcelProperty("阎良美团收入")
    private float yanmeirecharge;

    @ApiModelProperty(value = "阎良饿了么收入")
    @ExcelProperty("阎良饿了么收入")
    private float yanerecharge;

    @ApiModelProperty(value = "阎良鸭子消耗")
    @ExcelProperty("阎良鸭子消耗")
    private float yanyaziexpend;

    @ApiModelProperty(value = "阎良买菜消耗")
    @ExcelProperty("阎良买菜消耗")
    private float yanmaicaiexpend;

    @ApiModelProperty(value = "阎良餐具消耗")
    @ExcelProperty("阎良餐具消耗")
    private float yancanjvexpend;

    @ApiModelProperty(value = "阎良其他消耗")
    @ExcelProperty("阎良其他消耗")
    private float yanotherexpend;

    @ApiModelProperty(value = "阎良描述")
    @ExcelProperty("阎良描述")
    private String yandetail;

    @ApiModelProperty(value = "临潼盈利")
    @ExcelProperty("临潼盈利")
    private float linmargin;

    @ApiModelProperty(value = "阎良盈利")
    @ExcelProperty("阎良盈利")
    private float yanmargin;

    @ApiModelProperty(value = "总盈利")
    @ExcelProperty("总盈利")
    private float margin;

    @ApiModelProperty(value = "记录时间")
    @ExcelProperty("记录时间")
    private String time;

    @ApiModelProperty(value = "记录人")
    @ExcelProperty("记录人")
    private String creater;

    public KaoyaShouzhi() {
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

    public float getLinmei1recharge() {
        return linmei1recharge;
    }

    public void setLinmei1recharge(float linmei1recharge) {
        this.linmei1recharge = linmei1recharge;
    }

    public float getLine1recharge() {
        return line1recharge;
    }

    public void setLine1recharge(float line1recharge) {
        this.line1recharge = line1recharge;
    }

    public float getLinmei2recharge() {
        return linmei2recharge;
    }

    public void setLinmei2recharge(float linmei2recharge) {
        this.linmei2recharge = linmei2recharge;
    }

    public float getLine2recharge() {
        return line2recharge;
    }

    public void setLine2recharge(float line2recharge) {
        this.line2recharge = line2recharge;
    }

    public float getLinyaziexpend() {
        return linyaziexpend;
    }

    public void setLinyaziexpend(float linyaziexpend) {
        this.linyaziexpend = linyaziexpend;
    }

    public float getLinmaicaiexpend() {
        return linmaicaiexpend;
    }

    public void setLinmaicaiexpend(float linmaicaiexpend) {
        this.linmaicaiexpend = linmaicaiexpend;
    }

    public float getLincanjvexpend() {
        return lincanjvexpend;
    }

    public void setLincanjvexpend(float lincanjvexpend) {
        this.lincanjvexpend = lincanjvexpend;
    }

    public float getLinchangfenexpend() {
        return linchangfenexpend;
    }

    public void setLinchangfenexpend(float linchangfenexpend) {
        this.linchangfenexpend = linchangfenexpend;
    }

    public float getLinpeisongfeiexpend() {
        return linpeisongfeiexpend;
    }

    public void setLinpeisongfeiexpend(float linpeisongfeiexpend) {
        this.linpeisongfeiexpend = linpeisongfeiexpend;
    }

    public float getLinotherexpend() {
        return linotherexpend;
    }

    public void setLinotherexpend(float linotherexpend) {
        this.linotherexpend = linotherexpend;
    }

    public String getLindetail() {
        return lindetail;
    }

    public void setLindetail(String lindetail) {
        this.lindetail = lindetail;
    }

    public float getYanmeirecharge() {
        return yanmeirecharge;
    }

    public void setYanmeirecharge(float yanmeirecharge) {
        this.yanmeirecharge = yanmeirecharge;
    }

    public float getYanerecharge() {
        return yanerecharge;
    }

    public void setYanerecharge(float yanerecharge) {
        this.yanerecharge = yanerecharge;
    }

    public float getYanyaziexpend() {
        return yanyaziexpend;
    }

    public void setYanyaziexpend(float yanyaziexpend) {
        this.yanyaziexpend = yanyaziexpend;
    }

    public float getYanmaicaiexpend() {
        return yanmaicaiexpend;
    }

    public void setYanmaicaiexpend(float yanmaicaiexpend) {
        this.yanmaicaiexpend = yanmaicaiexpend;
    }

    public float getYancanjvexpend() {
        return yancanjvexpend;
    }

    public void setYancanjvexpend(float yancanjvexpend) {
        this.yancanjvexpend = yancanjvexpend;
    }

    public float getYanotherexpend() {
        return yanotherexpend;
    }

    public void setYanotherexpend(float yanotherexpend) {
        this.yanotherexpend = yanotherexpend;
    }

    public String getYandetail() {
        return yandetail;
    }

    public void setYandetail(String yandetail) {
        this.yandetail = yandetail;
    }

    public float getLinmargin() {
        return linmargin;
    }

    public void setLinmargin(float linmargin) {
        this.linmargin = linmargin;
    }

    public float getYanmargin() {
        return yanmargin;
    }

    public void setYanmargin(float yanmargin) {
        this.yanmargin = yanmargin;
    }

    public float getMargin() {
        return margin;
    }

    public void setMargin(float margin) {
        this.margin = margin;
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
