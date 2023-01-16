package com.namekart.amp2.Feign;

import com.namekart.amp2.NamesiloEntities.SiloRespAucList;
import feign.form.ContentType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "Namesilofeign", url = "https://www.namesilo.com")
public interface NamesiloFeign {

    @GetMapping(value = "/public/api/listAuctions")
    SiloRespAucList getList(@RequestParam int version, @RequestParam String type, @RequestParam String key);

}
