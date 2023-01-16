package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.DB_Bid_Details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Bidhisrepo extends JpaRepository<DB_Bid_Details,Long> {

}
