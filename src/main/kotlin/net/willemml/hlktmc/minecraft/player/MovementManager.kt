package net.willemml.hlktmc.minecraft.player

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket
import com.github.steveice10.packetlib.Client
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.willemml.hlktmc.minecraft.world.WorldManager
import net.willemml.hlktmc.minecraft.world.types.ChunkPos
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

const val GRAVITY = -0.98 // Y Acceleration of the player when not on ground (blocks per second per second)
const val WALK_SPEED = 4.4 // Normal Minecraft player walking speed (blocks per second)
const val RUN_SPEED = 5.7 // Normal Minecraft running (sprinting) speed (blocks per second)
const val JUMP_RUN_MODIFIER = 0.4 // How much faster the player can run when jumping (blocks per second)
const val ROTATE_SPEED = 90.0f // How fast the player should usually rotate (degrees per second)

/**
 * Manages movement of the player attached to [client], uses [world] as a reference for checking whether a position is valid.
 */
class MovementManager(private val client: Client, private val world: WorldManager) {
    private val movementQueue = ArrayList<PositionDelta>() // Queued movements
    private val rotationQueue = ArrayList<RotationDelta>() // Queued rotations
    private var jumping = false // Whether or not the player is jumping

    /**
     * The player's current position
     */
    var position: Position = Position()

    /**
     * The coordinates of the chunk the player is in
     */
    var chunk: ChunkPos = ChunkPos()

    /**
     * The player's current orientation
     */
    var rotation: Rotation = Rotation()

    /**
     * Set to true to stop the movement loop
     * @see [onJoin]
     */
    var stop = true

    /**
     * Starts the movement loop, if it is not started the movement queue will not be executed automatically, should be called once the player can move.
     */
    fun onJoin() {
        stop = false
        GlobalScope.launch {
            while (!stop) {
                if (movementQueue.isNotEmpty() || rotationQueue.isNotEmpty()) performMovement()
                delay(50) // Wait 50ms (20th of a second) before moving again, avoids packet spam and moving to fast
            }
        }
    }

    /**
     * Checks whether or not the player is touching the ground (not floating)
     * @return Whether or not the player is on the ground
     */
    fun onGround(): Boolean {
        val yDecimal = position.y - position.y.toInt() // Get the decimal part of the player's Y coordinate
        val isBlockUnderSolid = world.isSolid(
            position.copy(y = position.y.minus(1)).blockPos()
        ) // Check if the block under the player is solid
        return yDecimal != 0.0 || !isBlockUnderSolid
    }

    /**
     * Moves the player by [delta] at [speed] blocks per second, when done [onFinish] will be invoked
     * @return The time it will take for the player to move there
     */
    fun move(delta: PositionDelta, speed: Double = WALK_SPEED, onFinish: MovementManager.() -> Unit = {}): Long {
        val final = Position().addDelta(delta) // Desired final position
        val time: Double = abs( // Time it will take to move
            when {
                abs(delta.x) >= abs(delta.y) && abs(delta.x) >= abs(delta.z) -> delta.x
                abs(delta.z) >= abs(delta.x) && abs(delta.z) >= abs(delta.y) -> delta.z
                else -> delta.y
            }
        ) / abs(speed)
        val smallDelta = // How much to move every 100ms
            PositionDelta(
                if (delta.x == 0.0) 0.0 else (delta.x / abs((delta.x / time))) / 10,
                if (delta.y == 0.0) 0.0 else (delta.y / abs((delta.y / time))) / 10,
                if (delta.z == 0.0) 0.0 else (delta.z / abs((delta.z / time))) / 10
            )
        if (smallDelta.isZero()) {
            onFinish.invoke(this@MovementManager)
            return 0
        }
        GlobalScope.launch {
            var moved = Position() // Empty position to track progress
            while (moved <= final) { // Move in small increments until the desired position has been reached
                if (smallDelta.isZero()) break // Stop the loop if small delta is zero to avoid infinite looping
                movementQueue.add(smallDelta) // Add the movement to the queue so it will be executed
                moved = moved.addDelta(smallDelta) // Add the movement to the progress tracker
                if (moved <= final) delay(100) else break
            }
            onFinish.invoke(this@MovementManager)
        }
        return (time * 1000).toLong()
    }

