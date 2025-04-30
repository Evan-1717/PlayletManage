package com.rabbiter.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.rabbiter.util.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  推广链对象
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Data
@ApiModel(value="Promotion对象", description="")
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "推广链ID")
    @ExcelProperty("推广链ID")
    private String promotion_id;

    @ApiModelProperty(value = "推广链名")
    @ExcelProperty("推广链名")
    private String promotion_name;

    @ApiModelProperty(value = "剧场名")
    private String package_name;

    @ApiModelProperty(value = "推广链_url")
    @ExcelProperty("推广链_url")
    private String promotion_url;

    @ApiModelProperty(value = "推广链Http_url")
    @ExcelProperty("推广链Http_url")
    private String promotion_http_url;

    @ApiModelProperty(value = "分销商ID")
    @ExcelProperty("分销商ID")
    private String distributor_id;

    @ApiModelProperty(value = "wx_app_package_id")
    @ExcelProperty("wx_app_package_id")
    private String wx_app_package_id;

    @ApiModelProperty(value = "短剧ID")
    @ExcelProperty("短剧ID")
    private String playlet_id;

    @ApiModelProperty(value = "收益")
    @ExcelProperty("收益")
    private String recharge;

    @ApiModelProperty(value = "创建人")
    @ExcelProperty("创建人")
    private String creater;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty("创建时间")
    private String create_time;

    public Promotion() {

    }

    public Promotion(Map<String, Object> map) {
        this.promotion_id = map.getOrDefault("promotion_id", "").toString();
        this.promotion_name=map.getOrDefault("promotion_name", "").toString();
        this.package_name=map.getOrDefault("package_name", "").toString();
        this.promotion_url=map.getOrDefault("promotion_url", "").toString();
        this.promotion_http_url=map.getOrDefault("promotion_http_url", "").toString();
        this.distributor_id = map.getOrDefault("distributor_id", "").toString();
        this.wx_app_package_id=map.getOrDefault("wx_app_package_id", "").toString();
        this.playlet_id=map.getOrDefault("playlet_id", "").toString();
        this.recharge=map.getOrDefault("recharge", "").toString();
        this.creater=map.getOrDefault("creater", "").toString();
        this.create_time= Utils.getTime6();
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", promotion_id='" + promotion_id + '\'' +
                ", promotion_name='" + promotion_name + '\'' +
                ", package_name='" + package_name + '\'' +
                ", promotion_url=" + promotion_url +
                ", promotion_http_url=" + promotion_http_url +
                ", distributor_id=" + distributor_id +
                ", wx_app_package_id=" + wx_app_package_id +
                ", playlet_id=" + playlet_id +
                ", recharge=" + recharge +
                ", creater=" + creater +
                ", create_time=" + create_time +
                '}';
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

    public String getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(String promotion_id) {
        this.promotion_id = promotion_id;
    }

    public String getPromotion_name() {
        return promotion_name;
    }

    public void setPromotion_name(String promotion_name) {
        this.promotion_name = promotion_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPromotion_url() {
        return promotion_url;
    }

    public void setPromotion_url(String promotion_url) {
        this.promotion_url = promotion_url;
    }

    public String getPromotion_http_url() {
        return promotion_http_url;
    }

    public void setPromotion_http_url(String promotion_http_url) {
        this.promotion_http_url = promotion_http_url;
    }

    public String getDistributor_id() {
        return distributor_id;
    }

    public void setDistributor_id(String distributor_id) {
        this.distributor_id = distributor_id;
    }

    public String getWx_app_package_id() {
        return wx_app_package_id;
    }

    public void setWx_app_package_id(String wx_app_package_id) {
        this.wx_app_package_id = wx_app_package_id;
    }

    public String getPlaylet_id() {
        return playlet_id;
    }

    public void setPlaylet_id(String playlet_id) {
        this.playlet_id = playlet_id;
    }

    public String getRecharge() {
        return recharge;
    }

    public void setRecharge(String recharge) {
        this.recharge = recharge;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
