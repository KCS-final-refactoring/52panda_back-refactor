package com.kcs3.panda.domain.auction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record AuctionBidRequestDto(
        @NotNull Long itemId,
        @JsonProperty("price")
        @Positive(message = "입찰 가격은 양수이어야 합니다.")
        int bidPrice,
        @NotNull Long userId,
        @Size(min = 2, max = 10, message = "닉네임은 2자에서 10자 사이여야 합니다.")
        String nickname
) {
}
