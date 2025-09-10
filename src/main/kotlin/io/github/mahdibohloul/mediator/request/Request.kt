package io.github.mahdibohloul.mediator.request

/**
 * Marker interface for a request that expects a response.
 *
 * The Request interface represents a message that is sent through the mediator
 * and expects a typed response. This follows the Request-Response pattern where
 * a single handler processes the request and returns a result.
 *
 * ## Key Characteristics:
 * - **Single Handler**: Only one handler can be registered per request type
 * - **Typed Response**: The response type is specified as a generic parameter
 * - **Immutable**: Request objects should be immutable data classes
 * - **Serializable**: Should be serializable for potential caching or persistence
 *
 * ## Usage Example:
 * ```kotlin
 * data class GetUserRequest(val userId: String) : Request<User?>
 *
 * @Component
 * class GetUserRequestHandler : RequestHandler<GetUserRequest, User?> {
 *     override suspend fun handle(request: GetUserRequest): User? {
 *         return userRepository.findById(request.userId)
 *     }
 * }
 *
 * // Usage in service
 * val user = mediator.sendAsync(GetUserRequest("123"))
 * ```
 *
 * @param TResponse The type of response expected from this request
 * @author Mahdi Bohloul
 * @since 2.0.0
 * @see RequestHandler for handling requests
 * @see Mediator.sendAsync for sending requests
 */
interface Request<out TResponse>
