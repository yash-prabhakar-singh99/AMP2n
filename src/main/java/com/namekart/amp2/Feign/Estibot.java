package com.namekart.amp2.Feign;

import com.namekart.amp2.EstibotEntity.Estibot_Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "estibot", url = "https://www.estibot.com/api")
public interface Estibot {

    @GetMapping()
Estibot_Response getEstibot(@RequestParam String k,@RequestParam String a,@RequestParam String t,@RequestParam String d);
}
