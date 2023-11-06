package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.LiveDetails;
import com.namekart.amp2.NamecheapEntity.Livencdb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LiveNcRepo extends JpaRepository<Livencdb,String> {
    List<Livencdb> findByLiveTrueOrderByIddDesc();
    List<Livencdb> findByInitialListTrueOrderByEstibotValueDesc();
    List<Livencdb> findAllByOrderByEstibotValueDesc();
    List<Livencdb> findByInitialListTrueAndHighlightTrueOrderByEstibotValueDesc();

    List<Livencdb> findByHighlightTrueOrderByEstibotValueDesc();
    List<Livencdb> findByInitialListTrue();

    List<Livencdb> findByEndListTrueOrderByEstibotValueDesc();
    List<Livencdb> findByEndListTrueAndHighlightTrueOrderByEstibotValueDesc();

    List<Livencdb> findByInitialListTrueAndEstibotValueIsGreaterThanOrInitialListTrueAndBidCountGreaterThanOrderByEstibotValueDesc(Float EST, Integer bids);

    List<Livencdb> findByEndListTrueAndEstibotValueIsGreaterThanOrEndListTrueAndBidCountGreaterThanOrderByEstibotValueDesc(Float EST, Integer bids);

    List<Livencdb> findByEndListTrue();

    @Modifying
    @Transactional
    @Query("DELETE FROM Livencdb u WHERE u.id = ?1")
    void deleteByAuctionid(String id);

    //Livencdb findById(String ncid);
}
