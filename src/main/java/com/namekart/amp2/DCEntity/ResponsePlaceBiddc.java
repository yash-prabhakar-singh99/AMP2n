package com.namekart.amp2.DCEntity;

import java.util.List;

public class ResponsePlaceBiddc {
    List<BidPlacedSuccess> successes;
    List<BidPlacedFailure> failures;

    public ResponsePlaceBiddc() {
    }

    public List<BidPlacedSuccess> getSuccesses() {
        return successes;
    }

    public void setSuccesses(List<BidPlacedSuccess> successes) {
        this.successes = successes;
    }

    public List<BidPlacedFailure> getFailures() {
        return failures;
    }

    public void setFailures(List<BidPlacedFailure> failures) {
        this.failures = failures;
    }
}
