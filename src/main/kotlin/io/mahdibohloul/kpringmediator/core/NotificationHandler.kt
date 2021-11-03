package io.mahdibohloul.kpringmediator.core

/**
 * @author Mahdi Bohloul
 * @param TNotification the type of event to be emitted
 */
interface NotificationHandler<in TNotification : Notification> {

    /**
     * @author Mahdi Bohloul
     * @param event the type of emitted event
     */
    suspend fun handle(event: TNotification)
}