package com.namekart.amp2.Repository;

import com.namekart.amp2.UserEntities.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action,Long> {

}
