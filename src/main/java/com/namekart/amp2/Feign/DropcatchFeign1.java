package com.namekart.amp2.Feign;

import com.namekart.amp2.DCEntity.Authorise;
import com.namekart.amp2.DCEntity.Token;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "DropCatchFeign1", url = "https://api.dropcatch.com")
public interface DropcatchFeign1 {
    @PostMapping("/authorize")
    ResponseEntity<Token> authorise(@RequestBody Authorise authorise);
}
