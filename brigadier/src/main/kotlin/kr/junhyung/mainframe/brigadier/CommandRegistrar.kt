package kr.junhyung.mainframe.brigadier

public interface CommandRegistrar {

    public fun registerCommand(command: Any, metadata: Command)

}