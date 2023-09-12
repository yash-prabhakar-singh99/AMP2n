package com.namekart.amp2.EstibotEntity;

public class Estibot_Data {
    public Estibot_Data() {
    }

    public Estibot_Data(String domain, Integer appraised_value) {
        this.domain = domain;
        this.appraised_value = appraised_value;
    }
    /*String domain, domain_cc, sld, sld_ntld,tld,words,language, category,category_root,first_word,second_word,keyword_locale,keyword_exact_keyword,keyword_broad_keyword,
            keyword_ng_locale,keyword_ng_exact_keyword,keyword_ng_broad_keyword,sld_ng_exact_keyword,sld_ng_broad_keyword,whois_create_date,whois_expire_date,whois_update_date,whois_registrar,whois_registrar_iana,
            whois_reg_name,whois_reg_org,whois_reg_email,whois_reg_email_count,trademark_type,trademark_term,trademark_company,site_language,traffic_estimate_source;

    Integer is_cctld,is_ntld,is_adult,is_reversed,appraised_value,udrp;

    Long id;

    Integer appraised_wholesale_value,appraised_monetization_value,appraised_ignore_tm_value,appraisal_data_source_id,price_range_retail,price_range_no_tm,
            classification_id,num_words,num_hyphens,num_numbers,sld_length,com_taken,net_taken,org_taken,biz_taken,us_taken,info_taken,extensions_taken,search_results_phrase,search_ads_phrase*/

    public String domain;
    public String domain_cc;
    public String sld;
    public String sld_ntld;
    public String tld;
    public int is_cctld;
    public int is_ntld;
    public int is_adult;
    public int is_reversed;
    public int udrp;
    public String words;
    public String language;
    public String language_code;
    public Float language_probability;
    public String first_word;
    public String second_word;
    public int num_words;
    public int num_hyphens;
    public int num_numbers;
    public int sld_length;
    public int has_letters;
    public int has_numbers;
    public int has_only_letters;
    public int has_only_numbers;
    public int has_trademark;
    public int has_udrp_wipo;
    public int has_bad_ending;
    public int word_choppyness;
    public int unique_characters;
    public int unique_letters;
    public int words_rhyme;
    public int rhyme_extension;
    public String rhyme_pattern;
    public int words_flow;
    public int pronounceability_score;
    public String category;
    public String category_root;
    public int classification_id;
    public Integer appraised_value;
    public Integer appraised_wholesale_value;
    public Integer appraised_monetization_value;
    public Integer appraised_ignore_tm_value;
    public int appraisal_data_source_id;
    public int price_range_retail;
    public int price_range_no_tm;
    public int com_taken;
    public int net_taken;
    public int org_taken;
    public int biz_taken;
    public int us_taken;
    public int info_taken;
    public int extensions_taken;
    public Object sld_extensions_taken;
    public int has_dns;
    public Float search_results_phrase;
    public int search_ads_phrase;
    public Float search_results_reverse_phrase;
    public Float search_results_sld;
    public int search_ads_sld;
    public Float search_suggestion_sld;
    public int serp_source_phrase;
    public int serp_source_sld;
    public int search_results_tld;
    public int google_site_index;
    public String using_previous_sales_data;
    public int pagerank;
    public int pagerank_real;
    public int traffic_estimate;
    public int word_tracker_sld;
    public int word_tracker_term;
    public int alexa_traffic_rank;
    public String alexa_reach_3m;
    public int alexa_link_popularity;
    public String dmoz_listed;
    public int altavista_link_popularity;
    public int google_link_popularity;
    public int yahoo_link_popularity;
    public int altavista_link_saturation;
    public int google_link_saturation;
    public int yahoo_link_saturation;
    public String traffic_estimate_source;
    public String parking_revenue_estimate;
    public Float development_revenue_estimate;
    public int wayback_age;
    public int wayback_records;
    public int overture_tld;
    public int overture_sld;
    public int overture_term;
    public String keyword_locale;
   // public KeywordStats keyword_stats;
    public String keyword_ng_locale;
    /*public KeywordStatsNg keyword_stats_ng;
    public KeywordStatsRootNg keyword_stats_root_ng;
    public KeywordStatsExactNtldNg keyword_stats_exact_ntld_ng;*/
    //public ArrayList<SalesHistory> sales_history;
    public int end_user_buyers;
    //public Whois whois;
   // public Web web;
    //public Trademark trademark;
    public String using_sld;
    public String trending;
    public String trending_message;
    public int trademark_sld_count;
    public String char_pattern;
    public String char_pattern_basic;
    //public Stock stock;
    public int avg_sales_price;
    //public ArrayList<SimilarSale> similar_sales;
    public String appraiser_version;
    public String trademark_type;
    public String trademark_term;
    public String trademark_company;
    public int trademark_risk;
    public Float trademark_probability;
    public int trademark_domains;
    public int trademark_penalized;
    public String whois_create_date;
    public String whois_expire_date;
    public String whois_update_date;
    public String whois_registrar;
    public String whois_registrar_iana;
    public String whois_reg_name;
    public Object whois_reg_org;
    public String whois_reg_email;
    public int whois_reg_email_count;
    public int whois_name_count;
    public String whois_is_private;
    public String whois_age;
    public int web_pages;
    public int web_links_internal;
    public int web_links_external;
    public int web_links_external_unique;
    public Object site_language;
    public int site_status;
    public int backlinks;
    public int backlinks_unique;
    public int backlink_ips;
    public int backlink_ips_unique;
    public int backlink_subnets;
    public int backlink_subnets_unique;
    public String keyword_broad_keyword;
    public String keyword_broad_type;
    public String keyword_broad_competition;
    public Float keyword_broad_cpc;
    public int keyword_broad_global_search_volume;
    public int keyword_broad_local_search_volume;
    public int keyword_broad_month;
    public String keyword_broad_is_ng;
    public String keyword_exact_keyword;
    public String keyword_exact_type;
    public String keyword_exact_competition;
    public Float keyword_exact_cpc;
    public int keyword_exact_global_search_volume;
    public int keyword_exact_local_search_volume;
    public int keyword_exact_month;
    public String keyword_exact_is_ng;
    public String keyword_ng_broad_keyword;
    public String keyword_ng_broad_type;
    public String keyword_ng_broad_competition;
    public Float keyword_ng_broad_cpc;
    public int keyword_ng_broad_global_search_volume;
    public int keyword_ng_broad_local_search_volume;
    public int keyword_ng_broad_month;
    public String keyword_ng_broad_is_ng;
    public String keyword_ng_exact_keyword;
    public String keyword_ng_exact_type;
    public String keyword_ng_exact_competition;
    public Float keyword_ng_exact_cpc;
    public int keyword_ng_exact_global_search_volume;
    public int keyword_ng_exact_local_search_volume;
    public int keyword_ng_exact_month;
    public String keyword_ng_exact_is_ng;

