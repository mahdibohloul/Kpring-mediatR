package io.github.mahdibohloul.mediator

import io.github.mahdibohloul.mediator.dispatcher.Dispatcher
import io.github.mahdibohloul.mediator.publisher.Publisher

/**
 * Defines a mediator to encapsulate sending and publishing mediator patterns.
 *
 *
 * @author Mahdi Bohloul
 */
interface Mediator: Dispatcher, Publisher
