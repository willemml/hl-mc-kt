package net.willemml.hlktmc.minecraft.bot.commands

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.willemml.hlktmc.minecraft.bot.ChatMessage
import net.willemml.hlktmc.minecraft.player.Position
import net.willemml.hlktmc.minecraft.player.PositionDelta
import net.willemml.hlktmc.minecraft.player.Rotation
import net.willemml.ktcmd.Command

val moveZ = Command<ChatMessage>("z", "Move on the Z axis.", arrayListOf(), true) {
    it.client.player.positioning.move(PositionDelta(z = getArgument("distance")))
}.apply {
    double("distance", true, "How far to move")
}

val moveX = Command<ChatMessage>("x", "Move on the X axis.", arrayListOf(), true) {
    it.client.player.positioning.move(PositionDelta(x = getArgument("distance")))
}.apply {
    double("distance", true, "How far to move")
}

val moveY = Command<ChatMessage>("y", "Move on the Y axis.", arrayListOf(), true) {
    it.client.player.positioning.move(PositionDelta(y = getArgument("distance")))
}.apply {
    double("distance", true, "How far to move")
}

val moveTo = Command<ChatMessage>("move", "Move the player.", arrayListOf(), true) {
    it.client.player.positioning.moveTo(Position(getArgument("x"), getArgument("y"), getArgument("z")))
}.apply {
    double("x", true, "What x coordinate to go to")
    double("y", true, "What y coordinate to go to")
    double("z", true, "What z coordinate to go to")
}

val rotateTo = Command<ChatMessage>("rotate", "Rotate the player.", arrayListOf(), true) {
    it.client.player.positioning.rotateTo(Rotation(getArgument("yaw"), getArgument("pitch")))
}.apply {
    float("yaw", true, "What to set the yaw to")
    float("pitch", true, "What to set the pitch to")
}

val square = Command<ChatMessage>("square", "Makes the player move in a square on the XZ axis'.", arrayListOf("s"), true) {
    GlobalScope.launch {
        it.client.player.positioning.move(PositionDelta(getArgument("size"))) {
            move(PositionDelta(z = getArgument("size"))) {
                move(PositionDelta(getArgument<Double>("size") * -1.0)) {
                    move(PositionDelta(z = getArgument<Double>("size") * -1.0))
                }
            }
        }
    }
}.apply {
    double("size", true, "How big the sides of the square should be")
}
