package com.deange.marathonapp.controller;

import android.content.Context;

import com.deange.marathonapp.R;
import com.deange.marathonapp.google.BaseGameActivity;
import com.deange.marathonapp.google.GoogleClients;
import com.deange.marathonapp.ui.MarathonFragment;
import com.deange.marathonapp.ui.view.MarathonView;
import com.google.android.gms.games.GamesClient;

import java.util.HashSet;
import java.util.Set;

/**
 * A handler to determine if an achievement has been unlocked
 */
public final class AchievementsController {

    private static final int MILE_MARATHON = 23;
    private static final int MILE_500 = 500;
    private static final int MILE_500_MORE = 1000;
    private static final int MILE_NILE_RIVER = 4132;
    private static final int MILE_MOON = 6786;
    private static final int MILE_GREAT_WALL_OF_CHINA = 13171;
    private static final int MILE_100_LIGHT_MS = 18600;
    private static final int MILE_THIS_IS_THE_END = MarathonView.TOTAL_MILES;

    private Context mContext;
    private static final Object sLock = new Object();
    private static AchievementsController sInstance;

    // Micro-optimization so that we are not submitting achievements every time
    private Set<Integer> mAchievements = new HashSet<Integer>();

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

    public void notifyMileRan(final BaseGameActivity activity, final int mile) {

        synchronized (sLock) {

            final GamesClient client = activity.getGamesClient();
            if (client.isConnected()) {

                // Check for all the achievements that can be unlocked
                if (shouldUnlock(mile, MILE_THIS_IS_THE_END)) {
                    unlock(client, R.string.achievement_this_is_the_end, MILE_THIS_IS_THE_END);

                } else if (shouldUnlock(mile, MILE_100_LIGHT_MS)) {
                    unlock(client, R.string.achievement_buzz_lighthundredmilliseconds, MILE_100_LIGHT_MS);

                } else if (shouldUnlock(mile, MILE_GREAT_WALL_OF_CHINA)) {
                    unlock(client, R.string.achievement_walk_the_wall, MILE_GREAT_WALL_OF_CHINA);

                } else if (shouldUnlock(mile, MILE_MOON)) {
                    unlock(client, R.string.achievement_one_giant_marathon_for_mankind, MILE_MOON);

                } else if (shouldUnlock(mile, MILE_NILE_RIVER)) {
                    unlock(client, R.string.achievement_walk_the_nile, MILE_NILE_RIVER);

                } else if (shouldUnlock(mile, MILE_500_MORE)) {
                    unlock(client, R.string.achievement_and_i_would_walk_500_more, MILE_500_MORE);

                } else if (shouldUnlock(mile, MILE_500)) {
                    unlock(client, R.string.achievement_i_would_walk_500_miles, MILE_500);

                } else if (shouldUnlock(mile, MILE_MARATHON)) {
                    unlock(client, R.string.achievement_marathon_man, MILE_MARATHON);
                }

                // Submit score to leaderboards
                // Throttled to deter rate-limiting from the network
                final long now = System.currentTimeMillis();
                if ((now - mLastLeaderboardSubmission >= LEADERBOARD_SUBMISSION_INTERVAL)) {
                    mLastLeaderboardSubmission = System.currentTimeMillis();
                    submit(mile);
                }
            }
        }

    }

    public void notifyLeaderBoardImmediate(final int mile) {
        synchronized (sLock) {
            // Bypass the time-locking system to deter rate-limiting
            submit(mile);
        }
    }

    private boolean shouldUnlock(final int miles, final int achievementLimit) {
        return (miles >= achievementLimit) && (!mAchievements.contains(achievementLimit));
    }

    private void unlock(final GamesClient client, final int achievementResId, final int achievementLimit) {
        final String achievement = mContext.getString(achievementResId);
        mAchievements.add(achievementLimit);

        if (client.isConnected()) {
            client.unlockAchievement(achievement);
        }
    }

    private void submit(final int mile) {
        final GamesClient client = GoogleClients.getInstance().getGamesClient();
        if (client.isConnected()) {
            client.submitScore(mContext.getString(R.string.leaderboard_total_distance_ran), mile);
        }
    }

}
