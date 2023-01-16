package com.namekart.amp2.Feign;


import com.namekart.amp2.DCEntity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "DropCatchFeign", url = "https://api.dropcatch.com")
public interface DropCatchFeign {

    @GetMapping("/v2/auctions/{id}")
    ResponseEntity<Object> getAuctionDetail1(@RequestHeader(value = "Authorization", required = true) String bearer, @PathVariable int id);

    @GetMapping("/v2/auctions/{id}")
    ResponseEntity<AuctionDetailDC> getAuctionDetail(@RequestHeader(value = "Authorization", required = true) String bearer, @PathVariable int id);



    @GetMapping("/v2/auctions")
    ResponseEntity<ResponseAuctionList> getAuctionDetails(@RequestHeader(value = "Authorization", required = true) String bearer, @RequestParam int size,@RequestParam boolean showAllActive, @RequestParam String Types, @RequestParam String sort);

    @GetMapping("/v2/auctions")
    ResponseEntity<ResponseAuctionList> getAuctionDetailslive(@RequestHeader(value = "Authorization", required = true) String bearer, @RequestParam int size,@RequestParam boolean showAllActive, @RequestParam String Types, @RequestParam String sort, @RequestParam(name = "EndTime.Max") String endTime, @RequestParam Boolean HasBids);
    @GetMapping("/v2/history/auctions")
    ResponseEntity<ResponseAuctionresult> getAuctionResult(@RequestHeader(value = "Authorization", required = true) String bearer, @RequestParam String searchTerm, @RequestParam int size);
    @PostMapping("/v2/bids")
    ResponseEntity<ResponsePlaceBiddc> placeBiddc(@RequestHeader(value = "Authorization", required = true) String bearer, @RequestBody List<Biddc> bids);

    @PostMapping("/authorize")
    ResponseEntity<Token> authorise(@RequestBody Authorise authorise);

}
