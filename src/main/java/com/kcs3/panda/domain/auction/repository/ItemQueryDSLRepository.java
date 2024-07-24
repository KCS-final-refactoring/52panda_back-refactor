package com.kcs3.panda.domain.auction.repository;

import com.kcs3.panda.domain.auction.dto.ItemSearchCondition;
import com.kcs3.panda.domain.auction.entity.AuctionProgressItem;

import java.util.List;

public interface ItemQueryDSLRepository
{
    List<AuctionProgressItem> itemListSearch(ItemSearchCondition condition);
}
