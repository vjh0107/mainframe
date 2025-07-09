package kr.junhyung.mainframe.paper.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.bukkit.Location

public class LocationSerializer : StdSerializer<Location>(Location::class.java) {

    override fun serialize(value: Location, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeNumberField("x", value.x)
        generator.writeNumberField("y", value.y)
        generator.writeNumberField("z", value.z)
        generator.writeObjectField("world_uid", value.world?.uid)
        generator.writeEndObject()
    }

}