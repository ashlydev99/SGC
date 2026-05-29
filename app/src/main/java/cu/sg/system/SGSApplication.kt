package cu.sg.system

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cu.sg.system.data.local.AppDatabase
import cu.sg.system.data.local.UserPreferences
import cu.sg.system.util.NotificationWorker
import java.util.concurrent.TimeUnit

class SGCApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val userPreferences: UserPreferences by lazy { UserPreferences(this) }
    
    override fun onCreate() {
        super.onCreate()
        scheduleNotificationWorker()
    }
    
    private fun scheduleNotificationWorker() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "payment_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}