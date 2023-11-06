package com.namekart.amp2.Repository;

import com.namekart.amp2.NamesiloEntities.SiloAuctionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Siloliverepo extends JpaRepository<SiloAuctionDetails,Long> {
    SiloAuctionDetails findByNsid(Long nsid);

    SiloAuctionDetails findById(Integer id);
    SiloAuctionDetails findByDomainIgnoreCase(String domain);
    List<SiloAuctionDetails> findByEndListTrueOrderByESTDesc();
    List<SiloAuctionDetails> findAllByOrderByESTDesc();
    List<SiloAuctionDetails> findByEndListTrueAndHighlightTrueOrderByESTDesc();
    List<SiloAuctionDetails> findByHighlightTrueOrderByESTDesc();

    List<SiloAuctionDetails> findByInitialListTrueOrderByESTDesc();
    List<SiloAuctionDetails> findByInitialListTrueAndHighlightTrueOrderByESTDesc();

}
