package net.willemml.hlktmc.minecraft.objects

import kotlinx.serialization.Serializable

@Serializable
data class Material(val patternProperties: Array<Float>? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Material

        if (!patternProperties.contentEquals(other.patternProperties)) return false

        return true
    }

    override fun hashCode(): Int {
        return patternProperties.contentHashCode()
    }
}