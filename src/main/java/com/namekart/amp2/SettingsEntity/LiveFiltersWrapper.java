package com.namekart.amp2.SettingsEntity;

import java.util.Set;

public class LiveFiltersWrapper {
    Boolean noHyphens,noNumbers;
    int[] domainLength,diff_exts_ests; String new_ests;
    Set<String> restrictedExts;

    public LiveFiltersWrapper(Boolean noHyphens, Boolean noNumbers, int[] domainLength, int[] diff_exts_ests, String new_ests, Set<String> restrictedExts) {
        this.noHyphens = noHyphens;
        this.noNumbers = noNumbers;
        this.domainLength = domainLength;
        this.diff_exts_ests = diff_exts_ests;
        this.new_ests = new_ests;
        this.restrictedExts = restrictedExts;
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

    public int[] getDomainLength() {
        return domainLength;
    }

    public void setDomainLength(int[] domainLength) {
        this.domainLength = domainLength;
    }

    public int[] getDiff_exts_ests() {
        return diff_exts_ests;
    }

    public void setDiff_exts_ests(int[] diff_exts_ests) {
        this.diff_exts_ests = diff_exts_ests;
    }

    public String getNew_ests() {
        return new_ests;
    }

    public void setNew_ests(String new_ests) {
        this.new_ests = new_ests;
    }

    public Set<String> getRestrictedExts() {
        return restrictedExts;
    }

    public void setRestrictedExts(Set<String> restrictedExts) {
        this.restrictedExts = restrictedExts;
    }

    public LiveFiltersWrapper() {
    }
}
