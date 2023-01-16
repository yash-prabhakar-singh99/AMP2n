package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.LiveDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveRepo extends JpaRepository<LiveDetails,Long> {
List<LiveDetails> findByLiveTrueOrderByIdDesc();

    @Query("SELECT u FROM LiveDetails u WHERE u.auction_id = ?1")
LiveDetails findByAuctionid(Long auction_id);
}
