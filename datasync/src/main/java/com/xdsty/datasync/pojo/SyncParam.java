package com.xdsty.datasync.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * @author 张富华 (fuhua.zhang@ucarinc.com)
 * @date 2020/4/2 11:19
 */
@Data
@ToString
public class SyncParam {

    private String fromDbUrl;

    private String fromDbUser;

    private String fromDbPassword;

    private Integer fromDbType;

    private String toDbUrl;

    private String toDbUser;

    private String toDbPassword;

    private Integer toDbType;
}
