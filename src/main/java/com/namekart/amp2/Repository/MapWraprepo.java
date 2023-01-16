package com.namekart.amp2.Repository;

import com.namekart.amp2.DCEntity.MapWrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapWraprepo extends JpaRepository<MapWrap,Integer> {

}
