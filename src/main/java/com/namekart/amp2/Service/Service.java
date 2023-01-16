package com.namekart.amp2.Service;


import com.namekart.amp2.Entity.Response_AuctionDetails;
import com.namekart.amp2.Feign.MyFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
@org.springframework.stereotype.Service
public class Service {
    @Autowired
     MyFeignClient myFeignClient;

    public Response_AuctionDetails getAuctionDetails(String key, String command, String domain, String currency)
    {
        return myFeignClient.getAuctionDetails(key,command,domain,currency);
    }
}
