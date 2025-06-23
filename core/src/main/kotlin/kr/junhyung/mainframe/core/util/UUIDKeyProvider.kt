package kr.junhyung.mainframe.core.util

import java.util.UUID

public open class UUIDKeyProvider(override val prefix: String) : IdentifiedKeyProvider<UUID> {

    public override fun getKey(identifier: UUID): String {
        return "$prefix$identifier"
    }

    public override fun extractIdentifier(key: String): UUID {
        return UUID.fromString(key.removePrefix(prefix))
    }

}