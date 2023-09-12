package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.LiveDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveRepo extends JpaRepository<LiveDetails,Long> {
List<LiveDetails> findByLiveTrueOrderByIdDesc();
    List<LiveDetails> findByInitialListTrue();
    List<LiveDetails> findByInitialListTrueOrderByESTDesc();
    List<LiveDetails> findAllByOrderByESTDesc();
    List<LiveDetails> findByInitialListTrueAndHighlightTrueOrderByESTDesc();
    List<LiveDetails> findByHighlightTrueOrderByESTDesc();

    List<LiveDetails> findByInitialListTrueAndESTIsGreaterThanOrEndListTrueAndBidsGreaterThanOrderByESTDesc(Integer EST, Integer bids);

    List<LiveDetails> findByEndListTrueAndESTIsGreaterThanOrEndListTrueAndBidsGreaterThanOrderByESTDesc(Integer EST, Integer bids);

    List<LiveDetails> findByEndListTrueOrderByESTDesc();
    List<LiveDetails> findByEndListTrueAndHighlightTrueOrderByESTDesc();


    List<LiveDetails> findByEndListTrue();



    @Query("SELECT u FROM LiveDetails u WHERE u.auction_id = ?1")
LiveDetails findByAuctionid(Long auction_id);
}
