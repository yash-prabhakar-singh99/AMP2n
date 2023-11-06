package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.DBdetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MyRepo extends JpaRepository<DBdetails,Long> {
    DBdetails findByDomain(String domain);

    DBdetails findTopByDomain(String domain);

    DBdetails findTopByPlatformOrderByBidAmountDesc(String platform);
    DBdetails findTopByPlatformAndScheduledTrueOrderByBidAmountDesc(String platform);


    DBdetails findByDomainIgnoreCaseAndScheduledTrue(String domain);

    List<DBdetails> findByDomainIgnoreCaseIn(List<String> domains);
    List<DBdetails> findAllByDomain(String domain);
    List<DBdetails> findAllByEndTimeistStartsWithOrderByEndTimeistDesc(String ft);

    List<DBdetails> findAllByDomainIgnoreCase(String domain);
    DBdetails findByAuctionId(Long auctionId);

    DBdetails findByNamecheapid(String namecheapid);

    List<DBdetails> findByFetchedTrueOrderByIdDesc();

    DBdetails findByPlatformAndDomain(String Platform, String domain);
    DBdetails findByPlatformAndAuctionId(String Platform, Long AuctionId);

    DBdetails findByWatchlistTrueAndDomainIgnoreCase(String domain);
    List<DBdetails> findByIsBidPlacedAndResult(Boolean isBidPlaced, String result);

    List<DBdetails> findByScheduledTrueOrderByEndTimeistAsc();
    List<DBdetails> findByResult(String result);

    @Query(value = "select d from DBdetails d where (d.result='Won' or d.result='Loss') and (d.endTimeist> ?1 and d.endTimeist<?2) order by d.endTimeist desc")
    List<DBdetails> getResultList(String d1, String d2);

    @Query(value = "select d from DBdetails d where (d.result='Won' or d.result='Loss') and (d.endTimeist like ?1) order by d.endTimeist desc")
    List<DBdetails> getResultListbyDate(String d1);


    @Query(value = "select d from DBdetails d where (d.result='Won') and (d.endTimeist> ?1) order by d.endTimeist desc")
    List<DBdetails> getWonList(String d1);

    @Query(value = "select d from DBdetails d where (d.result='Loss') and (d.endTimeist> ?1) order by d.endTimeist desc")
    List<DBdetails> getLostList(String d1);

    @Query(value = "select d from DBdetails d where (d.result='Bid Scheduled' or d.result='Bid Placed' or d.result='Bid Placed And Scheduled' or d.result='Outbid') and (d.endTimeist> ?1 and d.endTimeist<?2) order by d.endTimeist asc")
    List<DBdetails> getScheduledList(String d1, String d2);

    List<DBdetails> findByPlatformAndResult(String Platform,String result);

    DBdetails findByPlatformAndNamecheapid(String Platform,String ncid);

    @Query(value="select  max(char_length(domain)) from dbdetails where (result='Won' or result='Loss') and (end_timeist> ?1 and end_timeist<?2)",nativeQuery = true)
    Integer findLargestResultLength(String d1, String d2);

    @Query(value="select  min(len(domain)) from DBdetails d where (d.result='Bid Scheduled' or d.result='Bid Placed' or d.result='Bid Placed And Scheduled' or d.result='Outbid') and (d.endTimeist> ?1 and d.endTimeist<?2)",nativeQuery = true)
    int findLargestScheduledLength(String d1, String d2);

    List<DBdetails> findByWatchlistFalseAndWasWatchlistedFalseAndTrackFalseAndResult(String Result);

    @Query(value = "select d from DBdetails d where d.platform='GoDaddy' and (d.result='Bid Scheduled' or d.result='Bid Placed And Scheduled' or d.result='Bid Placed' or d.result='Outbid')")
    List<DBdetails> findScheduledGD();

    @Query(value = "select d from DBdetails d where d.platform='Dynadot' and (d.result='Bid Scheduled' or d.result='Bid Placed And Scheduled' or d.result='Bid Placed' or d.result='Outbid')")
    List<DBdetails> findScheduledDD();

    @Query(value = "select d from DBdetails d where d.platform='Dropcatch' and (d.result='Bid Scheduled' or d.result='Bid Placed And Scheduled' or d.result='Bid Placed' or d.result='Outbid')")
    List<DBdetails> findScheduledDC();

    @Query(value = "select d from DBdetails d where d.platform='Namecheap' and (d.result='Bid Scheduled' or d.result='Bid Placed And Scheduled' or d.result='Bid Placed' or d.result='Outbid')")
    List<DBdetails> findScheduledNC();

    @Query(value = "select d from DBdetails d where d.platform='Namesilo' and (d.result='Bid Scheduled' or d.result='Bid Placed And Scheduled' or d.result='Bid Placed' or d.result='Outbid')")
    List<DBdetails> findScheduledNS();
    List<DBdetails> findByResultOrResult(String result, String result1);
    List<DBdetails> findByResultOrResultOrderByEndTimeistDesc(String result, String result1);

    List<DBdetails> findByPlatformAndResultOrResultOrResultOrResult(String Platform,String result, String result1, String result2, String result3);
    List<DBdetails> findByResultOrResultOrResultOrResultOrderByEndTimeist(String result, String result1, String result2, String result3);

    List<DBdetails> findByResultOrResultOrResultOrResult(String result, String result1, String result2, String result3);
    List<DBdetails> findByWatchlistTrue();

    List<DBdetails> findByWatchlistTrueAndTrackIsFalse();

    List<DBdetails> findByWatchlistTrueOrTrackTrueOrderByEndTimeist();
    List<DBdetails> findByWatchlistTrueOrderByEndTimeist();


    List<DBdetails> findByPlatformAndTrackIsTrue(String Platform);
    List<DBdetails> findByPlatformAndWatchlistIsTrue(String Platform);
    List<DBdetails> findByPlatformAndWatchlistIsTrueAndTrackIsFalse(String Platform);


}