    public String sld_ng_exact_keyword;
    public Integer sld_ng_exact_competition;
    public Float sld_ng_exact_cpc;
    public Integer sld_ng_exact_global_search_volume;
    public Integer sld_ng_exact_local_search_volume;
    public String sld_ng_broad_keyword;
    public Integer sld_ng_broad_competition;
    public Float sld_ng_broad_cpc;
    public Integer sld_ng_broad_global_search_volume;
    public Integer sld_ng_broad_local_search_volume;
    public Object tld_ng_exact_keyword;
    public Integer tld_ng_exact_competition;
    public Float tld_ng_exact_cpc;
    public Integer tld_ng_exact_global_search_volume;

    public String getSld_ng_exact_keyword() {
        return sld_ng_exact_keyword;
    }

    public void setSld_ng_exact_keyword(String sld_ng_exact_keyword) {
        this.sld_ng_exact_keyword = sld_ng_exact_keyword;
    }

    public Integer getSld_ng_exact_competition() {
        return sld_ng_exact_competition;
    }

    public void setSld_ng_exact_competition(Integer sld_ng_exact_competition) {
        this.sld_ng_exact_competition = sld_ng_exact_competition;
    }

    public Float getSld_ng_exact_cpc() {
        return sld_ng_exact_cpc;
    }

    public void setSld_ng_exact_cpc(Float sld_ng_exact_cpc) {
        this.sld_ng_exact_cpc = sld_ng_exact_cpc;
    }

    public Integer getSld_ng_exact_global_search_volume() {
        return sld_ng_exact_global_search_volume;
    }

    public void setSld_ng_exact_global_search_volume(Integer sld_ng_exact_global_search_volume) {
        this.sld_ng_exact_global_search_volume = sld_ng_exact_global_search_volume;
    }

    public Integer getSld_ng_exact_local_search_volume() {
        return sld_ng_exact_local_search_volume;
    }

    public void setSld_ng_exact_local_search_volume(Integer sld_ng_exact_local_search_volume) {
        this.sld_ng_exact_local_search_volume = sld_ng_exact_local_search_volume;
    }

    public String getSld_ng_broad_keyword() {
        return sld_ng_broad_keyword;
    }

    public void setSld_ng_broad_keyword(String sld_ng_broad_keyword) {
        this.sld_ng_broad_keyword = sld_ng_broad_keyword;
    }

    public Integer getSld_ng_broad_competition() {
        return sld_ng_broad_competition;
    }

    public void setSld_ng_broad_competition(Integer sld_ng_broad_competition) {
        this.sld_ng_broad_competition = sld_ng_broad_competition;
    }

    public Float getSld_ng_broad_cpc() {
        return sld_ng_broad_cpc;
    }

    public void setSld_ng_broad_cpc(Float sld_ng_broad_cpc) {
        this.sld_ng_broad_cpc = sld_ng_broad_cpc;
    }

    public Integer getSld_ng_broad_global_search_volume() {
        return sld_ng_broad_global_search_volume;
    }

    public void setSld_ng_broad_global_search_volume(Integer sld_ng_broad_global_search_volume) {
        this.sld_ng_broad_global_search_volume = sld_ng_broad_global_search_volume;
    }

    public Integer getSld_ng_broad_local_search_volume() {
        return sld_ng_broad_local_search_volume;
    }

    public void setSld_ng_broad_local_search_volume(Integer sld_ng_broad_local_search_volume) {
        this.sld_ng_broad_local_search_volume = sld_ng_broad_local_search_volume;
    }

    public Object getTld_ng_exact_keyword() {
        return tld_ng_exact_keyword;
    }

    public void setTld_ng_exact_keyword(Object tld_ng_exact_keyword) {
        this.tld_ng_exact_keyword = tld_ng_exact_keyword;
    }

