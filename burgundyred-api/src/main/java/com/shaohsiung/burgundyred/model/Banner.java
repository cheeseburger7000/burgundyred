package com.shaohsiung.burgundyred.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 网站轮播图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banner implements Serializable {
    private String id;
    private String name;
    private String path;
    private Boolean active;
    private Date createTime;
}
