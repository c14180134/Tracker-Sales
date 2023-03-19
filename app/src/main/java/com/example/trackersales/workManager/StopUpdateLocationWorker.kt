package com.example.trackersales.workManager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.trackersales.service.LocationServiceCoba
import java.lang.Exception

class StopUpdateLocationWorker(context: Context, params: WorkerParameters) :
    UpdateLocationWorker(context, params) {
    override fun doWork(): Result {
        try{
            stopLocationService()
            return Result.success()
        }catch(e: Exception) {
            return Result.failure()
        }
    }
}