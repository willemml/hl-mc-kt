package net.willemml.hlktmc.minecraft

import kotlin.math.pow
import kotlin.math.sqrt

data class Rotation(var yaw: Float = 0.0f, var pitch: Float = 0.0f) {
    fun addDelta(delta: RotationDelta) {
        addDelta(delta.yawDelta, delta.pitchDelta)
    }

    fun addDelta(yawDelta: Float, pitchDelta: Float) {
        pitch += pitchDelta
        yaw += yawDelta
    }

    fun set(pitch: Float, yaw: Float) {
        this.pitch = pitch
        this.yaw = yaw
    }
}

data class RotationDelta(var yawDelta: Float = 0.0f, var pitchDelta: Float = 0.0f) {
    fun isZero() = pitchDelta != 0.0f && yawDelta != 0.0f

    companion object {
        fun between(a: Rotation, b: Rotation) = RotationDelta(b.yaw.minus(a.yaw), b.pitch.minus(a.pitch))
    }
}

data class Position(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {
    fun addDelta(delta: PositionDelta) {
        addDelta(delta.deltaX, delta.deltaY, delta.deltaZ)
    }

    fun addDelta(deltaX: Double, deltaY: Double, deltaZ: Double) {
        x += deltaX
        y += deltaY
        z += deltaZ
    }

    fun set(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }
}

data class PositionDelta(var deltaX: Double = 0.0, var deltaY: Double = 0.0, var deltaZ: Double = 0.0) {
    fun isZero() = deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0

    fun biggerThan(maximum: Double) = (deltaX.pow(2) + deltaY.pow(2) + deltaZ.pow(2)) > maximum.pow(2)

    fun distanceSquared() = deltaX.pow(2) + deltaY.pow(2) + deltaZ.pow(2)

    fun distance() = sqrt(distanceSquared())

    companion object {
        fun between(a: Position, b: Position) = PositionDelta(b.x.minus(a.x), b.y.minus(a.y), b.z.minus(a.z))
    }
}