package io.github.mahdibohloul.kpringmediator.mock

import io.github.mahdibohloul.kpringmediator.core.Request
import io.github.mahdibohloul.kpringmediator.core.RequestHandler
import org.springframework.stereotype.Component

class HelloMockRequest : Request<String>

@Component
class HelloMockRequestHandler : RequestHandler<HelloMockRequest, String> {
    override suspend fun handle(request: HelloMockRequest): String {
        return "hello"
    }
}