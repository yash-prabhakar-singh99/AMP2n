package com.namekart.amp2.Repository;

import com.namekart.amp2.DCEntity.AuctionDetailDC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveDCrepo extends JpaRepository<AuctionDetailDC,Long> {
    List<AuctionDetailDC> findByOrderByIdDesc();
}
