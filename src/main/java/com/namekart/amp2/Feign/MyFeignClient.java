package com.namekart.amp2.Feign;


import com.namekart.amp2.Entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;

@FeignClient(value = "MyFeignClient", url = "https://api.dynadot.com/api3.json")
public interface MyFeignClient {

    @GetMapping()
    Response_AuctionDetails getAuctionDetails(@RequestParam String key,@RequestParam String command,@RequestParam String domain, @RequestParam String currency);

    @GetMapping()
    ResponseClosedAuction getClosedAuctionDetails(@RequestParam String key,@RequestParam String command,@RequestParam String startDate,@RequestParam String endDate, @RequestParam String currency);

    @GetMapping()
    String getClosedAuctionDetailstr(@RequestParam String key,@RequestParam String command,@RequestParam String startDate,@RequestParam String endDate, @RequestParam String currency);

    @GetMapping()
    ResponseLive getLiveDetails(@RequestParam String key,@RequestParam String command, @RequestParam String currency, @RequestParam String type);



    @GetMapping()
    String getAuctionDetailstr(@RequestParam String key,@RequestParam String command,@RequestParam String domain, @RequestParam String currency);

    @GetMapping()
    Error1 getAuctionError(@RequestParam String key, @RequestParam String command, @RequestParam String domain, @RequestParam String bid, @RequestParam String currency);

    @GetMapping()
    Error2 getAuctionError2(@RequestParam String key, @RequestParam String command, @RequestParam String domain, @RequestParam String currency);
    @GetMapping()
    LinkedHashMap getAuctionDetail(@RequestParam String key, @RequestParam String command, @RequestParam String domain, @RequestParam String currency);

    @GetMapping()
    Object placeAuctionBid(@RequestParam String key,@RequestParam String command,@RequestParam String domain,@RequestParam String bid_amount, @RequestParam String currency);

    @GetMapping()
    Response_PlaceBid placeAuctionBids(@RequestParam String key, @RequestParam String command, @RequestParam String domain, @RequestParam String bid_amount, @RequestParam String currency);

    @GetMapping()
    String placeAuctionBidstr(@RequestParam String key,@RequestParam String command,@RequestParam String domain,@RequestParam String bid_amount, @RequestParam String currency);


}
