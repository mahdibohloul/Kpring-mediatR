package io.mb.mediator

import io.mb.mediator.builder.MediatorBuilder
import io.mb.mediator.interfaces.Mediator
import io.mb.mediator.mock.HelloRequestHandlerMock
import io.mb.mediator.mock.HelloRequestMock
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.assertEquals

@SpringBootTest(classes = [ApplicationContext::class, HelloRequestHandlerMock::class])
class CommandHandlerTest {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    private lateinit var mediator: Mediator

    @BeforeEach
    fun init() {
        mediator = MediatorBuilder(applicationContext).build()
    }

    @Test
    fun `request handler should return hello as string`() {
        val request = HelloRequestMock()
        val result = runBlocking { mediator.sendAsync(request) }
        assertEquals("hello", result)
    }

}