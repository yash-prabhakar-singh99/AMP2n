package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.GoDaddyEntities.Closeoutdb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Closeoutrepo extends JpaRepository<Closeoutdb,Long> {
    Closeoutdb findByDomain(String domain);

    List<Closeoutdb> findByStatusOrStatus(String status, String status1);
    List<Closeoutdb> findByStatusOrStatusOrderByEndTimeistDesc(String status, String status1);
    List<Closeoutdb> findByStatusOrStatusOrderByEndTimeist(String status, String status1);
    List<Closeoutdb> findByStatusOrStatusOrStatusOrderByEndTimeistDesc(String status, String status1, String status2De);
    List<Closeoutdb> findByAuctypeNot(String type);
    @Query(value = "select d from Closeoutdb d where (d.auctype='Bid') and (d.status='Closeout Scheduled'or d.status='Closeout Recheck Scheduled')")
    List<Closeoutdb> getBidList();
    List<Closeoutdb> findByAuctype(String type);
    List<Closeoutdb> findByWatchlistedTrue();

    List<Closeoutdb> findByWatchlistedTrueOrderByEndTimeist();

}
