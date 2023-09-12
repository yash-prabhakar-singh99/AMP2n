package com.namekart.amp2.Repository;

import com.namekart.amp2.APIKeySetting.APIKeySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface APIKeysRepo extends JpaRepository<APIKeySettings,Integer> {
}
