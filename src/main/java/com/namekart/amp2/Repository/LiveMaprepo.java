package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.LiveMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveMaprepo extends JpaRepository<LiveMap,Integer> {

}
