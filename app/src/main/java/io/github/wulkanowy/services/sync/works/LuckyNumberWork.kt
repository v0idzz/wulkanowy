package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.luckynumber.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.LuckyNumberChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import javax.inject.Inject
import kotlin.random.Random

class LuckyNumberWork @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return luckyNumberRepository.getLuckyNumber(student, true, preferencesRepository.isNotificationsEnable)
            .flatMap { luckyNumberRepository.getNotNotifiedLuckyNumber(student) }
            .flatMapCompletable {
                notify(it)
                luckyNumberRepository.updateLuckyNumber(it.apply { isNotified = true })
            }
    }

    private fun notify(luckyNumber: LuckyNumber) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), NotificationCompat.Builder(context, LuckyNumberChannel.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.lucky_number_notify_new_item_title))
            .setContentText(context.getString(R.string.lucky_number_notify_new_item, luckyNumber.luckyNumber))
            .setSmallIcon(R.drawable.ic_stat_luckynumber)
            .setAutoCancel(true)
            .setDefaults(DEFAULT_ALL)
            .setPriority(PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, MainView.Section.MESSAGE.id,
                    MainActivity.getStartIntent(context, MainView.Section.LUCKY_NUMBER, true), FLAG_UPDATE_CURRENT))
            .build())
    }
}
