package kr.junhyung.mainframe.core.util

public open class IntKeyProvider(override val prefix: String) : IdentifiedKeyProvider<Int> {

    override fun getKey(identifier: Int): String {
        return "$prefix$identifier"
    }

    override fun extractIdentifier(key: String): Int {
        return key.removePrefix(prefix).toInt()
    }

}