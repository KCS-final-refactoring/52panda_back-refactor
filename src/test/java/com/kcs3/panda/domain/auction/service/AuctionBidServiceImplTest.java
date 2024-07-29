package com.kcs3.panda.domain.auction.service;

import com.kcs3.panda.domain.auction.dto.AuctionBidHighestDto;
import com.kcs3.panda.domain.auction.entity.AuctionProgressItem;
import com.kcs3.panda.domain.auction.repository.AuctionProgressItemRepository;
import com.kcs3.panda.domain.auction.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class AuctionBidServiceImplTest {

    @Autowired
    private AuctionBidServiceImpl auctionBidService;

    @Autowired
    private AuctionProgressItemRepository auctionProgressItemRepo;

    @Autowired
    private ItemRepository itemRepository;

    private final Long itemId = 1L;
    private final Long userId = 2L; // 입찰자 ID
    private final String nickname = "Test User";
    private final int initialBidPrice = 5000;
    private final int bidPrice = 6000;
    private final int numberOfThreads = 100; // 동시 실행할 스레드 수

    @BeforeEach
    void setUp() {
        log.info("Mock 데이터 설정");

        // Mock 데이터 설정
        AuctionProgressItem progressItem = AuctionProgressItem.builder()
                .auctionProgressItemId(itemId)
                .startPrice(initialBidPrice)
                .build();
        when(auctionProgressItemRepo.findByItemItemId(itemId)).thenReturn(Optional.of(progressItem));
        when(itemRepository.findSellerIdByItemId(itemId)).thenReturn(userId); // 판매자 ID와 입찰자 ID가 같음

        // Initial highest bid
        AuctionBidHighestDto highestBid = new AuctionBidHighestDto(itemId, 1L, "NickName", initialBidPrice);
        when(auctionProgressItemRepo.findHighestBidByAuctionProgressItemId(progressItem.getAuctionProgressItemId()))
                .thenReturn(Optional.of(highestBid));
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
                auctionProgressItemRepo, itemRepository
        );
    }

    private void attemptBidTest(Consumer<Void> action) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    action.accept(null);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // 추가적인 검증 로직을 여기에 추가
        // 예를 들어, 경매 진행 상태가 업데이트 되었는지 검증
        verify(auctionProgressItemRepo, atLeastOnce()).findHighestBidByAuctionProgressItemId(anyLong());
        verify(auctionProgressItemRepo, atLeastOnce()).save(any(AuctionProgressItem.class));
    }

    @Test
    @DisplayName("동시에 100명이 입찰 시도 : 동시성 제어 테스트")
    void bid_concurrentAccess_test() throws InterruptedException {
        attemptBidTest((_no) -> {
            try {
                auctionBidService.attemptBid(itemId, userId, nickname, bidPrice);
            } catch (Exception e) {
                log.error("입찰 중 예외 발생: ", e);
            }
        });
    }
}
