package com.namekart.amp2.Feign;

import com.namekart.amp2.GoDaddyEntities.GDAppraisalResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "GoDaddyFeign", url = "https://api.godaddy.com/v1")
public interface GoDaddyFeign {

@GetMapping("/appraisal/{domain}")
    GDAppraisalResp getGDV(@RequestHeader String Authorization, @PathVariable String domain);

}
