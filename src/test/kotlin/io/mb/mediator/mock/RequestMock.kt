package io.mb.mediator.mock

import io.mb.mediator.interfaces.Request
import io.mb.mediator.interfaces.RequestHandler
import org.springframework.stereotype.Component

class HelloRequestMock : Request<String>

@Component
class HelloRequestHandlerMock : RequestHandler<HelloRequestMock, String> {
    override suspend fun handle(request: HelloRequestMock): String {
        return "hello"
    }
}