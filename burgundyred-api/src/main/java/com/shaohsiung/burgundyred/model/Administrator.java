package com.shaohsiung.burgundyred.model;

import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理员数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrator implements Serializable {

    private String id;

    private String adminName;

    private String password;

    /** 超级管理员 */
    private Boolean root;

    private Date createTime;
}
