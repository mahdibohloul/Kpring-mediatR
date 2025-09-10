package io.github.mahdibohloul.mediator.dispatcher

import io.github.mahdibohloul.mediator.request.Request

interface RequestDispatcher {
  suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse
}
