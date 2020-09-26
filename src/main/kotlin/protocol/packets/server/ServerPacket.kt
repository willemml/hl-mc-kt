package protocol.packets.server

import protocol.packets.Packet
import protocol.packets.VarInt

abstract class ServerPacket(@VarInt id: Int): Packet(id)