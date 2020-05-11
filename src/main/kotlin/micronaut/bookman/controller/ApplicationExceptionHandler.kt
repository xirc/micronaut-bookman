package micronaut.bookman.controller

import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import micronaut.bookman.controller.ApplicationExceptionSyntax.toResponseBody
import micronaut.bookman.exceptions.ApplicationException
import javax.inject.Singleton

@Produces
@Singleton
@Requirements(
        Requires(classes = [ApplicationException::class, ExceptionHandler::class])
)
class ApplicationExceptionHandler : ExceptionHandler<ApplicationException, HttpResponse<ResponseBody<*>>> {
    override fun handle(request: HttpRequest<*>?, exception: ApplicationException): HttpResponse<ResponseBody<*>> {
        return HttpResponse.ok(ResponseBody.failure<Any>(exception.toResponseBody()))
    }
}