package io.kpring.mediator.mock

import io.kpring.mediator.core.Request
import io.kpring.mediator.core.RequestHandler
import org.springframework.stereotype.Component

class HelloMockRequest : Request<String>

@Component
class HelloMockRequestHandler : RequestHandler<HelloMockRequest, String> {
    override suspend fun handle(request: HelloMockRequest): String {
        return "hello"
    }
}