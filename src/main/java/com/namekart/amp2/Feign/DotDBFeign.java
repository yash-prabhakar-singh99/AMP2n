package com.namekart.amp2.Feign;

import com.namekart.amp2.DotDBEntity.DotDbResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "DotDBFeign", url = "https://api.dotdb.com/v2")
public interface DotDBFeign {

    @GetMapping("/search")
    DotDbResponse getLeads(@RequestHeader(value = "Authorization", required = true) String token, @RequestParam String keyword, @RequestParam String site_status);
}
