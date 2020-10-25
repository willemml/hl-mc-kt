package net.willemml.hlktmc.minecraft.world

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket
import com.github.steveice10.packetlib.Client
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.willemml.hlktmc.minecraft.player.Position
import net.willemml.hlktmc.minecraft.player.PositionDelta
import net.willemml.hlktmc.minecraft.player.Rotation
import net.willemml.hlktmc.minecraft.player.RotationDelta
import net.willemml.hlktmc.minecraft.world.types.ChunkPos

const val GRAVITY = -0.98 // blocks per second squared
const val WALK_SPEED = 4.4 // blocks per second
const val RUN_SPEED = 5.7 // blocks per second
const val JUMP_RUN_MODIFIER = 0.4 // blocks per second

class ClientMovementManager(private val client: Client, private val world: WorldManager) {
    private val movementQueue = ArrayList<PositionDelta>()
    private val rotationQueue = ArrayList<RotationDelta>()

    var position: Position = Position()
    var chunk: ChunkPos = ChunkPos()
    var rotation: Rotation = Rotation()
    var stopped = true

    var rotationDelta = RotationDelta()

    fun onJoin() {
        stopped = false
        GlobalScope.launch {
            while (!stopped) {
                performMovement()
                delay(50)
            }
        }
    }

    fun onGround(): Boolean {
        val yDecimal = position.y - position.y.toInt()
        val isBlockUnderSolid = world.isSolid(position.copy(y = position.y.minus(1)).blockPos())
        return yDecimal != 0.0 || !isBlockUnderSolid
    }

    fun move(delta: PositionDelta, speed: Double): Long {
        val final = Position().addDelta(delta)
        val time: Double = when {
            delta.x >= delta.y && delta.x >= delta.z -> delta.x
            delta.z >= delta.x && delta.z >= delta.y -> delta.z
            else -> delta.y
        } / speed
        val smallSpeed = (speed / time)
        val smallDelta = PositionDelta(delta.x / smallSpeed, delta.y / smallSpeed, delta.z / smallSpeed)
        val moved = Position()
        GlobalScope.launch {
            while (!moved.equals(final)) {
                movementQueue.add(smallDelta)
                moved.addDelta(smallDelta)
                if (!moved.equals(final)) delay(100)
            }
        }
        return (time * 1000).toLong()
    }

    fun rotate(delta: RotationDelta, speed: Float): Long {
        val final = Rotation().addDelta(delta)
        val time: Float = when {
            delta.yawDelta > delta.pitchDelta -> delta.yawDelta
            else -> delta.pitchDelta
        } / speed
        val smallSpeed = (speed / time)
        val smallDelta = RotationDelta(delta.yawDelta / smallSpeed, delta.pitchDelta / smallSpeed)
        val rotated = Rotation()
        GlobalScope.launch {
            while (!rotated.equals(final)) {
                rotationQueue.add(smallDelta)
                rotated.addDelta(smallDelta)
                if (!rotated.equals(final)) delay(100)
            }
        }
        return (time * 1000).toLong()
    }

    private fun performMovement() {
        val start = position
        val rotationDelta = if (rotationQueue.size > 0) rotationQueue[0] else RotationDelta()
        val positionDelta = if (movementQueue.size > 0) movementQueue[0] else PositionDelta()
        position.addDelta(positionDelta)
        rotation.addDelta(rotationDelta)
        if (world.isSolid(position.blockPos())) {
            position = start
            positionDelta.zero()
        }
        when {
            rotationDelta.isZero() -> client.session.send(ClientPlayerPositionPacket(onGround(), position.x, position.y, position.z))
            positionDelta.isZero() -> client.session.send(ClientPlayerRotationPacket(onGround(), rotation.yaw, rotation.pitch))
            else -> client.session.send(ClientPlayerPositionRotationPacket(true, position.x, position.y, position.z, rotation.yaw, rotation.pitch))
        }
        rotationDelta.zero()
        positionDelta.zero()
    }
}