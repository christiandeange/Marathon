package com.deange.marathonapp.utils;

import android.util.Log;

import java.util.LinkedList;
import java.util.NoSuchElementException;

// Represents a queue of Runnables that can be executed at a later time.
// Useful for time-invariant processes that depend on a particular configuration, which is
// not yet available.
public class ProcessList {

    private static final String TAG = ProcessList.class.getSimpleName();
    private final LinkedList<Runnable> mProcesses;

    public ProcessList() {
        mProcesses = new LinkedList<Runnable>();
    }

    public void admitProcess(final Runnable process) {
        // Add a new Runnable to the queue
        mProcesses.addLast(process);
    }

    public void process() {

        try {
            // Remove the oldest Runnable, and run it
            final Runnable nextProcess = mProcesses.pollFirst();

            if (nextProcess != null) {
                nextProcess.run();
            }

        } catch (final NoSuchElementException e) {
            Log.w(TAG, "No processes left!");
        }

    }

    public void processAll() {
        // Flush out the entire Runnable queue, running each one on the way
        while (!mProcesses.isEmpty()) {
            process();
        }
    }

    public void clear() {
        // Flush out the entire Runnable queue, without running them
        mProcesses.clear();
    }

    public boolean isEmpty() {
        // Delegate isEmpty to the queue
        return mProcesses.isEmpty();
    }

}
