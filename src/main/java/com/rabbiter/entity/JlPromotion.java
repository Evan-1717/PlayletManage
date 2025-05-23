package com.rabbiter.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.rabbiter.util.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户实体类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@Data
@ApiModel(value="Jl广告", description="")
@TableName(value = "jlpromotion", autoResultMap = true)
public class JlPromotion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "推广链ID")
    @ExcelProperty("推广链ID")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> promotion_id_info;

    @ApiModelProperty(value = "推广链名")
    @ExcelProperty("推广链名")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> promotion_name_info;

    @ApiModelProperty(value = "分销商ID")
    @ExcelProperty("分销商ID")
    private String distributor_id;

    @ApiModelProperty(value = "账户信息")
    @ExcelProperty("账户信息")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> advertiser_id_info;

    @ApiModelProperty(value = "短剧ID")
    @ExcelProperty("短剧ID")
    private String book_id;

    @ApiModelProperty(value = "短剧名")
    @ExcelProperty("短剧名")
    private String book_name;

    @ApiModelProperty(value = "创建人")
    @ExcelProperty("创建人")
    private String creater;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty("创建时间")
    private String create_time;

    @ApiModelProperty(value = "主体")
    @ExcelProperty("主体")
    private String subject;

    @ApiModelProperty(value = "任务状态")
    @ExcelProperty("任务状态")
    private String status;

    @ApiModelProperty(value = "错误信息")
    @ExcelProperty("错误信息")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> error_info;

    @ApiModelProperty(value = "项目信息")
    @ExcelProperty("项目信息")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> project_info;

    @ApiModelProperty(value = "广告信息")
    @ExcelProperty("广告信息")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> advertising_info;

    @ApiModelProperty(value = "项目个数")
    @ExcelProperty("项目个数")
    private String project_num;

    @ApiModelProperty(value = "广告个数")
    @ExcelProperty("广告个数")
    private String advertising_num;

    @ApiModelProperty(value = "账户个数")
    @ExcelProperty("账户个数")
    private String advertiser_num;

    public JlPromotion() {

    }

    public JlPromotion(Map<String, Object> map) {
        this.distributor_id = map.getOrDefault("distributor_id", "").toString();
        this.creater=map.getOrDefault("creater", "").toString();
        this.book_name=map.getOrDefault("book_name", "").toString();
        this.subject=map.getOrDefault("jlaccount", "").toString();
        this.book_id=map.getOrDefault("video_id", "").toString();
        this.create_time= Utils.getTime9();
        int advertiser_ids = ((List<String>)map.get("advertiser_ids")).size();
        int project_number = Integer.parseInt(map.get("project_number").toString());
        int jlpromotion_number = Integer.parseInt(map.get("jlpromotion_number").toString());
        this.project_num = "" + advertiser_ids * project_number;
        this.advertising_num = "" + advertiser_ids * project_number * jlpromotion_number;
        this.advertiser_num = "" + advertiser_ids;
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

    public List<String> getPromotion_id_info() {
        return promotion_id_info;
    }

    public void setPromotion_id_info(List<String> promotion_id_info) {
        this.promotion_id_info = promotion_id_info;
    }

    public List<String> getPromotion_name_info() {
        return promotion_name_info;
    }

    public void setPromotion_name_info(List<String> promotion_name_info) {
        this.promotion_name_info = promotion_name_info;
    }

    public String getDistributor_id() {
        return distributor_id;
    }

    public void setDistributor_id(String distributor_id) {
        this.distributor_id = distributor_id;
    }

    public List<String> getAdvertiser_id_info() {
        return advertiser_id_info;
    }

    public void setAdvertiser_id_info(List<String> advertiser_id_info) {
        this.advertiser_id_info = advertiser_id_info;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getError_info() {
        return error_info;
    }

    public void setError_info(List<String> error_info) {
        this.error_info = error_info;
    }

    public List<String> getProject_info() {
        return project_info;
    }

    public void setProject_info(List<String> project_info) {
        this.project_info = project_info;
    }

    public List<String> getAdvertising_info() {
        return advertising_info;
    }

    public void setAdvertising_info(List<String> advertising_info) {
        this.advertising_info = advertising_info;
    }

    public String getProject_num() {
        return project_num;
    }

    public void setProject_num(String project_num) {
        this.project_num = project_num;
    }

    public String getAdvertising_num() {
        return advertising_num;
    }

    public void setAdvertising_num(String advertising_num) {
        this.advertising_num = advertising_num;
    }
}
