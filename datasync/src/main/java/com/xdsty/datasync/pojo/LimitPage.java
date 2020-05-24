package com.xdsty.datasync.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitPage {

    /**
     * 起始下标
     */
    private Long startLimit;

    /**
     * 结束下标
     */
    private Long endLimit;

}
