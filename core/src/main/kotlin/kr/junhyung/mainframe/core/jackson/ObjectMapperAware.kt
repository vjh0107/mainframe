package kr.junhyung.mainframe.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper

public interface ObjectMapperAware {

    public fun setObjectMapper(objectMapperAware: ObjectMapper)

}