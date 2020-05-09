package micronaut.bookman.infra

import micronaut.bookman.infra.error.InfraException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.joda.time.DateTimeZone
import java.lang.IllegalStateException
import java.sql.SQLException

interface DBRepositoryTrait {
    // DB関連の既知の例外を処理する
    fun <T> catchingKnownException(statement: Unit.() -> T): T {
        return try {
            statement(Unit)
        } catch (e: SQLException) {
            // データベースの制約を満たしていない場合などに起きるため
            throw e;//InfraException(e)
        } catch (e: ExposedSQLException) {
            // データベースの制約を満たしていない場合などに起きるため
            throw InfraException(e)
        }
    }
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