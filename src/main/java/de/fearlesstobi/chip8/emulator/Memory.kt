package de.fearlesstobi.chip8.emulator

@ExperimentalUnsignedTypes
class Memory {

    private fun UByte.toPositiveInt() = toInt() and 0xFF
    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }
    private val spriteData = byteArrayOfInts(
            0xF0, 0x90, 0x90, 0x90, 0xF0, //0
            0x20, 0x60, 0x20, 0x20, 0x70, //1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, //2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, //3
            0x90, 0x90, 0xF0, 0x10, 0x10, //4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, //5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, //6
            0xF0, 0x10, 0x20, 0x40, 0x40, //7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, //8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, //9
            0xF0, 0x90, 0xF0, 0x90, 0x90, //A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, //B
            0xF0, 0x80, 0x80, 0x80, 0xF0, //C
            0xE0, 0x90, 0x90, 0x90, 0xE0, //D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, //E
            0xF0, 0x80, 0xF0, 0x80, 0x80  //F
    )

    private var mem = UByteArray(0xFFF) // RAM
    var stack = UShortArray(0x10) // Array of 16 (16 bit) values

    var V = UByteArray(0x10) //16 registers (8 bit)
    var I: UShort = 0x0.toUShort() //I register (16 bit)
    var delayTimer: UByte = 0x0u //8 bit timer
    var soundTimer: UByte = 0x0u //8 bit timer

    var PC: UShort = 0x0u //Program Counter (16 bit)
    var SP: UByte = 0x0u //Stack Pointer (8 bit)

    var display = UByteArray(2048)

    var keys = UByteArray(16)

    fun memcpy(toWrite: UByteArray, position: Int) {
        for (i in toWrite.indices) {
            mem[i + position] = toWrite[i]
        }
    }

    fun write8(pos: Int, value: UByte) {
        mem[pos] = value
    }

    fun read8(pos: Int): UByte {
        return mem[pos]
    }

    fun read16(pos: Int): UShort {
        val hiByte = mem[pos + 0]  // or as appropriate
        val lowByte = mem[pos + 1]

        return ((hiByte.toInt() shl 8) or lowByte.toPositiveInt()).toUShort()
    }

    fun mapSpriteData() {
        memcpy(spriteData.toUByteArray(), 0x0)
    }

    fun resetRegisters() {
        for (i in V.indices) {
            V[i] = 0x0u
        }
        I = 0x0u
        delayTimer = 0x0u
        soundTimer = 0x0u

        PC = 0x0u
        SP = 0x0u
    }

    fun resetScreen() {
        for (i in display.indices) {
            display[i] = 0x0u
        }
    }

}
