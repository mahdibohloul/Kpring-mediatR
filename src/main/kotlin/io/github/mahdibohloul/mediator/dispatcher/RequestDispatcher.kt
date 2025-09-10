package io.github.mahdibohloul.mediator.dispatcher

import io.github.mahdibohloul.mediator.request.Request

/**
 * Interface for dispatching requests through the mediator pattern.
 *
 * RequestDispatcher provides the functionality to send requests that expect typed responses.
 * Requests follow the Request-Response pattern where a single handler processes the request
 * and returns a result.
 *
 * ## Key Characteristics:
 * - **Typed Response**: Requests return strongly typed responses
 * - **Single Handler**: Only one handler can be registered per request type
 * - **Asynchronous**: Uses suspend functions for non-blocking execution
 * - **Type Safety**: Strongly typed request and response parameters
 *
 * ## Usage Example:
 * ```kotlin
 * class UserService(private val requestDispatcher: RequestDispatcher) {
 *     suspend fun getUser(userId: String): User? {
 *         return requestDispatcher.sendAsync(GetUserRequest(userId))
 *     }
 *
 *     suspend fun searchUsers(criteria: SearchCriteria): List<User> {
 *         return requestDispatcher.sendAsync(SearchUsersRequest(criteria))
 *     }
 * }
 * ```
 *
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Request for the request interface
 * @see RequestHandler for handling requests
 * @see Mediator for the main mediator interface
 */
interface RequestDispatcher {

  /**
   * Sends a request asynchronously through the mediator and returns the response.
   *
   * This method dispatches the specified request to its registered handler and
   * returns the typed response. The request is processed asynchronously.
   *
   * @param TRequest The type of request being sent
   * @param TResponse The type of response expected
   * @param request The request to send
   * @return The response from the request handler
   * @throws NoRequestHandlerException if no handler is registered for the request type
   * @throws Exception any exception thrown by the request handler will be propagated
   * @author Mahdi Bohloul
   */
  suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse
}
