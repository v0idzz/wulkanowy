package io.github.wulkanowy.services.sync

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationManagerCompat
import androidx.work.BackoffPolicy.EXPONENTIAL
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.SharedPrefProvider.Companion.APP_VERSION_CODE_KEY
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.Channel
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate.now
import timber.log.Timber
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager,
    private val preferencesRepository: PreferencesRepository,
    channels: Set<@JvmSuppressWildcards Channel>,
    notificationManager: NotificationManagerCompat,
    sharedPrefProvider: SharedPrefProvider,
    appInfo: AppInfo
) {

    init {
        if (now().isHolidays) stopSyncWorker()

        if (SDK_INT > O) {
            channels.forEach { it.create() }
            notificationManager.deleteNotificationChannel("new_entries_channel")
        }

        if (sharedPrefProvider.getLong(APP_VERSION_CODE_KEY, -1L) != appInfo.versionCode.toLong()) {
            startSyncWorker(true)
            sharedPrefProvider.putLong(APP_VERSION_CODE_KEY, appInfo.versionCode.toLong(), true)
        }
        Timber.i("SyncManager was initialized")
    }

    fun startSyncWorker(restart: Boolean = false) {
        if (preferencesRepository.isServiceEnabled && !now().isHolidays) {
            workManager.enqueueUniquePeriodicWork(SyncWorker::class.java.simpleName, if (restart) REPLACE else KEEP,
                PeriodicWorkRequestBuilder<SyncWorker>(preferencesRepository.servicesInterval, MINUTES)
                    .setInitialDelay(10, MINUTES)
                    .setBackoffCriteria(EXPONENTIAL, 30, MINUTES)
                    .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(if (preferencesRepository.isServicesOnlyWifi) UNMETERED else CONNECTED)
                        .build())
                    .build())
        }
    }

    fun stopSyncWorker() {
        workManager.cancelUniqueWork(SyncWorker::class.java.simpleName)
    }
}