    /**
     * Makes the player jump [height] blocks at [speed] blocks per second
     * @return The time it will take the player to reach the peak in the jump
     */
    fun jump(height: Double = 1.25, speed: Double = 2.0): Long {
        if (jumping || !onGround()) return 0 // Check that the player is on the ground and not already jumping
        jumping = true // This is set so that gravity does not take effect and so that the player does not "double jump"
        val time = move(PositionDelta(y = height), speed)
        GlobalScope.launch { // Wait for the player to hit the apex of the jump and then set jumping to false
            delay(time)
            jumping = false
        }
        return time
    }

    /**
     * Makes the player go to [position] at [speed] blocks per second
     */
    fun moveTo(position: Position, speed: Double = WALK_SPEED) =
        move(PositionDelta.from(this.position, position), speed)

    /**
     * Makes the player look at [rotation] rotating at [speed] degrees per second
     */
    fun rotateTo(rotation: Rotation, speed: Float = ROTATE_SPEED) =
        rotate(RotationDelta.from(this.rotation, rotation), speed)

    /**
     * Rotates the player by the values given in [delta] at [speed] degrees per second, when done [onFinish] will be invoked
     * @return How long it will take to rotate the player
     */
    fun rotate(delta: RotationDelta, speed: Float = ROTATE_SPEED, onFinish: MovementManager.() -> Unit = {}): Long {
        val final = Rotation().apply { addDelta(delta) } // Desired final orientation
        val time: Float = abs( // Time it will take to rotate
            when {
                delta.yaw > delta.pitch -> delta.yaw
                else -> delta.pitch
            }
        ) / abs(speed)
        val smallDelta = // How much to rotate every 100ms
            RotationDelta(
                (delta.yaw / (delta.yaw / time)) / 10,
                (delta.pitch / (delta.pitch / time)) / 10
            )
        GlobalScope.launch {
            var rotated = Rotation() // Create an empty rotation, used to track progress
            while (rotated <= final) { // Keep rotating in small increments until the desired orientation has been reached
                if (smallDelta.isZero()) break // Stop the loop if the delta is 0 to avoid infinite looping
                rotationQueue.add(smallDelta) // Add the rotation to the queue so that it will be executed
                rotated = rotated.addDelta(smallDelta) // Add the rotation to the progress tracker
                if (rotated <= final) delay(100) else break
            }
            onFinish.invoke(this@MovementManager)
        }
        return (time * 1000).toLong()
    }

    /**
     * Checks if there is a movement in the queue, if so it checks that it is valid and executes it
     */
    private fun performMovement() {
        var currentPosition = position
        var currentRotation = rotation
        val rotationDelta = if (rotationQueue.size > 0) {
            rotationQueue[0]
            rotationQueue.removeAt(0) // remove it from the queue or it will be repeated indefinitely
        } else RotationDelta() // Get the next rotation in the queue, if there are none get an empty one
        var positionDelta = if (movementQueue.size > 0) {
            movementQueue[0]
            movementQueue.removeAt(0)
        } else PositionDelta()
        val newPosition = currentPosition.addDelta(positionDelta) // Get the position this movement would result in
        val newRotation = currentRotation.addDelta(rotationDelta)
        if (world.isSolid(newPosition.blockPos()) || world.isSolid(
                newPosition.blockPos().copy(y = newPosition.blockPos().y + 1)
            )
        ) positionDelta = PositionDelta() else currentPosition = newPosition
        currentRotation = newRotation
        when { // Send the new position and or rotation to the server
            rotationDelta.isZero() -> client.session.send(
                ClientPlayerPositionPacket(
                    onGround(),
                    currentPosition.x,
                    currentPosition.y,
                    currentPosition.z
                )
            )
            positionDelta.isZero() -> client.session.send(
                ClientPlayerRotationPacket(
                    onGround(),
                    currentRotation.yaw,
                    currentRotation.pitch
                )
            )
            else -> client.session.send(
                ClientPlayerPositionRotationPacket(
                    true,
                    currentPosition.x,
                    currentPosition.y,
                    currentPosition.z,
                    currentRotation.yaw,
                    currentRotation.pitch
                )
            )
        }
        position = currentPosition
        rotation = currentRotation
        println("$position ${SimpleDateFormat("mm:ss").format(Date())}")
    }
}