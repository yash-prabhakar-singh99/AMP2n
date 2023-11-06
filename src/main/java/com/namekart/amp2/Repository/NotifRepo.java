package com.namekart.amp2.Repository;

import com.namekart.amp2.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotifRepo extends JpaRepository<Notification,Long> {
    List<Notification> findByOrderByIDDesc();

    List<Notification> findAllByDateOrderByIDDesc(Date date);
    List<Notification> findTop100ByOrderByIDDesc();
}
