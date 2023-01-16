package com.namekart.amp2.Repository;

import com.namekart.amp2.NamecheapEntity.Livencdb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveNcRepo extends JpaRepository<Livencdb,Integer> {
    List<Livencdb> findByLiveTrueOrderByIddDesc();
}