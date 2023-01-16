package com.namekart.amp2.Repository;

import com.namekart.amp2.GoDaddyEntities.Lauction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiveGDrepo extends JpaRepository<Lauction,Long> {
}
