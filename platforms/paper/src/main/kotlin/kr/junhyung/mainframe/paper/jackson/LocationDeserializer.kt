package kr.junhyung.mainframe.paper.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.bukkit.Location
import org.bukkit.Server
import org.slf4j.LoggerFactory

public class LocationDeserializer(private val server: Server) : StdDeserializer<Location>(Location::class.java) {

    private val logger = LoggerFactory.getLogger(LocationDeserializer::class.java)

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Location {
        val node = parser.codec.readTree<JsonNode>(parser)
        val x = node.get("x").asDouble()
        val y = node.get("y").asDouble()
        val z = node.get("z").asDouble()
        val worldUid = node.get("world_uid")?.asText()

        if (worldUid == null) {
            return Location(null, x, y, z)
        }

        val world = server.getWorld(worldUid)
        if (world == null) {
            logger.warn("World with UID $worldUid not found. Returning Location with null world.")
            return Location(null, x, y, z)
        }

        return Location(world, x, y, z)
    }

}