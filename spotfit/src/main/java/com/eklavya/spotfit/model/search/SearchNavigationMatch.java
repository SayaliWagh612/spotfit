package com.eklavya.spotfit.model.search;

import java.util.List;

public class SearchNavigationMatch {

    public enum ResultType {
        DOMAIN,
        ACTIVITY_GROUP,
        ACTIVITY
    }

    private final ResultType resultType;
    private final String canonicalDomain;
    private final String subdomainOrGroup;
    private final String activityName;
    private final String displayLabel;
    private final boolean prototype;
    private final List<String> aliases;

    public SearchNavigationMatch(ResultType resultType, String canonicalDomain, String subdomainOrGroup,
            String activityName, String displayLabel, boolean prototype, List<String> aliases) {
        this.resultType = resultType;
        this.canonicalDomain = canonicalDomain;
        this.subdomainOrGroup = subdomainOrGroup;
        this.activityName = activityName;
        this.displayLabel = displayLabel;
        this.prototype = prototype;
        this.aliases = aliases;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public String getCanonicalDomain() {
        return canonicalDomain;
    }

    public String getSubdomainOrGroup() {
        return subdomainOrGroup;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public boolean isPrototype() {
        return prototype;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
