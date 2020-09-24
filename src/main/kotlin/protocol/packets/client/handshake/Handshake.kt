package protocol.packets.client.handshake


enum class NextState(val id: Int) {
    Status(1),
    Login(2)
}

data class Handshake @ExperimentalUnsignedTypes constructor(val protocolVersion: Int, val serverAddress: String, val serverPort: UShort, val nextState: NextState)