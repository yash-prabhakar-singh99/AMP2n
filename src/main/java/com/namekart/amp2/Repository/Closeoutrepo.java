package com.namekart.amp2.Repository;

import com.namekart.amp2.GoDaddyEntities.Closeoutdb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Closeoutrepo extends JpaRepository<Closeoutdb,Long> {
    Closeoutdb findByDomain(String domain);

    List<Closeoutdb> findByStatusOrStatus(String status, String status1);
    List<Closeoutdb> findByStatusOrStatusOrderByEndTimeistDesc(String status, String status1);
    List<Closeoutdb> findByStatusOrStatusOrderByEndTimeist(String status, String status1);
    List<Closeoutdb> findByStatusOrStatusOrStatusOrderByEndTimeistDesc(String status, String status1, String status2De);



    List<Closeoutdb> findByWatchlistedTrue();

    List<Closeoutdb> findByWatchlistedTrueOrderByEndTimeist();

}
