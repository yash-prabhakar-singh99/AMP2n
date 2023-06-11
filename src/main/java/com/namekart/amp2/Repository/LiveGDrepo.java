package com.namekart.amp2.Repository;

import com.namekart.amp2.GoDaddyEntities.Lauction;
import com.namekart.amp2.NamecheapEntity.Livencdb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveGDrepo extends JpaRepository<Lauction,Long> {
    List<Lauction> findByLiveTrueOrderByIddDesc();
    Lauction findByName(String Name);
}
