package com.namekart.amp2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class GoDaddySoapConfig {
    @Bean(name = "marshaller")
    public Jaxb2Marshaller marshaller()  {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.namekart.amp2.stub");
        return marshaller;
    }

    @Bean(name = "marshaller1")
    public Jaxb2Marshaller marshaller1()  {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.namekart.amp2.stub1");
        return marshaller;
    }
    @Bean
    public GoDaddySoapClient soapConnector(Jaxb2Marshaller marshaller) {
        GoDaddySoapClient client = new GoDaddySoapClient();
        client.setDefaultUri("https://auctions.godaddy.com/gdAuctionsWSAPI/gdAuctionsBiddingWS_v2.asmx");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public GoDaddySoapClient1 soapConnector1(Jaxb2Marshaller marshaller1) {
        GoDaddySoapClient1 client = new GoDaddySoapClient1();
        client.setDefaultUri("https://auctions.godaddy.com/gdAuctionsWSAPI/gdAuctionsMemberWS_v2.asmx");
        client.setMarshaller(marshaller1);
        client.setUnmarshaller(marshaller1);
        return client;
    }
}
