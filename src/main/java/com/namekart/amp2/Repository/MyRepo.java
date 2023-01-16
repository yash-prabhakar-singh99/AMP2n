package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.DBdetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MyRepo extends JpaRepository<DBdetails,Long> {
    DBdetails findByDomain(String domain);
    DBdetails findByAuctionId(Long auctionId);

    DBdetails findByNamecheapid(String namecheapid);

    List<DBdetails> findByFetchedTrueOrderByIdDesc();

    DBdetails findByPlatformAndDomain(String Platform, String domain);

    List<DBdetails> findByIsBidPlacedAndResult(Boolean isBidPlaced, String result);

    List<DBdetails> findByResult(String result);

    List<DBdetails> findByPlatformAndResult(String Platform,String result);

    List<DBdetails> findByResultOrResult(String result, String result1);

    List<DBdetails> findByWatchlistTrue();
}
