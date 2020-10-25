package net.willemml.hlktmc.minecraft.objects

enum class BoundingBox {
    block,
    empty
}

data class State(val name: String, val type: String, val num_values: Int)

data class Block(
        val id: Int,
        val displayName: String,
        val name: String,
        val hardness: Float?,
        val stackSize: Int,
        val diggable: Boolean,
        val boundingBox: BoundingBox,
        val material: String?,
        val harvestTools: HashMap<String, Boolean>?,
        val states: Array<State>?,
        val drops: Array<Int>,
        val transparent: Boolean,
        val emitLight: Int,
        val filterLight: Int,
        val minStateId: Int?,
        val maxStateId: Int?,
        val defaultState: Int?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (id != other.id) return false
        if (displayName != other.displayName) return false
        if (name != other.name) return false
        if (hardness != other.hardness) return false
        if (stackSize != other.stackSize) return false
        if (diggable != other.diggable) return false
        if (boundingBox != other.boundingBox) return false
        if (material != other.material) return false
        if (harvestTools != other.harvestTools) return false
        if (states != null) {
            if (other.states == null) return false
            if (!states.contentEquals(other.states)) return false
        } else if (other.states != null) return false
        if (!drops.contentEquals(other.drops)) return false
        if (transparent != other.transparent) return false
        if (emitLight != other.emitLight) return false
        if (filterLight != other.filterLight) return false
        if (minStateId != other.minStateId) return false
        if (maxStateId != other.maxStateId) return false
        if (defaultState != other.defaultState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + displayName.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (hardness?.hashCode() ?: 0)
        result = 31 * result + stackSize
        result = 31 * result + diggable.hashCode()
        result = 31 * result + boundingBox.hashCode()
        result = 31 * result + (material?.hashCode() ?: 0)
        result = 31 * result + (harvestTools?.hashCode() ?: 0)
        result = 31 * result + (states?.contentHashCode() ?: 0)
        result = 31 * result + drops.contentHashCode()
        result = 31 * result + transparent.hashCode()
        result = 31 * result + emitLight
        result = 31 * result + filterLight
        result = 31 * result + (minStateId ?: 0)
        result = 31 * result + (maxStateId ?: 0)
        result = 31 * result + (defaultState ?: 0)
        return result
    }
}