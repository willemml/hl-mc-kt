package net.willemml.hlktmc.minecraft.bot.commands

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.willemml.hlktmc.minecraft.bot.ChatMessage
import net.willemml.hlktmc.minecraft.player.PositionDelta
import net.willemml.hlktmc.minecraft.player.WALK_SPEED
import net.willemml.ktcmd.Command

val moveZ = Command<ChatMessage>("z", "Move on the Z axis.", arrayListOf(), true) {
    it.client.player.positioning.move(PositionDelta(z = getArgument("distance")), WALK_SPEED)
}.apply {
    double("distance", true, "How far to move")
}

val moveX = Command<ChatMessage>("x", "Move on the X axis.", arrayListOf(), true) {
    it.client.player.positioning.move(PositionDelta(x = getArgument("distance")), WALK_SPEED)
}.apply {
    double("distance", true, "How far to move")
}

val moveY = Command<ChatMessage>("y", "Move on the Y axis.", arrayListOf(), true) {
    it.client.player.positioning.move(PositionDelta(y = getArgument("distance")), WALK_SPEED)
}.apply {
    double("distance", true, "How far to move")
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
