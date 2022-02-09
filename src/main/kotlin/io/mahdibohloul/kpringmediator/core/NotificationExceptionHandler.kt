package io.mahdibohloul.kpringmediator.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * This class is responsible for handling exceptions thrown during the handling published notification.
 * @author Mahdi Bohloul
 * @param TException the type of the exception that will be handled
 * @param TNotification the type of the notification that will be handled
 */
interface NotificationExceptionHandler<
    in TNotification : Notification,
    in TException : Exception> {
    suspend fun handle(notification: TNotification, exception: TException)

    fun getCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.Default
    }
}
