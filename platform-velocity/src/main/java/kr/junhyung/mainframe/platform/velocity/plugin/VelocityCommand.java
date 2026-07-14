package kr.junhyung.mainframe.platform.velocity.plugin;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandMeta;

public record VelocityCommand(CommandMeta meta, Command command) {}
