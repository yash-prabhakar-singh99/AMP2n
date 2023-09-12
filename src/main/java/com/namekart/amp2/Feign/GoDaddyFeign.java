package com.namekart.amp2.Feign;

import com.namekart.amp2.GoDaddyEntities.DomainCheck;
import com.namekart.amp2.GoDaddyEntities.DomainsCheckResp;
import com.namekart.amp2.GoDaddyEntities.GDAppraisalResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(value = "GoDaddyFeign", url = "https://api.godaddy.com/v1")
public interface GoDaddyFeign {

@GetMapping("/appraisal/{domain}")
    GDAppraisalResp getGDV(@RequestHeader String Authorization, @PathVariable String domain);

    @PostMapping("/domains/available")
    DomainsCheckResp checkDomains(@RequestHeader String Authorization,@RequestBody List<String> domains);

    @PostMapping("/domains/available")
    DomainsCheckResp checkDomains(@RequestHeader String Authorization,@RequestBody List<String> domains, @RequestParam String checkType);

    @PostMapping("/domains/available")
    DomainsCheckResp checkDomains(@RequestHeader String Authorization,@RequestBody Set<String> domains);

    @GetMapping("/domains/available")
    DomainCheck checkDomains(@RequestHeader String Authorization, @RequestParam String domain);

    @PostMapping("/domains/available")
    DomainsCheckResp checkDomains1(@RequestHeader String Authorization, @RequestBody String domains);

}
