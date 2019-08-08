package de.fearlesstobi.chip8.gui

import de.fearlesstobi.chip8.EmulatorMain
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JPanel

class Chip8Panel(private val emulator: EmulatorMain) : JPanel() {

    init {
        preferredSize = Dimension(64 * 8, 32 * 8)
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.color = Color.BLACK

        for (x in 0 until 64) {
            for (y in 0 until 32) {
                val display = emulator.memory.display[x + y * 64]

                if (display.toInt() == 1) {
                    g.fillRect(x * 8, y * 8, 8, 8)
                }
            }
        }
    }

}
