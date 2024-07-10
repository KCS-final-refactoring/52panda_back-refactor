package com.kcs3.panda.domain.auction.repository;

import com.kcs3.panda.domain.auction.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemCustomRepositoryImpl implements ItemCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public ItemCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<AuctionProgressItem> findByProgressItemWithFilter(String category, Integer method, String region,Long lastItemId, int limit) {

        QAuctionProgressItem qAuctionProgressItem = QAuctionProgressItem.auctionProgressItem;
        QItem qItem = QItem.item;
        QCategory qCategory = QCategory.category1;
        QTradingMethod qTradingMethod = QTradingMethod.tradingMethod1;
        QRegion qRegion = QRegion.region1;

        JPAQuery<AuctionProgressItem> query = jpaQueryFactory.selectFrom(qAuctionProgressItem)
                .leftJoin(qAuctionProgressItem.item, qItem).fetchJoin()
                .leftJoin(qItem.category, qCategory).fetchJoin()
                .leftJoin(qItem.tradingMethod, qTradingMethod).fetchJoin()
                .leftJoin(qItem.region, qRegion).fetchJoin()
                .orderBy(qItem.itemId.desc());

        BooleanExpression predicate = qAuctionProgressItem.isNotNull();

        if (category != null) {
            predicate = predicate.and(qCategory.category.eq(category));
        }

        if (method != null) {
            predicate = predicate.and(qTradingMethod.tradingMethod.eq(method)
                    .or(qTradingMethod.tradingMethod.eq(3)));
        }

        if (region != null) {
            predicate = predicate.and(qRegion.region.eq(region));
        }

        if (lastItemId != null) {
            predicate = predicate.and(qItem.itemId.lt(lastItemId));
        }

        return query.where(predicate)
                .limit(limit)
                .fetch();
    }
}
