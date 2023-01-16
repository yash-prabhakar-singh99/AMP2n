package com.namekart.amp2.Repository;

import com.namekart.amp2.NamesiloEntities.SiloAuctionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Siloliverepo extends JpaRepository<SiloAuctionDetails,Long> {
}
