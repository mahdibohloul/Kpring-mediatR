package io.github.mahdibohloul.mediator.request

/**
 * Interface for handling requests and returning typed responses.
 *
 * RequestHandler is responsible for processing a specific type of [Request] and
 * returning a corresponding response. Each request type can have only one handler
 * registered, ensuring a clear single responsibility for each request.
 *
 * ## Key Characteristics:
 * - **Single Responsibility**: One handler per request type
 * - **Asynchronous**: Uses suspend functions for non-blocking operations
 * - **Type Safe**: Strongly typed request and response parameters
 * - **Spring Integration**: Should be annotated with @Component for auto-discovery
 *
 * ## Implementation Guidelines:
 * - Keep handlers focused on a single responsibility
 * - Use dependency injection for required services
 * - Handle exceptions appropriately (they will propagate to the caller)
 * - Consider using repositories or services for data access
 *
 * ## Usage Example:
 * ```kotlin
 * @Component
 * class GetUserRequestHandler(
 *     private val userRepository: UserRepository
 * ) : RequestHandler<GetUserRequest, User?> {
 *
 *     override suspend fun handle(request: GetUserRequest): User? {
 *         return try {
 *             userRepository.findById(request.userId)
 *         } catch (e: Exception) {
 *             logger.error("Failed to get user ${request.userId}", e)
 *             throw UserNotFoundException("User not found: ${request.userId}")
 *         }
 *     }
 * }
 * ```
 *
 * @param TRequest The type of request this handler processes
 * @param TResponse The type of response this handler returns
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see Request for the request interface
 * @see Mediator.sendAsync for sending requests
 */
interface RequestHandler<in TRequest : Request<TResponse>, TResponse> {

  /**
   * Handles the specified request and returns a response.
   *
   * This method is called by the mediator when a request of type [TRequest] is sent.
   * The implementation should process the request and return the appropriate response.
   *
   * @param request The request to handle
   * @return The response for the request
   * @throws Exception Any exception thrown will be propagated to the caller
   * @author Mahdi Bohloul
   */
  suspend fun handle(request: TRequest): TResponse
}
