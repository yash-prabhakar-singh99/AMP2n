package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.LiveDetails;
import com.namekart.amp2.NamecheapEntity.Livencdb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveNcRepo extends JpaRepository<Livencdb,Integer> {
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


    Livencdb findById(String ncid);
}
