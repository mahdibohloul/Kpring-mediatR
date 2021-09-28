package io.mb.mediator.interfaces.request

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
    fun handle(request: TRequest): TResponse
}
