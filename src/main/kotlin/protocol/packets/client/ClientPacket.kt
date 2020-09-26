package protocol.packets.client

import protocol.packets.Packet
import protocol.packets.VarInt

abstract class ClientPacket(@VarInt id: Int): Packet(id)