package io.kpring.mediator.core

/**
 * @author Mahdi Bohloul
 * @param TResponse the type of the response
 * @param TRequest the type of the request to be handled
 */

interface RequestHandler<in TRequest : Request<TResponse>, TResponse> {

    /**
     * @author Mahdi Bohloul
     * @param request request to handle
     * @return the response of the request
     */
    suspend fun handle(request: TRequest): TResponse
}
