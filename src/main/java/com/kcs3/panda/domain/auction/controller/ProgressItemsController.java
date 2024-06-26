package com.kcs3.panda.domain.auction.controller;

import com.kcs3.panda.domain.auction.dto.HotItemListDto;
import com.kcs3.panda.domain.auction.dto.ProgressItemListDto;
import com.kcs3.panda.domain.auction.service.ProgressItemsService;
import com.kcs3.panda.global.dto.ResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("api/v1/no-auth")
public class ProgressItemsController {

    private ProgressItemsService progressItemsService;



    /**
     * 경매진행중인 아이템 목록 조회 - API
     * 경매완료된 아이템 목록 조회
     * 전체 경매 아이템 목록 조회
     */
    @GetMapping("/auction")
    public ResponseDto<ProgressItemListDto> getProgressItemsApi(@PageableDefault(size = 10)Pageable pageable,
                                                                @RequestParam(required = false) String category,
                                                                @RequestParam(required = false) Integer tradingMethod,
                                                                @RequestParam(required = false) String region,
                                                                @RequestParam String status
                                                                ) {
        return ResponseDto.ok(progressItemsService.getProgressItems(category, tradingMethod, region, status, pageable));

    }


    /**
     * Redis에서 Hot Item 목록 조회 - API
     */
    @GetMapping("/hot-item")
    public ResponseDto<HotItemListDto> getHotItemsSaveApi() {
        return ResponseDto.ok(progressItemsService.getHotItems());
    }

    /**
     * Redis에서 New Item 목록 조회 - API
     */
    @GetMapping("/new-item")
    public ResponseDto<HotItemListDto> getNewItemsSaveApi() {
        return ResponseDto.ok(progressItemsService.getNewItems());
    }


    @GetMapping("/hot-save")
    public void testtHotItemsSaveApi() {
        progressItemsService.saveHotItems();
    }


    @GetMapping("/new-save")
    public void testtNewItemsSaveApi() {
        progressItemsService.saveNewItems();
    }




}
