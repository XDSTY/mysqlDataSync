package com.xdsty.datasync.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataSyncInfo {

    /**
     * 是否开启数据同步
     */
    private String flag;

    /**
     * 每次同步条数
     */
    private Long limit;

}
