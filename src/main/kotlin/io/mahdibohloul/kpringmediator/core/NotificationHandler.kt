package io.mahdibohloul.kpringmediator.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * @author Mahdi Bohloul
 * @param TNotification the type of notification to be published
 */
interface NotificationHandler<in TNotification : Notification> {

    /**
     * @author Mahdi Bohloul
     * @param notification the type of published notification
     */
    suspend fun handle(notification: TNotification)

    /**
     * Specify the coroutine dispatcher to be used for handling the notification
     * @author Mahdi Bohloul
     * @default Dispatchers.Default
     */
    fun getCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }
}