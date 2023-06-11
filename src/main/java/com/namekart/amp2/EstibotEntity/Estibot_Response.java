package com.namekart.amp2.EstibotEntity;

import java.util.List;

public class Estibot_Response {

        public boolean success;
        //public int bulk;
        public int item_count;
        public int release_socket;
        public Estibot_Results results;

        List<String> not_found;

    public List<String> getNot_found() {
        return not_found;
    }

    public void setNot_found(List<String> not_found) {
        this.not_found = not_found;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /*public int getBulk() {
        return bulk;
    }

    public void setBulk(int bulk) {
        this.bulk = bulk;
    }
*/
    public int getItem_count() {
        return item_count;
    }

    public void setItem_count(int item_count) {
        this.item_count = item_count;
    }

    public int getRelease_socket() {
        return release_socket;
    }

    public void setRelease_socket(int release_socket) {
        this.release_socket = release_socket;
    }

    public Estibot_Results getResults() {
        return results;
    }

    public void setResults(Estibot_Results results) {
        this.results = results;
    }
}