    public Integer getTld_ng_exact_competition() {
        return tld_ng_exact_competition;
    }

    public void setTld_ng_exact_competition(Integer tld_ng_exact_competition) {
        this.tld_ng_exact_competition = tld_ng_exact_competition;
    }

    public Float getTld_ng_exact_cpc() {
        return tld_ng_exact_cpc;
    }

    public void setTld_ng_exact_cpc(Float tld_ng_exact_cpc) {
        this.tld_ng_exact_cpc = tld_ng_exact_cpc;
    }

    public Integer getTld_ng_exact_global_search_volume() {
        return tld_ng_exact_global_search_volume;
    }

    public void setTld_ng_exact_global_search_volume(Integer tld_ng_exact_global_search_volume) {
        this.tld_ng_exact_global_search_volume = tld_ng_exact_global_search_volume;
    }

    public Integer getTld_ng_exact_local_search_volume() {
        return tld_ng_exact_local_search_volume;
    }

    public void setTld_ng_exact_local_search_volume(Integer tld_ng_exact_local_search_volume) {
        this.tld_ng_exact_local_search_volume = tld_ng_exact_local_search_volume;
    }

    public Integer tld_ng_exact_local_search_volume;


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain_cc() {
        return domain_cc;
    }

    public void setDomain_cc(String domain_cc) {
        this.domain_cc = domain_cc;
    }

    public String getSld() {
        return sld;
    }

    public void setSld(String sld) {
        this.sld = sld;
    }

    public String getSld_ntld() {
        return sld_ntld;
    }

    public void setSld_ntld(String sld_ntld) {
        this.sld_ntld = sld_ntld;
    }

    public String getTld() {
        return tld;
    }

    public void setTld(String tld) {
        this.tld = tld;
    }

    public int getIs_cctld() {
        return is_cctld;
    }

    public void setIs_cctld(int is_cctld) {
        this.is_cctld = is_cctld;
    }

    public int getIs_ntld() {
        return is_ntld;
    }

    public void setIs_ntld(int is_ntld) {
        this.is_ntld = is_ntld;
    }

    public int getIs_adult() {
        return is_adult;
    }

    public void setIs_adult(int is_adult) {
        this.is_adult = is_adult;
    }

    public int getIs_reversed() {
        return is_reversed;
    }

    public void setIs_reversed(int is_reversed) {
        this.is_reversed = is_reversed;
    }

    public int getUdrp() {
        return udrp;
    }

