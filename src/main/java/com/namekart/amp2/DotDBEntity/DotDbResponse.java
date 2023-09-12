package com.namekart.amp2.DotDBEntity;

import java.util.ArrayList;

public class DotDbResponse {
    public DdParameter parameters;
    public int items_per_page;
    public boolean estimate_total;
    public int exact_match_total_suffix;
    public int total_name;
    public int total_suffix;
    public ArrayList<DdMatch> matches;

    public DdParameter getParameters() {
        return parameters;
    }

    public void setParameters(DdParameter parameters) {
        this.parameters = parameters;
    }

    public int getItems_per_page() {
        return items_per_page;
    }

    public void setItems_per_page(int items_per_page) {
        this.items_per_page = items_per_page;
    }

    public boolean isEstimate_total() {
        return estimate_total;
    }

    public void setEstimate_total(boolean estimate_total) {
        this.estimate_total = estimate_total;
    }

    public int getExact_match_total_suffix() {
        return exact_match_total_suffix;
    }

    public void setExact_match_total_suffix(int exact_match_total_suffix) {
        this.exact_match_total_suffix = exact_match_total_suffix;
    }

    public int getTotal_name() {
        return total_name;
    }

    public void setTotal_name(int total_name) {
        this.total_name = total_name;
    }

    public int getTotal_suffix() {
        return total_suffix;
    }

    public void setTotal_suffix(int total_suffix) {
        this.total_suffix = total_suffix;
    }

    public ArrayList<DdMatch> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<DdMatch> matches) {
        this.matches = matches;
    }
}
