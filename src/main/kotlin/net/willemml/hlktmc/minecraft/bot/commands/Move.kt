package net.willemml.hlktmc.minecraft.bot.commands

import net.willemml.hlktmc.minecraft.bot.ChatMessage
import net.willemml.ktcmd.Command

val moveZ = Command<ChatMessage>("z", "Move on the Z axis.", arrayListOf("e"), true) {
    it.client.positionDelta.deltaZ += getArgument<Double>("distance")
}.apply {
    string("stuff", true)
    double("distance", true, "How far to move")
}

val moveX = Command<ChatMessage>("x", "Move on the X axis.", arrayListOf("e"), true) {
    it.client.positionDelta.deltaX += getArgument<Double>("distance")
}.apply {
    string("stuff", true)
    double("distance", true, "How far to move")
}

val moveY = Command<ChatMessage>("y", "Move on the Y axis.", arrayListOf("e"), true) {
    it.client.positionDelta.deltaY += getArgument<Double>("distance")
}.apply {
    string("stuff", true)
    double("distance", true, "How far to move")
}
