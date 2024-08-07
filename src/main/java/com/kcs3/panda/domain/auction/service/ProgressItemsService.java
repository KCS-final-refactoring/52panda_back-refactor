package com.kcs3.panda.domain.auction.service;


import com.kcs3.panda.domain.auction.dto.*;
import com.kcs3.panda.domain.auction.entity.AuctionCompleteItem;
import com.kcs3.panda.domain.auction.entity.AuctionInfo;
import com.kcs3.panda.domain.auction.entity.AuctionProgressItem;
import com.kcs3.panda.domain.auction.entity.Item;
import com.kcs3.panda.domain.auction.repository.AuctionInfoRepository;
import com.kcs3.panda.domain.auction.repository.ItemQueryDSLRepository;
import com.kcs3.panda.domain.auction.repository.ItemRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class ProgressItemsService {

    private final ItemRepository itemRepository;
    private final AuctionInfoRepository auctionInfoRepository;


    private final RedisTemplate<String, HotItemsDto> redisTemplate;

    /**
     * 경매진행중인 아이템 목록 조회 - 서비스 로직
     * 경매완료된 아이템 목록 조회 - 서비스 로직
     * 모든 아이템 목록 조회 - 서비스 로직
     */

    public ProgressItemListDto getProgressItems(String category, Integer method, String region, String status, Pageable pageable) {
        List<ProgressItemsDto> itemtemDtoList = new ArrayList<>();

        if ("progress".equals(status)) {
            Slice<AuctionProgressItem> progressItems = itemRepository.findByProgressItemWithLocationAndMethodAndRegion(category, method, region, pageable);
            for (AuctionProgressItem progressItem : progressItems) {
                ProgressItemsDto progressItemsDto = ProgressItemsDto.fromProgressEntity(progressItem);
                itemtemDtoList.add(progressItemsDto);
            }
        } else if ("completion".equals(status)) {
            Slice<AuctionCompleteItem> completionItems = itemRepository.findByCompleteItemWithLocationAndMethodAndRegion(category, method, region, pageable);
            for (AuctionCompleteItem completionItem : completionItems) {
                ProgressItemsDto progressItemsDto = ProgressItemsDto.fromCompletionEntity(completionItem);
                itemtemDtoList.add(progressItemsDto);
            }
        } else {
            Slice<AuctionProgressItem> allItems = itemRepository.findByProgressItemWithLocationAndMethodAndRegion(category, method, region, pageable);
            for (AuctionProgressItem progressItems : allItems) {
                ProgressItemsDto progressItemsDto = ProgressItemsDto.fromProgressEntity(progressItems);
                itemtemDtoList.add(progressItemsDto);
            }

            Slice<AuctionCompleteItem> completionItems = itemRepository.findByCompleteItemWithLocationAndMethodAndRegion(category, method, region, pageable);
            for (AuctionCompleteItem completionItem : completionItems) {
                ProgressItemsDto progressItemsDto = ProgressItemsDto.fromCompletionEntity(completionItem);
                itemtemDtoList.add(progressItemsDto);
            }




        } //else

        return ProgressItemListDto.builder()
                .progressItemListDto(itemtemDtoList)
                .build();
    }



    /**
     * no offset 조회 서비스
     */
    public ProgressItemListDto getProgressItemsNoOffset(String category, Integer method, String region, Long lastItemId) {
        List<ProgressItemsDto> itemDtoList = new ArrayList<>();

        ItemSearchCondition condition = ItemSearchCondition.builder()
                .category(category)
                .method(method)
                .region(region)
                .lastItemId(lastItemId)
                .limit(10)
                .build();

        List<AuctionProgressItem> progressItems = itemRepository.itemListSearch(condition);

        for (AuctionProgressItem progressItem : progressItems) {
            ProgressItemsDto progressItemsDto = ProgressItemsDto.fromProgressEntity(progressItem);
            itemDtoList.add(progressItemsDto);
        }

        return ProgressItemListDto.builder()
                .progressItemListDto(itemDtoList)
                .build();
    }



    /**
     * Hot 아이템 목록 Redis 조회 서비스 로직
     */
    public HotItemListDto getHotItems(){

        List<HotItemsDto> hotItemsDtos = new ArrayList<>();

        for (int i=1;i<=10;i++) {
            hotItemsDtos.add( redisTemplate.opsForValue().get("hot_item:"+i));
        }

        return HotItemListDto.builder()
                .hotItemListDtos(hotItemsDtos)
                .build();
    }

    /**
     *  New 아이템 목록 Redis 조회 서비스 로직
     */
    public HotItemListDto getNewItems(){

        List<HotItemsDto> newItemsDtos = new ArrayList<>();

        for (int i=1;i<=10;i++) {
            newItemsDtos.add( redisTemplate.opsForValue().get("new_item:"+i));
        }

        return HotItemListDto.builder()
                .hotItemListDtos(newItemsDtos)
                .build();
    }



    /**
     * 핫아이템 Redis 저장 서비스 로직
     * refactor : RDB에서 조회한 데이터를 Redis에 다이렉트 저장한다.
     */
    @Cacheable(value = "hotItems", key = "'hotItemList'")
    public HotItemListDto getHotItemList() {

        // 최근 인기 아이템의 itemId 리스트 조회
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> hotItemIdList = auctionInfoRepository.findTop10ItemIds(pageable);
        List<AuctionProgressItem> hotItemList = new ArrayList<>();


        for (Long itemId : hotItemIdList) {
            AuctionProgressItem hotItem = itemRepository.findByHotItemList(itemId);
            hotItemList.add(hotItem);
        }

        // 조회된 AuctionProgressItem을 NewItemsDto로 변환
        List<HotItemsDto> hotItemsDtos = hotItemList
                .stream()
                .map(HotItemsDto::fromHotEntity)
                .collect(Collectors.toList());


        return HotItemListDto.builder()
                .hotItemListDtos(hotItemsDtos)
                .build();
    }



    /**
     * 신규 아이템 Redis 저장 서비스 로직
     */
    public void saveNewItems() {

        // 신규 아이템의 itemId 리스트 조회
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> newItemIdList = auctionInfoRepository.findNew10ItemIds(pageable);
        List<AuctionProgressItem> newItemList = new ArrayList<>();


        for (Long itemId : newItemIdList) {
            AuctionProgressItem newItem = itemRepository.findByHotItemList(itemId);
            newItemList.add(newItem);
        }

        // 조회된 AuctionProgressItem을 NewItemsDto로 변환
        List<HotItemsDto> hotItemsDtos = newItemList
                .stream()
                .map(HotItemsDto::fromHotEntity)
                .collect(Collectors.toList());


        int i = 1;
        for (HotItemsDto hotItemsDto : hotItemsDtos) {
            redisTemplate.opsForValue().set("new_item:" + i, hotItemsDto);
            i++;
        }



    }


}

