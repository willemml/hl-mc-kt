package net.willemml.hlktmc.minecraft.bot.commands

import net.willemml.hlktmc.minecraft.bot.ChatMessage
import net.willemml.hlktmc.minecraft.player.PositionDelta
import net.willemml.hlktmc.minecraft.world.WALK_SPEED
import net.willemml.ktcmd.Command

val moveZ = Command<ChatMessage>("z", "Move on the Z axis.", arrayListOf("e"), true) {
    it.client.player.positioning.move(PositionDelta(z = getArgument<Double>("distance")), WALK_SPEED)
}.apply {
    double("distance", true, "How far to move")
}

val moveX = Command<ChatMessage>("x", "Move on the X axis.", arrayListOf("e"), true) {
    it.client.player.positioning.move(PositionDelta(x = getArgument<Double>("distance")), WALK_SPEED)
}.apply {
    double("distance", true, "How far to move")
}

val moveY = Command<ChatMessage>("y", "Move on the Y axis.", arrayListOf("e"), true) {
    it.client.player.positioning.move(PositionDelta(y = getArgument<Double>("distance")), WALK_SPEED)
}.apply {
    double("distance", true, "How far to move")
}
