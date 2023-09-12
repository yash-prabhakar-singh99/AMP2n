package com.namekart.amp2.DotDBEntity;

import java.util.ArrayList;

public class DdParameter {
    public ArrayList<String> keywords;
    public String position;
    public Object keyword_exclude;
    public Object site_status;
    public boolean count_sorting;
    public int page;

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Object getKeyword_exclude() {
        return keyword_exclude;
    }

    public void setKeyword_exclude(Object keyword_exclude) {
        this.keyword_exclude = keyword_exclude;
    }

    public Object getSite_status() {
        return site_status;
    }

    public void setSite_status(Object site_status) {
        this.site_status = site_status;
    }

    public boolean isCount_sorting() {
        return count_sorting;
    }

    public void setCount_sorting(boolean count_sorting) {
        this.count_sorting = count_sorting;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
