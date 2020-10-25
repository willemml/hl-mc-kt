package net.willemml.hlktmc.minecraft.player

import kotlin.math.pow

data class Rotation(var yaw: Float = 0.0f, var pitch: Float = 0.0f) : Comparable<Rotation> {
    fun addDelta(delta: RotationDelta) {
        addDelta(delta.yaw, delta.pitch)
    }

    fun addDelta(yawDelta: Float, pitchDelta: Float) {
        pitch += pitchDelta
        yaw += yawDelta
    }

    fun set(pitch: Float, yaw: Float) {
        this.pitch = pitch
        this.yaw = yaw
    }

    override operator fun compareTo(other: Rotation): Int {
        val distance = RotationDelta(yaw, pitch).distanceSquared()
        val distanceOther = RotationDelta(other.yaw, other.pitch).distanceSquared()
        return when {
            distance == distanceOther -> 0
            distance > distanceOther -> 1
            else -> -1
        }
    }
}

data class RotationDelta(var yaw: Float = 0.0f, var pitch: Float = 0.0f) {
    fun isZero() = pitch != 0.0f && yaw != 0.0f

    fun distanceSquared() = yaw.pow(2) + pitch.pow(2)

    fun zero() {
        yaw = 0.0f
        pitch = 0.0f
    }

    companion object {
        fun between(a: Rotation, b: Rotation) = RotationDelta(b.yaw.minus(a.yaw), b.pitch.minus(a.pitch))
    }
}