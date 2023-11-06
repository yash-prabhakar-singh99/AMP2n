package com.namekart.amp2.Repository;

import com.namekart.amp2.SettingsEntity.FastBidSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FastSettingsRepo extends JpaRepository<FastBidSetting,String>
{

}
