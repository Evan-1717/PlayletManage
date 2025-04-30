package com.rabbiter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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

    @ApiModelProperty(value = "账户ID")
    private String advertiser_id;

    @ApiModelProperty(value = "项目ID")
    private String project_id;

    @ApiModelProperty(value = "广告名")
    private String name;

    @ApiModelProperty(value = "创意标题")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> title;

    @ApiModelProperty(value = "字节小程序调起链接")
    private String mini_program_info;

    @ApiModelProperty(value = "行动号召文案")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> call_to_action_buttons;

    @ApiModelProperty(value = "落地页链接素材")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> external_url_material_list;

    @ApiModelProperty(value = "产品卖点")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> product_info_selling_points;

    @ApiModelProperty(value = "授权抖音号id")
    private String aweme_id;

    @ApiModelProperty(value = "创建时间")
    private String time;

    @ApiModelProperty(value = "创建人")
    private String creater;

    @ApiModelProperty(value = "广告ID")
    private String promotion_id;

    @Override
    public String toString() {
        return "JlPromotion{" +
                "id=" + id +
                ", advertiser_id='" + advertiser_id + '\'' +
                ", project_id='" + project_id + '\'' +
                ", name='" + name + '\'' +
                ", title=" + title +
                ", mini_program_info='" + mini_program_info + '\'' +
                ", call_to_action_buttons=" + call_to_action_buttons +
                ", external_url_material_list=" + external_url_material_list +
                ", product_info_selling_points=" + product_info_selling_points +
                ", aweme_id=" + aweme_id +
                ", time=" + time +
                ", creater=" + creater +
                ", promotion_id=" + promotion_id +
                '}';
    }

    public String getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(String promotion_id) {
        this.promotion_id = promotion_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAdvertiser_id() {
        return advertiser_id;
    }

    public void setAdvertiser_id(String advertiser_id) {
        this.advertiser_id = advertiser_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public String getMini_program_info() {
        return mini_program_info;
    }

    public void setMini_program_info(String mini_program_info) {
        this.mini_program_info = mini_program_info;
    }

    public List<String> getCall_to_action_buttons() {
        return call_to_action_buttons;
    }

    public void setCall_to_action_buttons(List<String> call_to_action_buttons) {
        this.call_to_action_buttons = call_to_action_buttons;
    }

    public List<String> getExternal_url_material_list() {
        return external_url_material_list;
    }

    public void setExternal_url_material_list(List<String> external_url_material_list) {
        this.external_url_material_list = external_url_material_list;
    }

    public List<String> getProduct_info_selling_points() {
        return product_info_selling_points;
    }

    public void setProduct_info_selling_points(List<String> product_info_selling_points) {
        this.product_info_selling_points = product_info_selling_points;
    }

    public String getAweme_id() {
        return aweme_id;
    }

    public void setAweme_id(String aweme_id) {
        this.aweme_id = aweme_id;
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
