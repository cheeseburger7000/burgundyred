package com.shaohsiung.burgundyred.model;

import com.shaohsiung.burgundyred.enums.UserState;
import lombok.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户数据模型
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private String id;

    private String userName;

    private String password;

    /** 头像 */
    private String avatar;

    private String email;

    private String mobile;

    private UserState state;

    private Date createTime;

    private Date updateTime;
}
