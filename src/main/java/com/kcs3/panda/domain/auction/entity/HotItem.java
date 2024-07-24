package com.kcs3.panda.domain.auction.entity;


import com.kcs3.panda.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "HotItem")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
public class HotItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long hotItemId;

    @Column(nullable = false)
    private String itemTitle;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private int startPrice;

    @Column(nullable = false)
    private Integer buyNowPrice;



}
