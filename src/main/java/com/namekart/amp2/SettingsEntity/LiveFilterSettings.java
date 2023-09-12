package com.namekart.amp2.SettingsEntity;

import javax.persistence.*;
import java.util.*;

@Entity
public class LiveFilterSettings {
    Boolean noHyphens, noNumbers;
    Integer lowLength, upLength;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "extnEST",
            joinColumns = {@JoinColumn(name = "ESTs", referencedColumnName = "id")})
    @MapKeyColumn(name = "Extn")
    @Column(name = "extnEst")
    Map<String,Integer> extnEst;

    String newExtns;

    Integer newExtEsts,elseEsts;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "newExtns", joinColumns = @JoinColumn(name = "Extns"))
    Set<String> newExtnsSet;

    public String getNewExtns() {
        return newExtns;
    }

    public void setNewExtns(String newExtns) {
        this.newExtns = newExtns;
        this.newExtnsSet.addAll(Arrays.asList(newExtns.split(",")));
    }

    public Set<String> getNewExtnsSet() {
        return newExtnsSet;
    }

    public void setNewExtnsSet(Set<String> newExtnsSet) {
        this.newExtnsSet = newExtnsSet;
    }

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "restrictedExtns", joinColumns = @JoinColumn(name = "Extns"))
    Set<String> restrictedExtns;

    public Integer getNewExtEsts() {
        return newExtEsts;
    }

    public void setNewExtEsts(Integer newExtEsts) {
        this.newExtEsts = newExtEsts;
    }

    public Integer getElseEsts() {
        return elseEsts;
    }

    public void setElseEsts(Integer elseEsts) {
        this.elseEsts = elseEsts;
    }

    public LiveFilterSettings() {
        this.noHyphens=true;
        this.noNumbers=true;
        this.lowLength=0;
        this.upLength=35;
        this.extnEst= new HashMap<>();
        this.newExtns="academy,ae,ai,au,at,attorney,beauty,blog,capital,care,careers,ca,co,co.uk,co.in,de,digital,domain,earth,eu,es,exchange,farm,fit,finance,guru,guide,games,id,ie,in,io,is,info,inc,it,cc,com,legal,law,lawyer,life,live,london,ly,llc,inc,jewelry,market,me,miami,media,money,mortgage,mv,net,news,network,nl,no,nyc,nz,one,online,org,plus,properties,property,rehab,rentals,sale,science,se,sh,shop,so,social,store,studio,tech,to,trade,tv,us,vc,vegas,vacations,ventures,wiki,work,world";
        this.newExtnsSet=new HashSet<>();
        this.restrictedExtns=new HashSet<>();
        this.newExtEsts=0;
        this.elseEsts=0;
        extnEst.put("com",10);extnEst.put("net",1000);extnEst.put("org",1000);extnEst.put("info",1000);extnEst.put("co",0);
        extnEst.put("me",0);extnEst.put("tv",0);extnEst.put("ai",0);extnEst.put("io",0);
        restrictedExtns.add("xyz");
        newExtnsSet.addAll(Arrays.asList(newExtns.split(",")));
    }

    public Boolean getNoHyphens() {
        return noHyphens;
    }

    public void setNoHyphens(Boolean noHyphens) {
        this.noHyphens = noHyphens;
    }

    public Boolean getNoNumbers() {
        return noNumbers;
    }

    public void setNoNumbers(Boolean noNumbers) {
        this.noNumbers = noNumbers;
    }

    public Integer getLowLength() {
        return lowLength;
    }

    public void setLowLength(Integer lowLength) {
        this.lowLength = lowLength;
    }

    public Integer getUpLength() {
        return upLength;
    }

    public void setUpLength(Integer upLength) {
        this.upLength = upLength;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, Integer> getExtnEst() {
        return extnEst;
    }

    public void setExtnEst(Map<String, Integer> extnEst) {
        this.extnEst = extnEst;
    }

    public Set<String> getRestrictedExtns() {
        return restrictedExtns;
    }

    public void setRestrictedExtns(Set<String> restrictedExtns) {
        this.restrictedExtns = restrictedExtns;
    }
}
