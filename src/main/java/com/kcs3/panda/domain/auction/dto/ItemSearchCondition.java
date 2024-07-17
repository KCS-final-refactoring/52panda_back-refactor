package com.kcs3.panda.domain.auction.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ItemSearchCondition {


    private String category;
    private Integer method;
    private String region;
    private Long lastItemId;
    private int limit;

}
