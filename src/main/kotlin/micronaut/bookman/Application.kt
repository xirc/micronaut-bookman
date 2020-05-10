package micronaut.bookman

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
        info = Info(
                title = "Book Management",
                version = "0.1",
                description = "Book Management API"
        )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("micronaut.bookman")
                .mainClass(Application.javaClass)
                .start()
    }
}