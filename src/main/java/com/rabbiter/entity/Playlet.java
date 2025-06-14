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
 *
 * </p>
 *
 * @author rabbiter
 * @since 2023-01-05
 */
@Data
@ApiModel(value="短剧对象", description="")
public class Playlet implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "短剧ID")
    @ExcelProperty("短剧ID")
    private String playlet_id;

    @ApiModelProperty(value = "短剧名称")
    @ExcelProperty("短剧名称")
    private String playlet_name;

    @ApiModelProperty(value = "简介")
    @ExcelProperty("简介")
    private String abstract_info;

    @ApiModelProperty(value = "作者")
    @ExcelProperty("作者")
    private String author;

    @ApiModelProperty(value = "主分类")
    @ExcelProperty("主分类")
    private String category;

    @ApiModelProperty(value = "集数")
    @ExcelProperty("集数")
    private String chapter_amount;

    @ApiModelProperty(value = "书本完结状态")
    @ExcelProperty("书本完结状态")
    private String creation_status;

    @ApiModelProperty(value = "免费章节数")
    @ExcelProperty("免费章节数")
    private String free_chapter_count;

    @ApiModelProperty(value = "体裁")
    @ExcelProperty("体裁")
    private String genre;

    @ApiModelProperty(value = "短故事类型")
    @ExcelProperty("短故事类型")
    private String length_type;

    @ApiModelProperty(value = "单章价格")
    @ExcelProperty("单章价格")
    private String price;

    @ApiModelProperty(value = "起始收费进度")
    @ExcelProperty("起始收费进度")
    private String start_percentage;

    @ApiModelProperty(value = "封面url")
    @ExcelProperty("封面url")
    private String thumb_url;

    @ApiModelProperty(value = "字数")
    @ExcelProperty("字数")
    private String word_count;

    @ApiModelProperty(value = "投放载体")
    @ExcelProperty("投放载体")
    private String carrier;

    @ApiModelProperty(value = "更新时间")
    @ExcelProperty("更新时间")
    private String update_time;

    @ApiModelProperty(value = "上线时间")
    @ExcelProperty("上线时间")
    private String release_time;

    @Override
    public String toString() {
        return "Playlet{" +
                "id=" + id +
                ", playlet_name='" + playlet_name + '\'' +
                ", playlet_id='" + playlet_id + '\'' +
                ", release_time='" + release_time + '\'' +
                ", abstract_info='" + abstract_info + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", chapter_amount='" + chapter_amount + '\'' +
                ", creation_status='" + creation_status + '\'' +
                ", free_chapter_count='" + free_chapter_count + '\'' +
                ", genre='" + genre + '\'' +
                ", length_type='" + length_type + '\'' +
                ", price='" + price + '\'' +
                ", start_percentage='" + start_percentage + '\'' +
                ", thumb_url='" + thumb_url + '\'' +
                ", word_count='" + word_count + '\'' +
                ", update_time='" + update_time + '\'' +
                ", carrier='" + carrier + '\'' +
                '}';
    }

    public Playlet() {

    }

    public Playlet(Map<String, Object> map) {
        this.playlet_id = map.getOrDefault("book_id", "").toString();
        this.playlet_name=map.getOrDefault("book_name", "").toString();
        this.release_time=Utils.getTime6();
        this.abstract_info=map.getOrDefault("abstract", "").toString();
        this.author=map.getOrDefault("author", "").toString();
        this.category=map.getOrDefault("category", "").toString();
        this.chapter_amount = map.getOrDefault("chapter_amount", "").toString();
        this.creation_status=map.getOrDefault("creation_status", "").toString();
        this.free_chapter_count=map.getOrDefault("free_chapter_count", "").toString();
        this.genre=map.getOrDefault("genre", "").toString();
        this.length_type=map.getOrDefault("length_type", "").toString();
        this.price=map.getOrDefault("price", "").toString();
        this.start_percentage=map.getOrDefault("start_percentage", "").toString();
        this.thumb_url=map.getOrDefault("thumb_url", "").toString();
        this.word_count=map.getOrDefault("word_count", "").toString();
        this.carrier=map.getOrDefault("carrier", "").toString();
        this.update_time=map.getOrDefault("update_time", "").toString();
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
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

    public String getPlaylet_name() {
        return playlet_name;
    }

    public void setPlaylet_name(String playlet_name) {
        this.playlet_name = playlet_name;
    }

    public String getPlaylet_id() {
        return playlet_id;
    }

    public void setPlaylet_id(String playlet_id) {
        this.playlet_id = playlet_id;
    }

    public String getRelease_time() {
        return release_time;
    }

    public void setRelease_time(String release_time) {
        this.release_time = release_time;
    }

    public String getAbstract_info() {
        return abstract_info;
    }

    public void setAbstract_info(String abstractInfo) {
        this.abstract_info = abstractInfo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChapter_amount() {
        return chapter_amount;
    }

    public void setChapter_amount(String chapter_amount) {
        this.chapter_amount = chapter_amount;
    }

    public String getCreation_status() {
        return creation_status;
    }

    public void setCreation_status(String creation_status) {
        this.creation_status = creation_status;
    }

    public String getFree_chapter_count() {
        return free_chapter_count;
    }

    public void setFree_chapter_count(String free_chapter_count) {
        this.free_chapter_count = free_chapter_count;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLength_type() {
        return length_type;
    }

    public void setLength_type(String length_type) {
        this.length_type = length_type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStart_percentage() {
        return start_percentage;
    }

    public void setStart_percentage(String start_percentage) {
        this.start_percentage = start_percentage;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getWord_count() {
        return word_count;
    }

    public void setWord_count(String word_count) {
        this.word_count = word_count;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
