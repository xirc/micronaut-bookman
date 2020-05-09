package micronaut.bookman.infra

import org.joda.time.DateTimeZone

interface DBRepositoryTrait {
    // 日付関係を扱う場合にUTCを標準タイムゾーンにする
    // DateTimeをDBに保存する際に、アプリケーションで処理しないと不整合が起きるため
    // 例えばアプリケーションのサーバごとにデフォルトタイムゾーンが違うなど
    fun <T> withUtcZone(statement: Unit.() -> T): T {
        val defaultZone = DateTimeZone.getDefault()
        DateTimeZone.setDefault(DateTimeZone.UTC)
        val result = statement(Unit)
        DateTimeZone.setDefault(defaultZone)
        return result
    }
}