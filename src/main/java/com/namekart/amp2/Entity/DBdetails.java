package com.namekart.amp2.Entity;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="dbdetails")
public class DBdetails {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    String domain;
    String wonby;
    String wonat;
    Float myLastBid=0f;

    boolean account=false;

    public boolean getAccount() {
        return account;
    }

    public void setAccount(boolean account) {
        this.account = account;
    }

    public Float getMyLastBid() {
        return myLastBid;
    }

    public void setMyLastBid(Float myLastBid) {
        this.myLastBid = myLastBid;
    }

    public void setBidPlaced(boolean bidPlaced) {
        isBidPlaced = bidPlaced;
    }

    public Boolean getWasWatchlisted() {
        return wasWatchlisted;
    }

    public void setWasWatchlisted(Boolean wasWatchlisted) {
        this.wasWatchlisted = wasWatchlisted;
    }

    Boolean wasWatchlisted;

    public Boolean getMute() {
        return mute;
    }

    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    Boolean mute=false;
    Integer gdv;
    public Integer getGdv()
    {
        return gdv;
    }
    public void setGdv(Integer gdv) {
        this.gdv = gdv;
    }
    Boolean fetched;
    public Boolean getFetched() {
        return fetched;
    }

    public void setFetched(Boolean fetched) {
        this.fetched = fetched;
    }

    String namecheapid;

    public String getNamecheapid() {
        return namecheapid;
    }

    public void setNamecheapid(String namecheapid) {
        this.namecheapid = namecheapid;
    }

    Long auctionId;
    String mymaxbid;

    String platform;

    boolean track;

    public boolean isTrack() {
        return track;
    }

    public void setTrack(boolean track) {
        this.track = track;
        this.mute=true;
    }

    String currbid;

    Long bidders;

    String time_left;

    Long age;

    Integer bids;

    public Integer getBids() {
        return bids;
    }

    public void setBids(Integer bids) {
        this.bids = bids;
    }

    Integer estibot;

    String auctiontype;

    boolean watchlist;

    boolean scheduled;

    public boolean getScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
        this.watchlist=scheduled;
    }

    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    int nw;

    public int getNw() {
        return nw;
    }

    public void setNw(int nw) {
        this.nw = nw;
    }

    public boolean isWatchlist() {
        return watchlist;
    }

    public void setWatchlist(boolean watchlist) {
        this.watchlist = watchlist;
    }

