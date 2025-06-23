package kr.junhyung.mainframe.core

import org.springframework.boot.builder.SpringApplicationBuilder

public interface MainframeApplicationCustomizer<T : MainframeApplicationContext> {

    public fun customizeBuilder(builder: SpringApplicationBuilder): SpringApplicationBuilder {
        return builder
    }

    public fun customizeContext(applicationContext: T) {
    }

    public fun onApplicationStart() {
    }

    public fun onApplicationStarted(applicationContext: T) {
    }

}