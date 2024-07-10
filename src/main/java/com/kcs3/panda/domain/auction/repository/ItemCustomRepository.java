package com.kcs3.panda.domain.auction.repository;

import com.kcs3.panda.domain.auction.entity.AuctionProgressItem;

import java.util.List;

public interface ItemCustomRepository {

    List<AuctionProgressItem> findByProgressItemWithFilter(String category, Integer method, String region,Long lastItemId, int limit);
}