//    @OneToMany(mappedBy= "dbdetails", fetch= FetchType.LAZY, cascade= CascadeType.ALL)
//     List<DB_Bid_Details> bidhistory;

    public DBdetails(String domain, Integer gdv, Long auctionId, String platform, String currbid, String time_left, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, boolean isBidPlaced,String url) {
        this.domain = domain;
        this.gdv = gdv;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.time_left = time_left;
        this.auctiontype = auctiontype;
        this.bidAmount = bidAmount;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.isBidPlaced = isBidPlaced;
        this.url=url;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain,Long auctionId ,String platform, String currbid, Long bidders, String time_left, Long age, Integer estibot, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.time_left = time_left;
        this.age = age;
        this.estibot = estibot;
        this.auctiontype = auctiontype;
        this.bidAmount = bidAmount;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        //this.bidhistory= new ArrayList<>();
        this.watchlist=false;
        this.auctionId = auctionId;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain ,String platform, String currbid, Long bidders, String time_left, Long age, Integer estibot, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.time_left = time_left;
        this.age = age;
        this.estibot = estibot;
        this.auctiontype = auctiontype;
        this.bidAmount = bidAmount;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        //this.bidhistory= new ArrayList<>();
        this.watchlist=false;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }
    public DBdetails(String domain, Long auctionId, String platform, String currbid, Long bidders, String time_left, Long age, Integer estibot, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, boolean isBidPlaced) {
        this.domain = domain;
        this.auctionId=auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.time_left = time_left;
        this.age = age;
        this.estibot = estibot;
        this.auctiontype = auctiontype;
        this.bidAmount = bidAmount;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.isBidPlaced = isBidPlaced;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, String platform, String currbid, Long bidders, String time_left, Long age, Integer estibot, String auctiontype, String bidAmount, String result, String endTimepst, String endTimeist, boolean isBidPlaced) {
        this.domain = domain;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.time_left = time_left;
        this.age = age;
        this.estibot = estibot;
        this.auctiontype = auctiontype;
        this.bidAmount = bidAmount;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.isBidPlaced = isBidPlaced;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, Long auctionId, String platform, String currbid, Long bidders, String auctiontype, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced, String namecheapid) {
        this.domain = domain;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.auctiontype = auctiontype;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.watchlist=false;
        this.namecheapid=namecheapid;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, Long auctionId, String platform, String currbid, Long bidders, String time_left,String auctiontype, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidders = bidders;
        this.time_left=time_left;
        this.auctiontype = auctiontype;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.watchlist=false;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public DBdetails(String domain, Long auctionId, String platform, String currbid, String bidAmount,Long bidders, String time_left,String auctiontype, String result, String endTimepst, String endTimeist, String bidplacetime, boolean isBidPlaced) {
        this.domain = domain;
        this.auctionId = auctionId;
        this.platform = platform;
        this.currbid = currbid;
        this.bidAmount=bidAmount;
        this.bidders = bidders;
        this.time_left=time_left;
        this.auctiontype = auctiontype;
        this.result = result;
        this.endTimepst = endTimepst;
        this.endTimeist = endTimeist;
        this.bidplacetime = bidplacetime;
        this.isBidPlaced = isBidPlaced;
        this.watchlist=false;
        ft = new SimpleDateFormat("yyyy-MM-dd");

    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getMymaxbid() {
        return mymaxbid;
    }

    public void setMymaxbid(String mymaxbid) {
        this.mymaxbid = mymaxbid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCurrbid() {
        return currbid;
    }

    public void setCurrbid(String currbid) {
        this.currbid = currbid;
    }

    public Long getBidders() {
        return bidders;
    }

    public void setBidders(Long bidders) {
        this.bidders = bidders;
    }

    public String getTime_left() {
        return time_left;
    }

    public void setTime_left(String time_left) {
        this.time_left = time_left;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Integer getEstibot() {
        return estibot;
    }

    public void setEstibot(Integer estibot) {
        this.estibot = estibot;
    }

    public String getAuctiontype() {
        return auctiontype;
    }

    public void setAuctiontype(String auctiontype) {
        this.auctiontype = auctiontype;
    }

    /*public List<DB_Bid_Details> getBidhistory() {
        return bidhistory;
    }*/

   /* public void setBidhistory(List<DB_Bid_Details> bidhistory) {
        this.bidhistory = bidhistory;
    }*/

    public String getWonby() {
        return wonby;
    }

    public void setWonby(String wonby) {
        this.wonby = wonby;
    }

    public String getWonat() {
        return wonat;
    }

    public void setWonat(String wonat) {
        this.wonat = wonat;
    }
Integer extensions_taken;

    public Integer getExtensions_taken() {
        return extensions_taken;
    }

    public void setExtensions_taken(Integer extensions_taken) {
        this.extensions_taken = extensions_taken;
    }

    String bidAmount;
    String result;
    String endTimepst;
    String endTimeist;

    String bidplacetime;

    boolean isBidPlaced;

    public boolean isApproachWarn() {
        return approachWarn;
    }

    public void setApproachWarn(boolean approachWarn) {
        this.approachWarn = approachWarn;
    }

    boolean approachWarn=true;

    boolean estFlag;

    Integer keyword_exact_lsv;

    Float keyword_exact_cpc;

    String whois_create_date, whois_registrar;

    Integer end_users_buyers;

    Integer wayback_age;
    Integer appraised_wholesale_value;
    Integer num_words;
    Integer is_cctld;
    Integer is_ntld;
    Integer is_adult;
    Integer is_reversed;
    Integer num_numbers;
    Integer sld_length;
    Integer search_ads_phrase;
    Integer has_trademark;
    Integer backlinks;
    Integer wayback_records;

    public void setEstValues(Integer estibot, Integer keyword_exact_lsv,Float keyword_exact_cpc,String whois_create_date,String whois_registrar,Integer end_users_buyers,
    Integer wayback_age,
    Integer appraised_wholesale_value,
    Integer num_words,
    Integer is_cctld,
    Integer is_ntld,
    Integer is_adult,
    Integer is_reversed,
    Integer num_numbers,
    Integer sld_length,
    Integer search_ads_phrase,
    Integer has_trademark,
    Integer backlinks,
    Integer wayback_records,Integer pronounceability_score,
    String language,
    Float language_probability,
    String category,String category_root, String first_word,String second_word, Integer extensions_taken)
    {
        this.estFlag=true;
        this.estibot=estibot;
        this.keyword_exact_cpc=keyword_exact_cpc;
        this.keyword_exact_lsv=keyword_exact_lsv;
        this.whois_registrar=whois_registrar;
        this.whois_create_date=whois_create_date;
        setAgeWhois(whois_create_date);
        this.end_users_buyers=end_users_buyers;
        this.wayback_age=wayback_age;
        this.appraised_wholesale_value=appraised_wholesale_value;
        this.num_words=num_words;
        this.num_numbers=num_numbers;
        this.is_adult=is_adult;
        this.is_cctld=is_cctld;
        this.is_reversed=is_reversed;
        this.is_ntld=is_ntld;
        this.sld_length=sld_length;
        this.search_ads_phrase=search_ads_phrase;
        this.has_trademark=has_trademark;
        this.backlinks=backlinks;
        this.wayback_records=wayback_records;
        this.pronounceability_score=pronounceability_score;
        this.language=language;
        this.language_probability=language_probability;
        this.category=category;
        this.category_root=category_root;
        this.first_word=first_word;
        this.second_word=second_word;
        this.extensions_taken=extensions_taken;
    }
    public Integer getAppraised_wholesale_value() {
        return appraised_wholesale_value;
    }

    public void setAppraised_wholesale_value(Integer appraised_wholesale_value) {
        this.appraised_wholesale_value = appraised_wholesale_value;
    }

    public Integer getNum_words() {
        return num_words;
    }

    public void setNum_words(Integer num_words) {
        this.num_words = num_words;
    }

    public Integer getIs_cctld() {
        return is_cctld;
    }

    public void setIs_cctld(Integer is_cctld) {
        this.is_cctld = is_cctld;
    }

    public Integer getIs_ntld() {
        return is_ntld;
    }

    public void setIs_ntld(Integer is_ntld) {
        this.is_ntld = is_ntld;
    }

    public Integer getIs_adult() {
        return is_adult;
    }

    public void setIs_adult(Integer is_adult) {
        this.is_adult = is_adult;
    }

    public Integer getIs_reversed() {
        return is_reversed;
    }

    public void setIs_reversed(Integer is_reversed) {
        this.is_reversed = is_reversed;
    }

    public Integer getNum_numbers() {
        return num_numbers;
    }

    public void setNum_numbers(Integer num_numbers) {
        this.num_numbers = num_numbers;
    }

    public Integer getSld_length() {
        return sld_length;
    }

    public void setSld_length(Integer sld_length) {
        this.sld_length = sld_length;
    }

    public Integer getSearch_ads_phrase() {
        return search_ads_phrase;
    }

    public void setSearch_ads_phrase(Integer search_ads_phrase) {
        this.search_ads_phrase = search_ads_phrase;
    }

    public Integer getHas_trademark() {
        return has_trademark;
    }

    public void setHas_trademark(Integer has_trademark) {
        this.has_trademark = has_trademark;
    }

    public Integer getBacklinks() {
        return backlinks;
    }

    public void setBacklinks(Integer backlinks) {
        this.backlinks = backlinks;
    }

    public Integer getWayback_records() {
        return wayback_records;
    }

    public void setWayback_records(Integer wayback_records) {
        this.wayback_records = wayback_records;
    }

    public Integer getPronounceability_score() {
        return pronounceability_score;
    }

    public void setPronounceability_score(Integer pronounceability_score) {
        this.pronounceability_score = pronounceability_score;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Float getLanguage_probability() {
        return language_probability;
    }

    public void setLanguage_probability(Float language_probability) {
        this.language_probability = language_probability;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_root() {
        return category_root;
    }

    public void setCategory_root(String category_root) {
        this.category_root = category_root;
    }

    public String getFirst_word() {
        return first_word;
    }

    public void setFirst_word(String first_word) {
        this.first_word = first_word;
    }

    public String getSecond_word() {
        return second_word;
    }

    public void setSecond_word(String second_word) {
        this.second_word = second_word;
    }

    Integer pronounceability_score;

    String language;

    Float language_probability;

    String category,category_root,first_word,second_word;



    @Transient
    SimpleDateFormat ft= new SimpleDateFormat("yyyy-MM-dd");

    public void setAgeWhois(String whois_create_date)
    {
        try {
            Date d = ft.parse(whois_create_date);
            Date now = new Date();
            this.age= Long.valueOf(now.getYear()-d.getYear());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean isEstFlag() {
        return estFlag;
    }

    public void setEstFlag(boolean estFlag) {
        this.estFlag = estFlag;
    }

    public Integer getKeyword_exact_lsv() {
        return keyword_exact_lsv;
    }

    public void setKeyword_exact_lsv(Integer keyword_exact_lsv) {
        this.keyword_exact_lsv = keyword_exact_lsv;
    }

    public Float getKeyword_exact_cpc() {
        return keyword_exact_cpc;
    }

    public void setKeyword_exact_cpc(Float keyword_exact_cpc) {
        this.keyword_exact_cpc = keyword_exact_cpc;
    }

    public String getWhois_create_date() {
        return whois_create_date;
    }

    public void setWhois_create_date(String whois_create_date) {
        this.whois_create_date = whois_create_date;
    }

    public String getWhois_registrar() {
        return whois_registrar;
    }

    public void setWhois_registrar(String whois_registrar) {
        this.whois_registrar = whois_registrar;
    }

    public Integer getEnd_users_buyers() {
        return end_users_buyers;
    }

    public void setEnd_users_buyers(Integer end_users_buyers) {
        this.end_users_buyers = end_users_buyers;
    }

    public Integer getWayback_age() {
        return wayback_age;
    }

    public void setWayback_age(Integer wayback_age) {
        this.wayback_age = wayback_age;
    }

    public DBdetails() {
        ft = new SimpleDateFormat("yyyy-MM-dd");
    }



    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
        this.approachWarn=true;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
        if(result.equals("Won")||result.equals("Loss")||result.equals("Bid Not Placed")||result.equals("Bid Cancelled"))
        {this.scheduled=false;
        this.watchlist=false;
        this.wasWatchlisted=true;}
        /*else if(result.equals("Bid Placed And Scheduled")||result.equals("Bid Scheduled")||result.equals("Placed"))
            this.scheduled=true;*/
    }



    public String getEndTimepst() {
        return endTimepst;
    }

    public void setEndTimepst(String endTimepst) {
        this.endTimepst = endTimepst;
    }

    public String getEndTimeist() {
        return endTimeist;
    }

    public void setEndTimeist(String endTimeist) {
        this.endTimeist = endTimeist;
    }

    public String getBidplacetime() {
        return bidplacetime;
    }

    public void setBidplacetime(String bidplacetime) {
        this.bidplacetime = bidplacetime;
    }

    public boolean isBidPlaced() {
        return isBidPlaced;
    }

    public void setIsBidPlaced(boolean bidPlaced) {
        isBidPlaced = bidPlaced;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
