package com.eklavya.spotfit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Application result object representing submission readiness assessment.
 * Not persisted to Firestore.
 */
public class SubmissionReadinessResult {

    private boolean ready;
    private List<String> missingRequirements;

    public SubmissionReadinessResult() {
        this.ready = false;
        this.missingRequirements = new ArrayList<>();
    }

    public SubmissionReadinessResult(boolean ready, List<String> missingRequirements) {
        this.ready = ready;
        this.missingRequirements = missingRequirements != null ? new ArrayList<>(missingRequirements) : new ArrayList<>();
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public List<String> getMissingRequirements() {
        if (missingRequirements == null) {
            missingRequirements = new ArrayList<>();
        }
        return missingRequirements;
    }

    public void setMissingRequirements(List<String> missingRequirements) {
        this.missingRequirements = missingRequirements != null ? new ArrayList<>(missingRequirements) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "SubmissionReadinessResult{" +
                "ready=" + ready +
                ", missingRequirements=" + missingRequirements +
                '}';
    }
}
