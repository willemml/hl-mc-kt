package net.willemml.hlktmc.minecraft.player

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

    fun zero() {
        yawDelta = 0.0f
        pitchDelta = 0.0f
    }

    companion object {
        fun between(a: Rotation, b: Rotation) = RotationDelta(b.yaw.minus(a.yaw), b.pitch.minus(a.pitch))
    }
}