package micronaut.bookman.controller.person

import io.micronaut.http.client.annotation.Client
import micronaut.bookman.controller.person.PersonsApi

@Client("/persons")
interface PersonsClient : PersonsApi