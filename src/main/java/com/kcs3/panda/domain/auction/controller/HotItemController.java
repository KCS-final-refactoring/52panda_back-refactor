package com.kcs3.panda.domain.auction.controller;

import com.kcs3.panda.domain.auction.entity.HotItem;
import com.kcs3.panda.domain.auction.service.HotItemService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/no-auth")
public class HotItemController {

    @Autowired
    private HotItemService hotItemService;


    /**
     * RDB에서 모든 HotItem을 조회
     */
    @GetMapping("/auction/rdb/hot-items")
    public List<HotItem> getAllHotItems() {
        return hotItemService.getAllHotItems();
    }






    /**
     * Redis 캐싱 데이터로 모든 HotItem을 조회
     * 1번 저장 후 레디스에서 조회됨
     */
    @GetMapping("/auction/rdb/cache-hot-items")
    public List<HotItem> getAllCacheHotItems() {
        return hotItemService.getAllCacheHotItems();
    }





}
