package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.WasLive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WasLiveRepo extends JpaRepository<WasLive,Long> {
    List<WasLive> findByOrderByIdDesc();
}
