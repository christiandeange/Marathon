package com.deange.marathonapp.controller;

import android.content.Context;

import com.deange.marathonapp.R;
import com.google.android.gms.games.GamesClient;

/**
 * A handler to determine if an achievement has been unlocked
 */
public final class AchievementsController {

    private static final int MILE_MARATHON = 23;
    private static final int MILE_500 = 500;
    private static final int MILE_500_MORE = 1000;
    private static final int MILE_NILE_RIVER = 4132;
    private static final int MILE_MOON = 6786;

    private Context mContext;
    private static final Object sLock = new Object();
    private static AchievementsController sInstance;

    private long mLastLeaderboardSubmission;
    private long LEADERBOARD_SUBMISSION_INTERVAL = 5000;

    private AchievementsController(final Context context) {
        mContext = context;
    }

    public static AchievementsController getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                throw new IllegalStateException("AchievementsController has not been created");
            }
            return sInstance;
        }
    }

    public static synchronized void createInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new AchievementsController(context.getApplicationContext());
        }
    }

    public void notifyMileRan(final int mile) {

        // Check for all the achievements that can be unlocked
        if (shouldUnlock(mile, MILE_MOON)) {
            unlock(R.string.achievement_one_giant_marathon_for_mankind);

        } else if (shouldUnlock(mile, MILE_NILE_RIVER)) {
            unlock(R.string.achievement_walk_the_nile);

        } else if (shouldUnlock(mile, MILE_500_MORE)) {
            unlock(R.string.achievement_and_i_would_walk_500_more);

        } else if (shouldUnlock(mile, MILE_500)) {
            unlock(R.string.achievement_i_would_walk_500_miles);

        } else if (shouldUnlock(mile, MILE_MARATHON)) {
            unlock(R.string.achievement_marathon_man);
        }

        final long now = System.currentTimeMillis();
        if ((now - mLastLeaderboardSubmission >= LEADERBOARD_SUBMISSION_INTERVAL)) {
            mLastLeaderboardSubmission = now;

            final GamesClient client = GoogleClients.getInstance().getGamesClient();
            client.submitScore(mContext.getString(R.string.leaderboard_total_distance_ran), mile);
        }

    }

    private boolean shouldUnlock(final int miles, final int achievementLimit) {
        return miles >= achievementLimit;
    }

    private void unlock(final int achievementResId) {
        final GamesClient client = GoogleClients.getInstance().getGamesClient();
        final String achievement = mContext.getString(achievementResId);
        client.unlockAchievement(achievement);
    }

}
