package net.willemml.hlktmc.minecraft.objects

import kotlinx.serialization.Serializable

@Serializable
data class Item(
        val id: Int,
        val displayName: String,
        val name: String,
        val stackSize: Int
)