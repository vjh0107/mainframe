package kr.junhyung.mainframe.exposed.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json

public fun Table.adventureComponent(name: String): Column<Component> {
    return json(name, GsonComponentSerializer.gson()::serialize, GsonComponentSerializer.gson()::deserialize)
}