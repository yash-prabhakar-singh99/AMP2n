package com.namekart.amp2.Feign;

import com.namekart.amp2.NamecheapEntity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "Namecheapfeign", url = "https://aftermarketapi.namecheap.com/client/api")
public interface Namecheapfeign {
    static final String bearer = "Bearer ef7b03f63d8a94e2f083b991a74dd5852s5DuDtyOc9Ft1QZ5u0plxLpA0vlYdHFxEccAez6lh/wUyQNkOTCfqcOgrYMcvG4";


    @GetMapping("/sales")
    ResponseAuctionDetailsNC getAuctionDetails(@RequestHeader(value = "Authorization", required = true) String bearer, @RequestParam String name);

    @GetMapping("/sales")
    ResponseLivedb getAuctionDetailslive(@RequestHeader(value = "Authorization", required = true) String bearer, @RequestParam String bidCount, @RequestParam String endDate, @RequestParam String orderBy);

    @GetMapping("/sales")
    ResponseLivedb getAuctionDetailslive1(@RequestHeader(value = "Authorization", required = true) String bearer, @RequestParam String bidCount, @RequestParam String endDate, @RequestParam String orderBy, @RequestParam int page);

       @PostMapping("/sales/{id}/bids")
    ResponsePlaceBidNc placeBidnc(@RequestHeader(value = "Authorization", required = true,defaultValue = "Bearer ef7b03f63d8a94e2f083b991a74dd5852s5DuDtyOc9Ft1QZ5u0plxLpA0vlYdHFxEccAez6lh/wUyQNkOTCfqcOgrYMcvG4") String bearer, @PathVariable String id, @RequestBody Bidnc bid);

    @GetMapping("/sales/{id}")
    AuctionDetailNC getAuctionDetailbyId(@RequestHeader(value = "Authorization", required = true,defaultValue = "Bearer ef7b03f63d8a94e2f083b991a74dd5852s5DuDtyOc9Ft1QZ5u0plxLpA0vlYdHFxEccAez6lh/wUyQNkOTCfqcOgrYMcvG4") String bearer, @PathVariable String id);

}
