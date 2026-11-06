package com.eklavya.spotfit.model.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CustomerSearchCatalog {

    private static final List<SearchNavigationMatch> CATALOG = new ArrayList<>();

    static {
        // Domains
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.DOMAIN, "Creativity", null, null,
                "Creativity", false, Arrays.asList("creative", "creativity")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.DOMAIN, "Sports", null, null, "Sports",
                true, Arrays.asList("sport", "sports")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.DOMAIN, "Wellness", null, null,
                "Wellness", true, Arrays.asList("wellness", "fitness", "yoga")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.DOMAIN, "Workspaces", null, null,
                "Workspaces", true,
                Arrays.asList("workspace", "workspaces", "study", "coworking", "co-working", "study spaces")));

        // Creativity Activity Groups
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY_GROUP, "Creativity", "Art",
                null, "Art", false, Arrays.asList("art", "arts", "painting", "craft", "crafts", "crafting")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY_GROUP, "Creativity", "Music",
                null, "Music", false, Arrays.asList("music", "singing", "instruments")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY_GROUP, "Creativity", "Podcasts",
                null, "Podcasts", false, Arrays.asList("podcast", "podcasts", "podcasting")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY_GROUP, "Creativity",
                "Content Creation", null, "Content Creation", false, Arrays.asList("content", "content creation",
                        "creator", "photography", "videography", "video recording")));

        // Activities
        // Art matches
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Art",
                "Painting Studio", "Painting Studio", false, Arrays.asList("painting studio", "painting", "paint")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Art",
                "Pottery Studio", "Pottery Studio", false, Arrays.asList("pottery studio", "pottery", "ceramics")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Art",
                "Dance Studio", "Dance Studio", false, Arrays.asList("dance studio", "dance", "dancing")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Art",
                "Craft Workshop", "Craft Workshop", false,
                Arrays.asList("craft workshop", "craft workshops", "handicraft")));

        // Music matches
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Music",
                "Music Workshops", "Music Workshops", false,
                Arrays.asList("music workshop", "music workshops", "music lesson")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Music",
                "Recording Studios", "Recording Studios", false,
                Arrays.asList("recording studio", "recording studios", "audio recording")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Music",
                "Practice Rooms", "Practice Rooms", false,
                Arrays.asList("practice room", "practice rooms", "rehearsal room")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Music",
                "Performance Venues", "Performance Venues", false,
                Arrays.asList("performance venue", "performance venues", "concert venue", "concert")));

        // Podcasts
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity", "Podcasts",
                "Podcast Studios", "Podcast Studios", false,
                Arrays.asList("podcast studio", "podcast studios", "podcasting studio")));

        // Content Creation
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity",
                "Content Creation", "Photography Studios", "Photography Studios", false,
                Arrays.asList("photography", "photography studio", "photography studios", "photo studio")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity",
                "Content Creation", "Creator Studios", "Creator Studios", false,
                Arrays.asList("creator studio", "creator studios", "youtube studio", "vlog studio")));
        CATALOG.add(new SearchNavigationMatch(SearchNavigationMatch.ResultType.ACTIVITY, "Creativity",
                "Content Creation", "Camera Rental", "Camera Rental", false,
                Arrays.asList("camera rental", "camera rentals", "equipment rental")));
    }

    public static List<SearchNavigationMatch> getNavigationMatches(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String normalizedQuery = normalizeQuery(query);
        List<SearchNavigationMatch> matches = new ArrayList<>();

        for (SearchNavigationMatch item : CATALOG) {
            for (String alias : item.getAliases()) {
                if (normalizeQuery(alias).contains(normalizedQuery)) {
                    matches.add(item);
                    break; // Matched this item, don't duplicate
                }
            }
        }
        return matches;
    }

    public static List<SearchNavigationMatch> getSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String normalizedQuery = normalizeQuery(query);
        List<SearchNavigationMatch> suggestions = new ArrayList<>();
        int count = 0;

        // Prefer exact or prefix matches first for suggestions
        for (SearchNavigationMatch item : CATALOG) {
            if (count >= 8)
                break;
            for (String alias : item.getAliases()) {
                String normalizedAlias = normalizeQuery(alias);
                if (normalizedAlias.equals(normalizedQuery) || normalizedAlias.startsWith(normalizedQuery)) {
                    suggestions.add(item);
                    count++;
                    break;
                }
            }
        }

        // Fill up to 8 with contains matches if we don't have enough
        for (SearchNavigationMatch item : CATALOG) {
            if (count >= 8)
                break;
            if (suggestions.contains(item))
                continue;

            for (String alias : item.getAliases()) {
                String normalizedAlias = normalizeQuery(alias);
                if (normalizedAlias.contains(normalizedQuery)) {
                    suggestions.add(item);
                    count++;
                    break;
                }
            }
        }

        return suggestions;
    }

    public static String normalizeQuery(String query) {
        if (query == null)
            return "";
        // Trim leading/trailing whitespace, convert to lowercase ROOT
        String q = query.trim().toLowerCase(Locale.ROOT);
        // Treat hyphens and underscores as spaces
        q = q.replace("-", " ").replace("_", " ");
        // Collapse repeated whitespace
        q = q.replaceAll("\\s+", " ");
        return q;
    }
}
