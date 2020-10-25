package net.willemml.hlktmc.minecraft.player

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.tcp.TcpClientSession
import net.willemml.hlktmc.minecraft.BasicClient
import net.willemml.hlktmc.minecraft.world.ClientMovementManager
import net.willemml.hlktmc.minecraft.world.WorldManager
import net.willemml.hlktmc.minecraft.world.types.ChunkPos
import java.util.*

data class Player(val name: String,
                  val uuid: UUID,
                  val client: Client,
                  val world: WorldManager,
                  val positioning: ClientMovementManager = ClientMovementManager(client, world),
                  var entityID: Int = 0,
                  var spawnPoint: Position = Position(),
                  var health: Health = Health(),
                  var gameMode: GameMode = GameMode.SPECTATOR
)