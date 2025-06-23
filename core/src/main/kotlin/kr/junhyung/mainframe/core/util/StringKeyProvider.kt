package kr.junhyung.mainframe.core.util

public class StringKeyProvider(override val prefix: String) : IdentifiedKeyProvider<String> {

    override fun getKey(identifier: String): String {
        return "$prefix$identifier"
    }

    override fun extractIdentifier(key: String): String {
        return key.removePrefix(prefix)
    }

}