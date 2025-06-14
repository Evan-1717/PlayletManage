package com.rabbiter.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 用户实体类
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-02
 */
@ApiModel(value="User对象", description="")
@TableName(value = "user", autoResultMap = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("id")
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ExcelProperty("账号")
    @ApiModelProperty(value = "账号")
    private String no;

    @ExcelProperty("名字")
    @ApiModelProperty(value = "名字")
    private String name;

    @ExcelProperty("密码")
    @ApiModelProperty(value = "密码")
    private String password;

    @ExcelProperty("年龄")
    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ExcelProperty("性别")
    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ExcelProperty("电话")
    @ApiModelProperty(value = "电话")
    private String phone;

    @ExcelProperty("角色")
    @ApiModelProperty(value = "角色")
    private String role;

    @ExcelProperty("账号Id")
    @ApiModelProperty(value = "角色 0超级管理员，1管理员，2普通账号")
    private Integer roleId;

    @ExcelProperty("位置")
    @ApiModelProperty(value = "位置")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> location;

    @ExcelProperty("内容")
    @ApiModelProperty(value = "内容")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> content;

    @ExcelProperty("分销商")
    @ApiModelProperty(value = "分销商")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> jlaccount;

    @ExcelProperty("入职时间")
    @ApiModelProperty(value = "入职时间")
    private String hiredate;

    @ApiModelProperty(value = "是否有效，Y有效，其他无效")
    @ExcelProperty("isValid")
    private String isvalid;

    @ExcelProperty("分销商")
    @ApiModelProperty(value = "批量投放权限")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> batch_permission;

    public List<String> getBatch_permission() {
        return batch_permission;
    }

    public void setBatch_permission(List<String> batch_permission) {
        this.batch_permission = batch_permission;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(String isvalid) {
        this.isvalid = isvalid;
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

    public List<String> getJlaccount() {
        return jlaccount;
    }

    public void setJlaccount(List<String> jlaccount) {
        this.jlaccount = jlaccount;
    }
}
