package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.message.MessageFolder.RECEIVED
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.NewMessagesChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import javax.inject.Inject
import kotlin.random.Random

class MessageWork @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val messageRepository: MessageRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override fun create(student: Student, semester: Semester): Completable {
        return messageRepository.getMessages(student, semester, RECEIVED, true, preferencesRepository.isNotificationsEnable)
            .flatMap { messageRepository.getNotNotifiedMessages(student) }
            .flatMapCompletable {
                if (it.isNotEmpty()) notify(it)
                messageRepository.updateMessages(it.onEach { message -> message.isNotified = true })
            }
    }

    private fun notify(messages: List<Message>) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), NotificationCompat.Builder(context, NewMessagesChannel.CHANNEL_ID)
            .setContentTitle(context.resources.getQuantityString(R.plurals.message_new_items, messages.size, messages.size))
            .setContentText(context.resources.getQuantityString(R.plurals.message_notify_new_items, messages.size, messages.size))
            .setSmallIcon(R.drawable.ic_stat_message)
            .setAutoCancel(true)
            .setDefaults(DEFAULT_ALL)
            .setPriority(PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, MainView.Section.MESSAGE.id,
                    MainActivity.getStartIntent(context, MainView.Section.MESSAGE, true), FLAG_UPDATE_CURRENT)
            )
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.message_number_item, messages.size, messages.size))
                messages.forEach { addLine("${it.sender}: ${it.subject}") }
                this
            })
            .build())
    }
}
