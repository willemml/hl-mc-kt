package net.willemml.hlktmc.minecraft.player

import kotlin.math.abs
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
        val distance = RotationDelta.from(Rotation(), this)
        val distanceOther = RotationDelta.from(Rotation(), other)
        return when {
            distance > distanceOther -> 1
            distance < distanceOther -> -1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rotation

        if (yaw != other.yaw) return false
        if (pitch != other.pitch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yaw.hashCode()
        result = 31 * result + pitch.hashCode()
        return result
    }
}

data class RotationDelta(var yaw: Float = 0.0f, var pitch: Float = 0.0f) : Comparable<RotationDelta> {
    fun isZero() = pitch != 0.0f && yaw != 0.0f

    fun distanceSquared() = yaw.pow(2) + pitch.pow(2)

    fun zero() {
        yaw = 0.0f
        pitch = 0.0f
    }

    override operator fun compareTo(other: RotationDelta): Int {
        if (this == other) return 0
        val array = arrayListOf(abs(yaw), abs(pitch)).apply { sort() }
        val arrayOther = arrayListOf(abs(other.yaw), abs(other.pitch)).apply { sort() }
        return when {
            array[0] > arrayOther[0] -> 1
            array[0] < arrayOther[0] -> -1
            array[1] > arrayOther[1] -> 1
            array[1] < arrayOther[1] -> -1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RotationDelta

        if (yaw != other.yaw) return false
        if (pitch != other.pitch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yaw.hashCode()
        result = 31 * result + pitch.hashCode()
        return result
    }

    companion object {
        fun from(a: Rotation, b: Rotation) = RotationDelta(b.yaw.minus(a.yaw), b.pitch.minus(a.pitch))
    }
}