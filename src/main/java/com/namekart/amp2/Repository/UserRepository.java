package com.namekart.amp2.Repository;

import com.namekart.amp2.UserEntities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    User findByTgUserId(Long tg_id);
}
