package com.kcs3.panda.domain.auction.service;

import com.kcs3.panda.domain.auction.entity.HotItem;
import com.kcs3.panda.domain.auction.repository.HotItemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class HotItemService {

    @Autowired
    private HotItemRepository hotItemRepository;


    /**
     * RDB에서 모든 핫아이템 목록을 조회하는 서비스 로직
     */
    public List<HotItem> getAllHotItems() {
        return hotItemRepository.findAll();
    }


    /**
     * Redis 캐싱 데이터로 저장 모든 HotItem을 조회
     * 1번 저장 후 레디스에서 조회됨
     */
    @Cacheable(value = "hotItemsFromRDB", key = "'allItemsFromRDB'")
    public List<HotItem> getAllCacheHotItems() {
        return hotItemRepository.findAll();
    }




}
