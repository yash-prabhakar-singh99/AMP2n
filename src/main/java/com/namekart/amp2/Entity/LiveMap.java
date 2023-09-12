package com.namekart.amp2.Entity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class LiveMap {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mapl",
            joinColumns = {@JoinColumn(name = "ml_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "livedomain")
    @Column(name = "auction_id")
    Map<String,Long> map;

    public LiveMap(Integer id) {
        this.id = id;
        this.map= new HashMap<>();
        this.mapnc=new HashMap<>();
        this.mapgd=new HashMap<>();
        this.mapdc= new HashMap<>();
        this.mapns=new HashMap<>();
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mapnc",
            joinColumns = {@JoinColumn(name = "mlnc_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "livencid")
    @Column(name = "livedomainnc")
    Map<String,String> mapnc;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mapns",
            joinColumns = {@JoinColumn(name = "mlns_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "livensid")
    @Column(name = "livedomainns")
    Map<Long,String> mapns;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mapgd",
            joinColumns = {@JoinColumn(name = "mgd_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "livegdid")
    @Column(name = "livedomaingd")
    Map<String,String> mapgd;

    public Map<String, String> getMapgd() {
        return mapgd;
    }

    public void setMapgd(Map<String, String> mapgd) {
        this.mapgd = mapgd;
    }

    public Map<Long, String> getMapdc() {
        return mapdc;
    }

    public void setMapdc(Map<Long, String> mapdc) {
        this.mapdc = mapdc;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mapdc",
            joinColumns = {@JoinColumn(name = "mapdc_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "liveiddc")
    @Column(name = "livedomaindc")
    Map<Long,String> mapdc;

    public Map<Long, String> getMapns() {
        return mapns;
    }

    public void setMapns(Map<Long, String> mapns) {
        this.mapns = mapns;
    }
/*
    public Map<String, String> getMapnsregistered() {
        return mapnsregistered;
    }

    public void setMapnsregistered(Map<String, String> mapnsregistered) {
        this.mapnsregistered = mapnsregistered;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mapns",
            joinColumns = {@JoinColumn(name = "mlnsr_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "livensidreg")
    @Column(name = "livedomainnsreg")
    Map<String,String> mapnsregistered;
    */

    public Map<String, Long> getMap() {
        return map;
    }

    public Map<String, String> getMapnc() {
        return mapnc;
    }

    public void setMapnc(Map<String, String> mapnc) {
        this.mapnc = mapnc;
    }

    public void setMap(Map<String, Long> map) {
        this.map = map;
    }

    public LiveMap() {
        this.map= new HashMap<>();
        this.mapnc=new HashMap<>();
        this.mapgd=new HashMap<>();
        this.mapdc= new HashMap<>();
        this.mapns=new HashMap<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
