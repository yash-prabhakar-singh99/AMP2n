package com.namekart.amp2.Repository;

import com.namekart.amp2.DCEntity.AuctionDetailDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LiveDCrepo extends JpaRepository<AuctionDetailDC,Long> {
    List<AuctionDetailDC> findByOrderByIdDesc();

    List<AuctionDetailDC> findByInitialListTrue();
    List<AuctionDetailDC> findByEndListTrue();

    List<AuctionDetailDC> findByInitialListTrueOrderByESTDesc();
    List<AuctionDetailDC> findAllByOrderByESTDesc();
    List<AuctionDetailDC> findByInitialListTrueAndHighlightTrueOrderByESTDesc();

    List<AuctionDetailDC> findByHighlightTrueOrderByESTDesc();
    List<AuctionDetailDC> findByEndListTrueAndHighlightTrueOrderByESTDesc();
    List<AuctionDetailDC> findByEndListTrueOrderByESTDesc();

    AuctionDetailDC findByAuctionId(Long auctionId);
    AuctionDetailDC findByNameIgnoreCase(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM AuctionDetailDC u WHERE u.auctionId = ?1")
    void deleteByAuctionid(Long auctionId);

}
