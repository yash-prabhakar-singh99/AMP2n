package com.namekart.amp2.DCEntity;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class MapWrap {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "map",
            joinColumns = {@JoinColumn(name = "mw_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "domain")
    @Column(name = "auction_id")
    Map<String,Long> map;

    public Map<Long, String> getRm() {
        return rm;
    }

    public void setRm(Map<Long, String> rm) {
        this.rm = rm;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rm",
            joinColumns = {@JoinColumn(name = "mw_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "rauction_id")
    @Column(name = "rdomain")
    Map<Long,String> rm;



    public MapWrap() {
        map= new LinkedHashMap<>();
        rm=new LinkedHashMap<>();
    }

    public Integer getId() {
        return id;
    }


    public Map<String, Long> getMap() {
        return map;
    }

    public void setMap(Map<String, Long> map) {
        this.map = map;
    }
}
