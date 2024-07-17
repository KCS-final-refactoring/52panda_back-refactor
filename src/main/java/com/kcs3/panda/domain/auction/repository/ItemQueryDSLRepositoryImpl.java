package com.kcs3.panda.domain.auction.repository;

import com.kcs3.panda.domain.auction.dto.ItemSearchCondition;
import com.kcs3.panda.domain.auction.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemQueryDSLRepositoryImpl implements ItemQueryDSLRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public ItemQueryDSLRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<AuctionProgressItem> itemListSearch(ItemSearchCondition condition) {
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

        if (condition.getCategory() != null) {
            predicate = predicate.and(qCategory.category.eq(condition.getCategory()));
        }

        if (condition.getMethod() != null) {
            predicate = predicate.and(qTradingMethod.tradingMethod.eq(condition.getMethod())
                    .or(qTradingMethod.tradingMethod.eq(3)));  // 예시로 '3'을 추가 조건으로 설정
        }

        if (condition.getRegion() != null) {
            predicate = predicate.and(qRegion.region.eq(condition.getRegion()));
        }

        if (condition.getLastItemId() != null) {
            predicate = predicate.and(qItem.itemId.lt(condition.getLastItemId()));
        }

        return query.where(predicate)
                .limit(condition.getLimit())  // condition에서 limit 값을 가져와서 설정
                .fetch();
    }
}
