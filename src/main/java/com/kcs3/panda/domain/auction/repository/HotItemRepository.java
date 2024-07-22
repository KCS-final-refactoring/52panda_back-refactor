package com.kcs3.panda.domain.auction.repository;

import com.kcs3.panda.domain.auction.entity.HotItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotItemRepository extends JpaRepository<HotItem, Long> {

    // 모든 HotItem을 조회하는 메서드
    List<HotItem> findAll();

    // ID로 특정 HotItem을 조회하는 메서드
    HotItem findByHotItemId(Long id);



}
