package protocol

import randomAlphanumeric

data class LoginRequest(
    val username: String = "",
    val password: String = "",
    val clientToken: String = randomAlphanumeric(32),
    val agent: Agent = Agent(),
    val requestUser: Boolean = false
)

data class SignOut(
    val username: String = "",
    val password: String = ""
)

data class Validate(
    val accessToken: String = "",
    val clientToken: String = ""
)

data class Refresh(
    val accessToken: String = "",
    val clientToken: String = "",
    val selectedProfile: Array<UserProfile>? = null,
    val requestUser: Boolean = false
)

data class RefreshResponse(
    val accessToken: String = "",
    val clientToken: String = "",
    val selectedProfile: Array<UserProfile>? = null,
    val user: AccountProfile? = null
)

data class Agent(
    val name: String = "Minecraft",
    val version: Int = 1
)

data class Profile(
    var accessToken: String = "",
    val clientToken: String = "",
    val availableProfiles: Array<UserProfile>? = null,
    val selectedProfile: Array<UserProfile>? = null,
    val user: AccountProfile? = null
)

data class UserProfile(
    val agent: String = "",
    val id: String = "",
    val name: String = "",
    val createdAt: Long = 0,
    val legacyProfile: Boolean = false,
    val suspended: Boolean = false,
    val paid: Boolean = true,
    val migrated: Boolean = false,
    val legacy: Boolean = false
)

data class AccountProfile(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val registerIp: String = "",
    val migratedFrom: String = "",
    val migratedAt: Long = 0,
    val registeredAt: Long = 0,
    val passwordChangedAt: Long = 0,
    val dateOfBirth: Long = 0,
    val suspended: Boolean = false,
    val blocked: Boolean = false,
    val secured: Boolean = false,
    val migrated: Boolean = false,
    val emailVerified: Boolean = false,
    val legacyUser: Boolean = false,
    val verifiedByParent: Boolean = false,
    val properties: Array<Property>? = null
)

data class Property(
    val name: String = "",
    val value: String = ""
)