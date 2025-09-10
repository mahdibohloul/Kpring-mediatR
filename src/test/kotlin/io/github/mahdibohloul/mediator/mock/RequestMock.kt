package io.github.mahdibohloul.mediator.mock

import io.github.mahdibohloul.mediator.request.Request
import io.github.mahdibohloul.mediator.request.RequestHandler
import org.springframework.stereotype.Component

class HelloMockRequest : Request<String>

@Component
class HelloMockRequestHandler : RequestHandler<HelloMockRequest, String> {
  override suspend fun handle(request: HelloMockRequest): String = "hello"
}
