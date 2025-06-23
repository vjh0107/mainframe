package kr.junhyung.mainframe.core.util

public interface IdentifiedKeyProvider<ID> {

    public val prefix: String

    public fun getKey(identifier: ID): String

    public fun extractIdentifier(key: String): ID

}