package de.fearlesstobi.chip8.gui

import de.fearlesstobi.chip8.EmulatorMain
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame

class Chip8Frame(title: String, private val emulator: EmulatorMain) : JFrame(title), KeyListener {

    // Keypad keymap
    private val keymap: IntArray = intArrayOf(
            KeyEvent.VK_X,
            KeyEvent.VK_1,
            KeyEvent.VK_2,
            KeyEvent.VK_3,
            KeyEvent.VK_Q,
            KeyEvent.VK_W,
            KeyEvent.VK_E,
            KeyEvent.VK_A,
            KeyEvent.VK_S,
            KeyEvent.VK_D,
            KeyEvent.VK_Z,
            KeyEvent.VK_C,
            KeyEvent.VK_4,
            KeyEvent.VK_R,
            KeyEvent.VK_F,
            KeyEvent.VK_V
    )

    init {
        addKeyListener(this)
    }

    override fun keyTyped(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent) {
        for (i in 0 until keymap.size) {
            if (e.keyCode == keymap[i]) {
                emulator.memory.keys[i] = 1u
            }
        }
    }

    override fun keyReleased(e: KeyEvent) {
        for (i in 0 until keymap.size) {
            if (e.keyCode == keymap[i]) {
                emulator.memory.keys[i] = 0u
            }
        }
    }
}
