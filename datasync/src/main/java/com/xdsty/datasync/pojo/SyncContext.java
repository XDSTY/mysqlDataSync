package com.xdsty.datasync.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 同步的上下文信息
 */
@Setter
@Getter
public class SyncContext {

    private DBInfo fromDb;

    private DBInfo destDb;

    private DataSyncInfo dataSync;

    /**
     * 同步定时器
     */
    private String corn;

    /**
     * 同步完成邮件通知
     */
    private String email;
}
