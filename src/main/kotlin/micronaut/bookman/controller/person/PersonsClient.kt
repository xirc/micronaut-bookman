package micronaut.bookman.controller.person

import io.micronaut.http.client.annotation.Client

@Client("/persons")
interface PersonsClient : PersonsApi