package kr.junhyung.mainframe.paper.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.UUID

class LocationSerializerTests {

    private lateinit var objectMapper: ObjectMapper

    private lateinit var worldId: UUID
    private lateinit var world: World
    private lateinit var server: Server

    @BeforeEach
    fun setup() {
        this.worldId = UUID.randomUUID()
        this.world = object : World by mock() {
            override fun getUID(): UUID {
                return worldId
            }
        }
        this.server = object : Server by mock() {
            override fun getWorld(uid: UUID): World? {
                return if (uid == worldId) {
                    world
                } else {
                    null
                }
            }
        }

        val serializerModule = SimpleModule().apply {
            addSerializer(LocationSerializer())
            addDeserializer(Location::class.java, LocationDeserializer(server))
        }

        val objectMapper = ObjectMapper().apply {
            registerModule(serializerModule)
        }
        this.objectMapper = objectMapper
    }

    @Test
    fun testIfWorldIsNull() {
        val location = Location(null, 1.0, 2.0, 3.0)
        val serialized = objectMapper.writeValueAsString(location)
        val deserialized = objectMapper.readValue(serialized, Location::class.java)
        assert(deserialized.x == location.x)
        assert(deserialized.y == location.y)
        assert(deserialized.z == location.z)
        assert(deserialized.world == null)
    }

    @Test
    fun testIfWorldIsNotNull() {
        val location = Location(world, 1.0, 2.0, 3.0)
        val serialized = objectMapper.writeValueAsString(location)
        val deserialized = objectMapper.readValue(serialized, Location::class.java)
        assert(deserialized.x == location.x)
        assert(deserialized.y == location.y)
        assert(deserialized.z == location.z)
        assert(location.world?.uid == worldId)
    }

}