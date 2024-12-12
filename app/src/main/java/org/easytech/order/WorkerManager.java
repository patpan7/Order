package org.easytech.order;

import android.content.Context;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkerManager {

    public static void scheduleAppWorker(Context context) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(PrintOrderWorker.class)
                .setInitialDelay(0, TimeUnit.SECONDS) // Άμεση εκκίνηση
                .build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
