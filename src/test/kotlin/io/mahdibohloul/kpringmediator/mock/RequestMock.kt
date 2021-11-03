package io.mahdibohloul.kpringmediator.mock

import io.mahdibohloul.kpringmediator.core.Request
import io.mahdibohloul.kpringmediator.core.RequestHandler
import org.springframework.stereotype.Component

class HelloMockRequest : Request<String>

@Component
class HelloMockRequestHandler : RequestHandler<HelloMockRequest, String> {
    override suspend fun handle(request: HelloMockRequest): String {
        return "hello"
    }
}