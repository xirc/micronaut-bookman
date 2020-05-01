package micronaut.bookman

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("micronaut.bookman")
                .mainClass(Application.javaClass)
                .start()
    }
}