    public void setUdrp(int udrp) {
        this.udrp = udrp;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public Float getLanguage_probability() {
        return language_probability;
    }

    public void setLanguage_probability(Float language_probability) {
        this.language_probability = language_probability;
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

    public int getNum_words() {
        return num_words;
    }

    public void setNum_words(int num_words) {
        this.num_words = num_words;
    }

    public int getNum_hyphens() {
        return num_hyphens;
    }

    public void setNum_hyphens(int num_hyphens) {
        this.num_hyphens = num_hyphens;
    }

    public int getNum_numbers() {
        return num_numbers;
    }

    public void setNum_numbers(int num_numbers) {
        this.num_numbers = num_numbers;
    }

    public int getSld_length() {
        return sld_length;
    }

    public void setSld_length(int sld_length) {
        this.sld_length = sld_length;
    }

    public int getHas_letters() {
        return has_letters;
    }

    public void setHas_letters(int has_letters) {
        this.has_letters = has_letters;
    }

    public int getHas_numbers() {
        return has_numbers;
    }

    public void setHas_numbers(int has_numbers) {
        this.has_numbers = has_numbers;
    }

    public int getHas_only_letters() {
        return has_only_letters;
    }

    public void setHas_only_letters(int has_only_letters) {
        this.has_only_letters = has_only_letters;
    }

    public int getHas_only_numbers() {
        return has_only_numbers;
    }

    public void setHas_only_numbers(int has_only_numbers) {
        this.has_only_numbers = has_only_numbers;
    }

    public int getHas_trademark() {
        return has_trademark;
    }

    public void setHas_trademark(int has_trademark) {
        this.has_trademark = has_trademark;
    }

    public int getHas_udrp_wipo() {
        return has_udrp_wipo;
    }

    public void setHas_udrp_wipo(int has_udrp_wipo) {
        this.has_udrp_wipo = has_udrp_wipo;
    }

    public int getHas_bad_ending() {
        return has_bad_ending;
    }

    public void setHas_bad_ending(int has_bad_ending) {
        this.has_bad_ending = has_bad_ending;
    }

    public int getWord_choppyness() {
        return word_choppyness;
    }

    public void setWord_choppyness(int word_choppyness) {
        this.word_choppyness = word_choppyness;
    }

    public int getUnique_characters() {
        return unique_characters;
    }

    public void setUnique_characters(int unique_characters) {
        this.unique_characters = unique_characters;
    }

    public int getUnique_letters() {
        return unique_letters;
    }

    public void setUnique_letters(int unique_letters) {
        this.unique_letters = unique_letters;
    }

    public int getWords_rhyme() {
        return words_rhyme;
    }

    public void setWords_rhyme(int words_rhyme) {
        this.words_rhyme = words_rhyme;
    }

    public int getRhyme_extension() {
        return rhyme_extension;
    }

    public void setRhyme_extension(int rhyme_extension) {
        this.rhyme_extension = rhyme_extension;
    }

    public String getRhyme_pattern() {
        return rhyme_pattern;
    }

    public void setRhyme_pattern(String rhyme_pattern) {
        this.rhyme_pattern = rhyme_pattern;
    }

    public int getWords_flow() {
        return words_flow;
    }

    public void setWords_flow(int words_flow) {
        this.words_flow = words_flow;
    }

    public int getPronounceability_score() {
        return pronounceability_score;
    }

    public void setPronounceability_score(int pronounceability_score) {
        this.pronounceability_score = pronounceability_score;
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

    public int getClassification_id() {
        return classification_id;
    }

    public void setClassification_id(int classification_id) {
        this.classification_id = classification_id;
    }

    public Integer getAppraised_value() {
        return appraised_value;
    }

    public void setAppraised_value(Integer appraised_value) {
        this.appraised_value = appraised_value;
    }

    public Integer getAppraised_wholesale_value() {
        return appraised_wholesale_value;
    }

    public void setAppraised_wholesale_value(Integer appraised_wholesale_value) {
        this.appraised_wholesale_value = appraised_wholesale_value;
    }

    public Integer getAppraised_monetization_value() {
        return appraised_monetization_value;
    }

    public void setAppraised_monetization_value(Integer appraised_monetization_value) {
        this.appraised_monetization_value = appraised_monetization_value;
    }

    public Integer getAppraised_ignore_tm_value() {
        return appraised_ignore_tm_value;
    }

    public void setAppraised_ignore_tm_value(Integer appraised_ignore_tm_value) {
        this.appraised_ignore_tm_value = appraised_ignore_tm_value;
    }

    public int getAppraisal_data_source_id() {
        return appraisal_data_source_id;
    }

    public void setAppraisal_data_source_id(int appraisal_data_source_id) {
        this.appraisal_data_source_id = appraisal_data_source_id;
    }

    public int getPrice_range_retail() {
        return price_range_retail;
    }

    public void setPrice_range_retail(int price_range_retail) {
        this.price_range_retail = price_range_retail;
    }

    public int getPrice_range_no_tm() {
        return price_range_no_tm;
    }

    public void setPrice_range_no_tm(int price_range_no_tm) {
        this.price_range_no_tm = price_range_no_tm;
    }

    public int getCom_taken() {
        return com_taken;
    }

    public void setCom_taken(int com_taken) {
        this.com_taken = com_taken;
    }

    public int getNet_taken() {
        return net_taken;
    }

    public void setNet_taken(int net_taken) {
        this.net_taken = net_taken;
    }

    public int getOrg_taken() {
        return org_taken;
    }

    public void setOrg_taken(int org_taken) {
        this.org_taken = org_taken;
    }

    public int getBiz_taken() {
        return biz_taken;
    }

    public void setBiz_taken(int biz_taken) {
        this.biz_taken = biz_taken;
    }

    public int getUs_taken() {
        return us_taken;
    }

    public void setUs_taken(int us_taken) {
        this.us_taken = us_taken;
    }

    public int getInfo_taken() {
        return info_taken;
    }

    public void setInfo_taken(int info_taken) {
        this.info_taken = info_taken;
    }

    public int getExtensions_taken() {
        return extensions_taken;
    }

    public void setExtensions_taken(int extensions_taken) {
        this.extensions_taken = extensions_taken;
    }

    public Object getSld_extensions_taken() {
        return sld_extensions_taken;
    }

    public void setSld_extensions_taken(Object sld_extensions_taken) {
        this.sld_extensions_taken = sld_extensions_taken;
    }

    public int getHas_dns() {
        return has_dns;
    }

    public void setHas_dns(int has_dns) {
        this.has_dns = has_dns;
    }

    public Float getSearch_results_phrase() {
        return search_results_phrase;
    }

    public void setSearch_results_phrase(Float search_results_phrase) {
        this.search_results_phrase = search_results_phrase;
    }

    public int getSearch_ads_phrase() {
        return search_ads_phrase;
    }

    public void setSearch_ads_phrase(int search_ads_phrase) {
        this.search_ads_phrase = search_ads_phrase;
    }

    public Float getSearch_results_reverse_phrase() {
        return search_results_reverse_phrase;
    }

    public void setSearch_results_reverse_phrase(Float search_results_reverse_phrase) {
        this.search_results_reverse_phrase = search_results_reverse_phrase;
    }

    public Float getSearch_results_sld() {
        return search_results_sld;
    }

    public void setSearch_results_sld(Float search_results_sld) {
        this.search_results_sld = search_results_sld;
    }

    public int getSearch_ads_sld() {
        return search_ads_sld;
    }

    public void setSearch_ads_sld(int search_ads_sld) {
        this.search_ads_sld = search_ads_sld;
    }

    public Float getSearch_suggestion_sld() {
        return search_suggestion_sld;
    }

    public void setSearch_suggestion_sld(Float search_suggestion_sld) {
        this.search_suggestion_sld = search_suggestion_sld;
    }

    public int getSerp_source_phrase() {
        return serp_source_phrase;
    }

    public void setSerp_source_phrase(int serp_source_phrase) {
        this.serp_source_phrase = serp_source_phrase;
    }

    public int getSerp_source_sld() {
        return serp_source_sld;
    }

    public void setSerp_source_sld(int serp_source_sld) {
        this.serp_source_sld = serp_source_sld;
    }

    public int getSearch_results_tld() {
        return search_results_tld;
    }

    public void setSearch_results_tld(int search_results_tld) {
        this.search_results_tld = search_results_tld;
    }

    public int getGoogle_site_index() {
        return google_site_index;
    }

    public void setGoogle_site_index(int google_site_index) {
        this.google_site_index = google_site_index;
    }

    public String getUsing_previous_sales_data() {
        return using_previous_sales_data;
    }

    public void setUsing_previous_sales_data(String using_previous_sales_data) {
        this.using_previous_sales_data = using_previous_sales_data;
    }

    public int getPagerank() {
        return pagerank;
    }

    public void setPagerank(int pagerank) {
        this.pagerank = pagerank;
    }

    public int getPagerank_real() {
        return pagerank_real;
    }

    public void setPagerank_real(int pagerank_real) {
        this.pagerank_real = pagerank_real;
    }

    public int getTraffic_estimate() {
        return traffic_estimate;
    }

    public void setTraffic_estimate(int traffic_estimate) {
        this.traffic_estimate = traffic_estimate;
    }

    public int getWord_tracker_sld() {
        return word_tracker_sld;
    }

    public void setWord_tracker_sld(int word_tracker_sld) {
        this.word_tracker_sld = word_tracker_sld;
    }

    public int getWord_tracker_term() {
        return word_tracker_term;
    }

    public void setWord_tracker_term(int word_tracker_term) {
        this.word_tracker_term = word_tracker_term;
    }

    public int getAlexa_traffic_rank() {
        return alexa_traffic_rank;
    }

    public void setAlexa_traffic_rank(int alexa_traffic_rank) {
        this.alexa_traffic_rank = alexa_traffic_rank;
    }

    public String getAlexa_reach_3m() {
        return alexa_reach_3m;
    }

    public void setAlexa_reach_3m(String alexa_reach_3m) {
        this.alexa_reach_3m = alexa_reach_3m;
    }

    public int getAlexa_link_popularity() {
        return alexa_link_popularity;
    }

    public void setAlexa_link_popularity(int alexa_link_popularity) {
        this.alexa_link_popularity = alexa_link_popularity;
    }

    public String getDmoz_listed() {
        return dmoz_listed;
    }

    public void setDmoz_listed(String dmoz_listed) {
        this.dmoz_listed = dmoz_listed;
    }

    public int getAltavista_link_popularity() {
        return altavista_link_popularity;
    }

    public void setAltavista_link_popularity(int altavista_link_popularity) {
        this.altavista_link_popularity = altavista_link_popularity;
    }

    public int getGoogle_link_popularity() {
        return google_link_popularity;
    }

    public void setGoogle_link_popularity(int google_link_popularity) {
        this.google_link_popularity = google_link_popularity;
    }

    public int getYahoo_link_popularity() {
        return yahoo_link_popularity;
    }

    public void setYahoo_link_popularity(int yahoo_link_popularity) {
        this.yahoo_link_popularity = yahoo_link_popularity;
    }

    public int getAltavista_link_saturation() {
        return altavista_link_saturation;
    }

    public void setAltavista_link_saturation(int altavista_link_saturation) {
        this.altavista_link_saturation = altavista_link_saturation;
    }

    public int getGoogle_link_saturation() {
        return google_link_saturation;
    }

    public void setGoogle_link_saturation(int google_link_saturation) {
        this.google_link_saturation = google_link_saturation;
    }

    public int getYahoo_link_saturation() {
        return yahoo_link_saturation;
    }

    public void setYahoo_link_saturation(int yahoo_link_saturation) {
        this.yahoo_link_saturation = yahoo_link_saturation;
    }

    public String getTraffic_estimate_source() {
        return traffic_estimate_source;
    }

    public void setTraffic_estimate_source(String traffic_estimate_source) {
        this.traffic_estimate_source = traffic_estimate_source;
    }

    public String getParking_revenue_estimate() {
        return parking_revenue_estimate;
    }

    public void setParking_revenue_estimate(String parking_revenue_estimate) {
        this.parking_revenue_estimate = parking_revenue_estimate;
    }

    public Float getDevelopment_revenue_estimate() {
        return development_revenue_estimate;
    }

    public void setDevelopment_revenue_estimate(Float development_revenue_estimate) {
        this.development_revenue_estimate = development_revenue_estimate;
    }

    public int getWayback_age() {
        return wayback_age;
    }

    public void setWayback_age(int wayback_age) {
        this.wayback_age = wayback_age;
    }

    public int getWayback_records() {
        return wayback_records;
    }

    public void setWayback_records(int wayback_records) {
        this.wayback_records = wayback_records;
    }

    public int getOverture_tld() {
        return overture_tld;
    }

    public void setOverture_tld(int overture_tld) {
        this.overture_tld = overture_tld;
    }

    public int getOverture_sld() {
        return overture_sld;
    }

    public void setOverture_sld(int overture_sld) {
        this.overture_sld = overture_sld;
    }

    public int getOverture_term() {
        return overture_term;
    }

    public void setOverture_term(int overture_term) {
        this.overture_term = overture_term;
    }

    public String getKeyword_locale() {
        return keyword_locale;
    }

    public void setKeyword_locale(String keyword_locale) {
        this.keyword_locale = keyword_locale;
    }

    public String getKeyword_ng_locale() {
        return keyword_ng_locale;
    }

    public void setKeyword_ng_locale(String keyword_ng_locale) {
        this.keyword_ng_locale = keyword_ng_locale;
    }

    public int getEnd_user_buyers() {
        return end_user_buyers;
    }

    public void setEnd_user_buyers(int end_user_buyers) {
        this.end_user_buyers = end_user_buyers;
    }

    public String getUsing_sld() {
        return using_sld;
    }

    public void setUsing_sld(String using_sld) {
        this.using_sld = using_sld;
    }

    public String getTrending() {
        return trending;
    }

    public void setTrending(String trending) {
        this.trending = trending;
    }

    public String getTrending_message() {
        return trending_message;
    }

    public void setTrending_message(String trending_message) {
        this.trending_message = trending_message;
    }

    public int getTrademark_sld_count() {
        return trademark_sld_count;
    }

    public void setTrademark_sld_count(int trademark_sld_count) {
        this.trademark_sld_count = trademark_sld_count;
    }

    public String getChar_pattern() {
        return char_pattern;
    }

    public void setChar_pattern(String char_pattern) {
        this.char_pattern = char_pattern;
    }

    public String getChar_pattern_basic() {
        return char_pattern_basic;
    }

    public void setChar_pattern_basic(String char_pattern_basic) {
        this.char_pattern_basic = char_pattern_basic;
    }

    public int getAvg_sales_price() {
        return avg_sales_price;
    }

    public void setAvg_sales_price(int avg_sales_price) {
        this.avg_sales_price = avg_sales_price;
    }

    public String getAppraiser_version() {
        return appraiser_version;
    }

    public void setAppraiser_version(String appraiser_version) {
        this.appraiser_version = appraiser_version;
    }

    public String getTrademark_type() {
        return trademark_type;
    }

    public void setTrademark_type(String trademark_type) {
        this.trademark_type = trademark_type;
    }

    public String getTrademark_term() {
        return trademark_term;
    }

    public void setTrademark_term(String trademark_term) {
        this.trademark_term = trademark_term;
    }

    public String getTrademark_company() {
        return trademark_company;
    }

    public void setTrademark_company(String trademark_company) {
        this.trademark_company = trademark_company;
    }

    public int getTrademark_risk() {
        return trademark_risk;
    }

    public void setTrademark_risk(int trademark_risk) {
        this.trademark_risk = trademark_risk;
    }

    public Float getTrademark_probability() {
        return trademark_probability;
    }

    public void setTrademark_probability(Float trademark_probability) {
        this.trademark_probability = trademark_probability;
    }

    public int getTrademark_domains() {
        return trademark_domains;
    }

    public void setTrademark_domains(int trademark_domains) {
        this.trademark_domains = trademark_domains;
    }

    public int getTrademark_penalized() {
        return trademark_penalized;
    }

    public void setTrademark_penalized(int trademark_penalized) {
        this.trademark_penalized = trademark_penalized;
    }

    public String getWhois_create_date() {
        return whois_create_date;
    }

    public void setWhois_create_date(String whois_create_date) {
        this.whois_create_date = whois_create_date;
    }

    public String getWhois_expire_date() {
        return whois_expire_date;
    }

    public void setWhois_expire_date(String whois_expire_date) {
        this.whois_expire_date = whois_expire_date;
    }

    public String getWhois_update_date() {
        return whois_update_date;
    }

    public void setWhois_update_date(String whois_update_date) {
        this.whois_update_date = whois_update_date;
    }

    public String getWhois_registrar() {
        return whois_registrar;
    }

    public void setWhois_registrar(String whois_registrar) {
        this.whois_registrar = whois_registrar;
    }

    public String getWhois_registrar_iana() {
        return whois_registrar_iana;
    }

    public void setWhois_registrar_iana(String whois_registrar_iana) {
        this.whois_registrar_iana = whois_registrar_iana;
    }

    public String getWhois_reg_name() {
        return whois_reg_name;
    }

    public void setWhois_reg_name(String whois_reg_name) {
        this.whois_reg_name = whois_reg_name;
    }

    public Object getWhois_reg_org() {
        return whois_reg_org;
    }

    public void setWhois_reg_org(Object whois_reg_org) {
        this.whois_reg_org = whois_reg_org;
    }

    public String getWhois_reg_email() {
        return whois_reg_email;
    }

    public void setWhois_reg_email(String whois_reg_email) {
        this.whois_reg_email = whois_reg_email;
    }

    public int getWhois_reg_email_count() {
        return whois_reg_email_count;
    }

    public void setWhois_reg_email_count(int whois_reg_email_count) {
        this.whois_reg_email_count = whois_reg_email_count;
    }

    public int getWhois_name_count() {
        return whois_name_count;
    }

    public void setWhois_name_count(int whois_name_count) {
        this.whois_name_count = whois_name_count;
    }

    public String getWhois_is_private() {
        return whois_is_private;
    }

    public void setWhois_is_private(String whois_is_private) {
        this.whois_is_private = whois_is_private;
    }

    public String getWhois_age() {
        return whois_age;
    }

    public void setWhois_age(String whois_age) {
        this.whois_age = whois_age;
    }

    public int getWeb_pages() {
        return web_pages;
    }

    public void setWeb_pages(int web_pages) {
        this.web_pages = web_pages;
    }

    public int getWeb_links_internal() {
        return web_links_internal;
    }

    public void setWeb_links_internal(int web_links_internal) {
        this.web_links_internal = web_links_internal;
    }

    public int getWeb_links_external() {
        return web_links_external;
    }

    public void setWeb_links_external(int web_links_external) {
        this.web_links_external = web_links_external;
    }

    public int getWeb_links_external_unique() {
        return web_links_external_unique;
    }

    public void setWeb_links_external_unique(int web_links_external_unique) {
        this.web_links_external_unique = web_links_external_unique;
    }

    public Object getSite_language() {
        return site_language;
    }

    public void setSite_language(Object site_language) {
        this.site_language = site_language;
    }

    public int getSite_status() {
        return site_status;
    }

    public void setSite_status(int site_status) {
        this.site_status = site_status;
    }

    public int getBacklinks() {
        return backlinks;
    }

    public void setBacklinks(int backlinks) {
        this.backlinks = backlinks;
    }

    public int getBacklinks_unique() {
        return backlinks_unique;
    }

    public void setBacklinks_unique(int backlinks_unique) {
        this.backlinks_unique = backlinks_unique;
    }

    public int getBacklink_ips() {
        return backlink_ips;
    }

    public void setBacklink_ips(int backlink_ips) {
        this.backlink_ips = backlink_ips;
    }

    public int getBacklink_ips_unique() {
        return backlink_ips_unique;
    }

    public void setBacklink_ips_unique(int backlink_ips_unique) {
        this.backlink_ips_unique = backlink_ips_unique;
    }

    public int getBacklink_subnets() {
        return backlink_subnets;
    }

    public void setBacklink_subnets(int backlink_subnets) {
        this.backlink_subnets = backlink_subnets;
    }

    public int getBacklink_subnets_unique() {
        return backlink_subnets_unique;
    }

    public void setBacklink_subnets_unique(int backlink_subnets_unique) {
        this.backlink_subnets_unique = backlink_subnets_unique;
    }

    public String getKeyword_broad_keyword() {
        return keyword_broad_keyword;
    }

    public void setKeyword_broad_keyword(String keyword_broad_keyword) {
        this.keyword_broad_keyword = keyword_broad_keyword;
    }

    public String getKeyword_broad_type() {
        return keyword_broad_type;
    }

    public void setKeyword_broad_type(String keyword_broad_type) {
        this.keyword_broad_type = keyword_broad_type;
    }

    public String getKeyword_broad_competition() {
        return keyword_broad_competition;
    }

    public void setKeyword_broad_competition(String keyword_broad_competition) {
        this.keyword_broad_competition = keyword_broad_competition;
    }

    public Float getKeyword_broad_cpc() {
        return keyword_broad_cpc;
    }

    public void setKeyword_broad_cpc(Float keyword_broad_cpc) {
        this.keyword_broad_cpc = keyword_broad_cpc;
    }

    public int getKeyword_broad_global_search_volume() {
        return keyword_broad_global_search_volume;
    }

    public void setKeyword_broad_global_search_volume(int keyword_broad_global_search_volume) {
        this.keyword_broad_global_search_volume = keyword_broad_global_search_volume;
    }

    public int getKeyword_broad_local_search_volume() {
        return keyword_broad_local_search_volume;
    }

    public void setKeyword_broad_local_search_volume(int keyword_broad_local_search_volume) {
        this.keyword_broad_local_search_volume = keyword_broad_local_search_volume;
    }

    public int getKeyword_broad_month() {
        return keyword_broad_month;
    }

    public void setKeyword_broad_month(int keyword_broad_month) {
        this.keyword_broad_month = keyword_broad_month;
    }

    public String getKeyword_broad_is_ng() {
        return keyword_broad_is_ng;
    }

    public void setKeyword_broad_is_ng(String keyword_broad_is_ng) {
        this.keyword_broad_is_ng = keyword_broad_is_ng;
    }

    public String getKeyword_exact_keyword() {
        return keyword_exact_keyword;
    }

    public void setKeyword_exact_keyword(String keyword_exact_keyword) {
        this.keyword_exact_keyword = keyword_exact_keyword;
    }

    public String getKeyword_exact_type() {
        return keyword_exact_type;
    }

    public void setKeyword_exact_type(String keyword_exact_type) {
        this.keyword_exact_type = keyword_exact_type;
    }

    public String getKeyword_exact_competition() {
        return keyword_exact_competition;
    }

    public void setKeyword_exact_competition(String keyword_exact_competition) {
        this.keyword_exact_competition = keyword_exact_competition;
    }

    public Float getKeyword_exact_cpc() {
        return keyword_exact_cpc;
    }

    public void setKeyword_exact_cpc(Float keyword_exact_cpc) {
        this.keyword_exact_cpc = keyword_exact_cpc;
    }

    public int getKeyword_exact_global_search_volume() {
        return keyword_exact_global_search_volume;
    }

    public void setKeyword_exact_global_search_volume(int keyword_exact_global_search_volume) {
        this.keyword_exact_global_search_volume = keyword_exact_global_search_volume;
    }

    public int getKeyword_exact_local_search_volume() {
        return keyword_exact_local_search_volume;
    }

    public void setKeyword_exact_local_search_volume(int keyword_exact_local_search_volume) {
        this.keyword_exact_local_search_volume = keyword_exact_local_search_volume;
    }

    public int getKeyword_exact_month() {
        return keyword_exact_month;
    }

    public void setKeyword_exact_month(int keyword_exact_month) {
        this.keyword_exact_month = keyword_exact_month;
    }

    public String getKeyword_exact_is_ng() {
        return keyword_exact_is_ng;
    }

    public void setKeyword_exact_is_ng(String keyword_exact_is_ng) {
        this.keyword_exact_is_ng = keyword_exact_is_ng;
    }

    public String getKeyword_ng_broad_keyword() {
        return keyword_ng_broad_keyword;
    }

    public void setKeyword_ng_broad_keyword(String keyword_ng_broad_keyword) {
        this.keyword_ng_broad_keyword = keyword_ng_broad_keyword;
    }

    public String getKeyword_ng_broad_type() {
        return keyword_ng_broad_type;
    }

    public void setKeyword_ng_broad_type(String keyword_ng_broad_type) {
        this.keyword_ng_broad_type = keyword_ng_broad_type;
    }

    public String getKeyword_ng_broad_competition() {
        return keyword_ng_broad_competition;
    }

    public void setKeyword_ng_broad_competition(String keyword_ng_broad_competition) {
        this.keyword_ng_broad_competition = keyword_ng_broad_competition;
    }

    public Float getKeyword_ng_broad_cpc() {
        return keyword_ng_broad_cpc;
    }

    public void setKeyword_ng_broad_cpc(Float keyword_ng_broad_cpc) {
        this.keyword_ng_broad_cpc = keyword_ng_broad_cpc;
    }

    public int getKeyword_ng_broad_global_search_volume() {
        return keyword_ng_broad_global_search_volume;
    }

    public void setKeyword_ng_broad_global_search_volume(int keyword_ng_broad_global_search_volume) {
        this.keyword_ng_broad_global_search_volume = keyword_ng_broad_global_search_volume;
    }

    public int getKeyword_ng_broad_local_search_volume() {
        return keyword_ng_broad_local_search_volume;
    }

    public void setKeyword_ng_broad_local_search_volume(int keyword_ng_broad_local_search_volume) {
        this.keyword_ng_broad_local_search_volume = keyword_ng_broad_local_search_volume;
    }

    public int getKeyword_ng_broad_month() {
        return keyword_ng_broad_month;
    }

    public void setKeyword_ng_broad_month(int keyword_ng_broad_month) {
        this.keyword_ng_broad_month = keyword_ng_broad_month;
    }

    public String getKeyword_ng_broad_is_ng() {
        return keyword_ng_broad_is_ng;
    }

    public void setKeyword_ng_broad_is_ng(String keyword_ng_broad_is_ng) {
        this.keyword_ng_broad_is_ng = keyword_ng_broad_is_ng;
    }

    public String getKeyword_ng_exact_keyword() {
        return keyword_ng_exact_keyword;
    }

    public void setKeyword_ng_exact_keyword(String keyword_ng_exact_keyword) {
        this.keyword_ng_exact_keyword = keyword_ng_exact_keyword;
    }

    public String getKeyword_ng_exact_type() {
        return keyword_ng_exact_type;
    }

    public void setKeyword_ng_exact_type(String keyword_ng_exact_type) {
        this.keyword_ng_exact_type = keyword_ng_exact_type;
    }

    public String getKeyword_ng_exact_competition() {
        return keyword_ng_exact_competition;
    }

    public void setKeyword_ng_exact_competition(String keyword_ng_exact_competition) {
        this.keyword_ng_exact_competition = keyword_ng_exact_competition;
    }

    public Float getKeyword_ng_exact_cpc() {
        return keyword_ng_exact_cpc;
    }

    public void setKeyword_ng_exact_cpc(Float keyword_ng_exact_cpc) {
        this.keyword_ng_exact_cpc = keyword_ng_exact_cpc;
    }

    public int getKeyword_ng_exact_global_search_volume() {
        return keyword_ng_exact_global_search_volume;
    }

    public void setKeyword_ng_exact_global_search_volume(int keyword_ng_exact_global_search_volume) {
        this.keyword_ng_exact_global_search_volume = keyword_ng_exact_global_search_volume;
    }

    public int getKeyword_ng_exact_local_search_volume() {
        return keyword_ng_exact_local_search_volume;
    }

    public void setKeyword_ng_exact_local_search_volume(int keyword_ng_exact_local_search_volume) {
        this.keyword_ng_exact_local_search_volume = keyword_ng_exact_local_search_volume;
    }

    public int getKeyword_ng_exact_month() {
        return keyword_ng_exact_month;
    }

    public void setKeyword_ng_exact_month(int keyword_ng_exact_month) {
        this.keyword_ng_exact_month = keyword_ng_exact_month;
    }

    public String getKeyword_ng_exact_is_ng() {
        return keyword_ng_exact_is_ng;
    }

    public void setKeyword_ng_exact_is_ng(String keyword_ng_exact_is_ng) {
        this.keyword_ng_exact_is_ng = keyword_ng_exact_is_ng;
    }
}
