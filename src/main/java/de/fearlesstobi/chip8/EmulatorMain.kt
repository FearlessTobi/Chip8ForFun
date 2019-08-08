package de.fearlesstobi.chip8

import de.fearlesstobi.chip8.emulator.Memory
import de.fearlesstobi.chip8.emulator.instructions.InstructionParser
import de.fearlesstobi.chip8.files.RomLoader
import de.fearlesstobi.chip8.gui.Chip8Form

object start {
    @ExperimentalUnsignedTypes
    @JvmStatic
    fun main(args: Array<String>) {
        println("Started")
        EmulatorMain()
    }
}

@ExperimentalUnsignedTypes
class EmulatorMain {

    var drawFlag: Boolean = true
    val memory: Memory = Memory()
    private val parser: InstructionParser = InstructionParser(getInstance())
    private val form: Chip8Form = Chip8Form.createNewMainForm(getInstance())

    init {
        initEmulator()

        // main loop
        while (true) {
            parser.nextInstruction()

            // Update timers
            if (memory.delayTimer > 0u)
                memory.delayTimer--

            if (memory.soundTimer > 0u) {
                // TODO: Implement sound
                if (memory.soundTimer.toInt() == 1)
                    println("DING")

                memory.soundTimer--
            }

            if (drawFlag) {
                form.panel.repaint()
                drawFlag = false
            }
        }
    }

    private fun initEmulator() {
        memory.resetRegisters()
        memory.resetScreen()
        memory.mapSpriteData()
        loadRom()
    }

    private fun loadRom() {
        val path = "Tetris.ch8"

        val bytes = RomLoader.getRomBytes(path)
        if (bytes == null) {
            System.err.println("File could not be loaded!")
            return
        }
        memory.memcpy(bytes.toUByteArray(), 0x200)
        memory.PC = 0x200u
    }

    private fun getInstance(): EmulatorMain {
        return this
    }

}
