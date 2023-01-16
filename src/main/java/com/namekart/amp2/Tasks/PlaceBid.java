/* package com.namekart.amp2.Tasks;

import com.namekart.amp2.Entity.Response_AuctionDetails;
import com.namekart.amp2.Feign.MyFeignClient;
import com.namekart.amp2.Service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@org.springframework.stereotype.Service
public class PlaceBid implements Runnable{



     String domain;
private String bid;
private String key;
    @Autowired
    Service service;


Logger logger =Logger.getLogger("Yash");

public PlaceBid(String domain, String bid, String key)
{
    this.domain= domain;
    this.bid=bid;
    this.key=key;
    //this.service= new Service();
}
    @Override
    public void run() {
//myFeignClient.placeAuctionBid(key,"place_auction_bid",domain,bid,"usd");
        Response_AuctionDetails ra = service.getAuctionDetails(key,"get_auction_details",domain,"usd");
logger.info(ra.getAuction_details().get(0).getAuction_json().getDomain());
}
}
*/

