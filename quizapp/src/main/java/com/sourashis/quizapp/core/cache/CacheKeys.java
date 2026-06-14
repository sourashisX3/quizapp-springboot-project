package com.sourashis.quizapp.core.cache;

public final class CacheKeys {

    private CacheKeys() {}

    public static final String USER_STATS = "user:stats:";
    public static final String LEADERBOARD = "leaderboard:";
    public static final String CONTEST = "contest:";
    public static final String ACTIVE_CONTESTS = "contests:active";
    public static final String RATE_LIMIT = "rate:limit:";

    public static String userStats(Long userId) { return USER_STATS + userId; }

    public static String leaderboard(String type, Long categoryId) { return LEADERBOARD + type + ":" + categoryId; }

    public static String contest(Long contestId) { return CONTEST + contestId; }

    public static String rateLimit(String key) { return RATE_LIMIT + key; }
}
