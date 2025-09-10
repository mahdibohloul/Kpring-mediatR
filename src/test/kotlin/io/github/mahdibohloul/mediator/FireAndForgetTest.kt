package io.github.mahdibohloul.mediator

import io.github.mahdibohloul.mediator.builder.MediatorBuilder
import io.github.mahdibohloul.mediator.mock.FirstNotificationMockHandler
import io.github.mahdibohloul.mediator.mock.FourthNotificationMockHandler
import io.github.mahdibohloul.mediator.mock.HelloMockRequestHandler
import io.github.mahdibohloul.mediator.mock.LoggerMockCommandHandler
import io.github.mahdibohloul.mediator.mock.MockNotificationExceptionHandler
import io.github.mahdibohloul.mediator.mock.NotificationMock
import io.github.mahdibohloul.mediator.mock.SecondNotificationMockHandler
import io.github.mahdibohloul.mediator.mock.SlowNotificationMockHandler
import io.github.mahdibohloul.mediator.mock.ThirdNotificationMockHandler
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(
  classes = [
    ApplicationContext::class, HelloMockRequestHandler::class, LoggerMockCommandHandler::class,
    FirstNotificationMockHandler::class, SecondNotificationMockHandler::class, ThirdNotificationMockHandler::class,
    FourthNotificationMockHandler::class, MockNotificationExceptionHandler::class, SlowNotificationMockHandler::class,
  ],
)
class FireAndForgetTest {

  @Autowired
  private lateinit var applicationContext: ApplicationContext

  private lateinit var mediator: Mediator

  @BeforeEach
  fun init() {
    mediator = MediatorBuilder(applicationContext).build()
  }

  @Test
  fun `publishAsync should be fire-and-forget in reactive context`() {
    val startTime = System.currentTimeMillis()

    StepVerifier.create(
      Mono.fromCallable { "initial" }
        .flatMap { value ->
          // This should return immediately, not wait for handlers
          mono {
            mediator.publishAsync(NotificationMock())
          }.thenReturn(value)
        },
    )
      .expectNext("initial")
      .verifyComplete()

    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime

    // If it's truly fire-and-forget, this should complete very quickly (< 100ms)
    // If it's waiting for handlers, it would take much longer
    assertTrue(duration < 100, "publishAsync took ${duration}ms, should be < 100ms for fire-and-forget")
  }

  @Test
  fun `publishAsync should not block reactive stream execution`() {
    val executionOrder = mutableListOf<String>()

    StepVerifier.create(
      Mono.fromCallable {
        executionOrder.add("before-publish")
        "data"
      }
        .flatMap { data ->
          mono {
            executionOrder.add("publishing")
            mediator.publishAsync(NotificationMock())
            executionOrder.add("published")
          }.thenReturn(data)
        }
        .doOnNext {
          executionOrder.add("after-publish")
        },
    )
      .expectNext("data")
      .verifyComplete()

    // Verify the execution order - "after-publish" should come immediately after "published"
    // If handlers were blocking, there would be a delay
    assertEquals(
      listOf("before-publish", "publishing", "published", "after-publish"),
      executionOrder,
    )
  }

  @Test
  fun `publishAsync should start handlers but not wait for completion`() {
    val startTime = System.currentTimeMillis()

    StepVerifier.create(
      Mono.fromCallable { "test" }
        .flatMap { value ->
          mono {
            // This will trigger SlowNotificationMockHandler which takes 5 seconds
            mediator.publishAsync(NotificationMock())
          }.thenReturn(value)
        },
    )
      .expectNext("test")
      .verifyComplete()

    val totalDuration = System.currentTimeMillis() - startTime

    // The publish should complete quickly (< 100ms) even though SlowNotificationMockHandler takes 5 seconds
    assertTrue(
      totalDuration < 100,
      "Publish should complete quickly (< 100ms), not wait for 5-second handler. Took ${totalDuration}ms",
    )
  }

  @Test
  fun `publishAsync with 5-second handler should return immediately in reactive chain`() {
    val startTime = System.currentTimeMillis()

    StepVerifier.create(
      Mono.fromCallable { "initial" }
        .flatMap { value ->
          mono {
            // This will trigger SlowNotificationMockHandler (5 seconds) + other handlers
            mediator.publishAsync(NotificationMock())
          }.thenReturn(value)
        }
        .doOnNext {
          val endTime = System.currentTimeMillis()
          val duration = endTime - startTime
          println("Reactive chain completed in ${duration}ms")
        },
    )
      .expectNext("initial")
      .verifyComplete()

    val totalDuration = System.currentTimeMillis() - startTime

    // If it's truly fire-and-forget, this should complete in < 1 second
    // If it's waiting for handlers, it would take ~5+ seconds
    assertTrue(
      totalDuration < 1000,
      "Reactive chain took ${totalDuration}ms, should be < 1000ms for fire-and-forget. " +
        "If it took ~5000ms, then it's waiting for the slow handler to complete.",
    )
  }
}
