package com.namekart.amp2.Feign;

import com.namekart.amp2.Entity.Domaindetails;
import com.namekart.amp2.Entity.Sample;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "MyFeignSample", url = "https://jsonplaceholder.typicode.com/todos/1")

public interface FeignSample {
    @GetMapping()
    Sample getA();

}
