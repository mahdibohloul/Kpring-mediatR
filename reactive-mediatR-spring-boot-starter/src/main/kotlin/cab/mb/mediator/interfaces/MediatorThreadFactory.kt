package cab.mb.mediator.interfaces

import java.util.concurrent.ThreadFactory

class MediatorThreadFactory : ThreadFactory {

    private var counter: Int = 0

    override fun newThread(r: Runnable): Thread {
        counter++
        return Thread(r, "ReactiveMediator-$counter")
    }
}