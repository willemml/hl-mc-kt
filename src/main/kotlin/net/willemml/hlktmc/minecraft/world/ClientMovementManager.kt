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
import kotlin.math.pow

const val GRAVITY = -0.98 // blocks per second squared
const val WALK_SPEED = 4.4 // blocks per second
const val RUN_SPEED = 5.7 // blocks per second
const val JUMP_RUN_MODIFIER = 0.4 // blocks per second
const val ROTATE_SPEED = 90.0f // degrees per second

class ClientMovementManager(private val client: Client, private val world: WorldManager) {
    private val movementQueue = ArrayList<PositionDelta>()
    private val rotationQueue = ArrayList<RotationDelta>()
    private var jumping = false

    var position: Position = Position()
    var chunk: ChunkPos = ChunkPos()
    var rotation: Rotation = Rotation()
    var stop = true

    fun onJoin() {
        stop = false
        GlobalScope.launch {
            while (!stop) {
                if (!jumping && !onGround()) move(PositionDelta(y = position.y - position.y.toInt()))
                performMovement()
                delay(50)
            }
        }
    }

    /**
     * Checks whether or not the player is touching the ground (not floating)
     * @return Whether or not the player is on the ground
     */
    fun onGround(): Boolean {
        val yDecimal = position.y - position.y.toInt()
        val isBlockUnderSolid = world.isSolid(position.copy(y = position.y.minus(1)).blockPos())
        return yDecimal != 0.0 || !isBlockUnderSolid
    }

    /**
     * Moves the player by [delta] at [speed] blocks per second
     * @return The time it will take for the player to move there
     */
    fun move(delta: PositionDelta, speed: Double = WALK_SPEED): Long {
        val final = Position().apply { addDelta(delta) }
        val time: Double = when {
            delta.x >= delta.y && delta.x >= delta.z -> delta.x
            delta.z >= delta.x && delta.z >= delta.y -> delta.z
            else -> delta.y
        } / speed
        val smallSpeed = (speed / time)
        val smallDelta = PositionDelta(delta.x / smallSpeed, delta.y / smallSpeed, delta.z / smallSpeed)
        GlobalScope.launch {
            val moved = Position()
            while (moved <= final) {
                movementQueue.add(smallDelta)
                moved.addDelta(smallDelta)
                if (moved <= final) delay(100) else break
            }
        }
        return (time * 1000).toLong()
    }

    /**
     * Makes the player jump [height] blocks at [speed] blocks per second
     * @return The time it will take the player to reach the peak in the jump
     */
    fun jump(height: Double = 1.25, speed: Double = 2.0): Long {
        jumping = true
        val time = move(PositionDelta(y = height), speed)
        GlobalScope.launch {
            delay(time)
            jumping = false
        }
        return time
    }

    /**
     * Rotates the player by the values given in [delta] at [speed] degrees per second
     * @return How long it will take to rotate the player
     */
    fun rotate(delta: RotationDelta, speed: Float = ROTATE_SPEED): Long {
        val final = Rotation().apply { addDelta(delta) }
        val time: Float = when {
            delta.yaw > delta.pitch -> delta.yaw
            else -> delta.pitch
        } / speed
        val smallSpeed = (speed / time)
        val smallDelta = RotationDelta(delta.yaw / smallSpeed, delta.pitch / smallSpeed)
        val rotated = Rotation()
        GlobalScope.launch {
            while (rotated <= final) {
                rotationQueue.add(smallDelta)
                rotated.addDelta(smallDelta)
                if (rotated <= final) delay(100) else break
            }
        }
        return (time * 1000).toLong()
    }

    private fun performMovement() {
        val start = position
        val rotationDelta = if (rotationQueue.size > 0) {
            rotationQueue[0]
            rotationQueue.removeAt(0)
        } else RotationDelta()
        val positionDelta = if (movementQueue.size > 0) {
            movementQueue[0]
            movementQueue.removeAt(0)
        } else PositionDelta()
        position.addDelta(positionDelta)
        rotation.addDelta(rotationDelta)
        if (world.isSolid(position.blockPos()) || world.isSolid(position.blockPos().copy(y = position.blockPos().y + 1))) {
            position = start
            positionDelta.zero()
        }
        when {
            rotationDelta.isZero() -> client.session.send(ClientPlayerPositionPacket(onGround(), position.x, position.y, position.z))
            positionDelta.isZero() -> client.session.send(ClientPlayerRotationPacket(onGround(), rotation.yaw, rotation.pitch))
            else -> client.session.send(ClientPlayerPositionRotationPacket(true, position.x, position.y, position.z, rotation.yaw, rotation.pitch))
        }
    }
}