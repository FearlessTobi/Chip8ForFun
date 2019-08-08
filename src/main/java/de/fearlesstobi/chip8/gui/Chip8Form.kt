package de.fearlesstobi.chip8.gui

import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import de.fearlesstobi.chip8.EmulatorMain
import java.awt.Insets
import javax.swing.JPanel
import javax.swing.WindowConstants

class Chip8Form private constructor(emulator: EmulatorMain) {

    private val jpMain: JPanel = JPanel()
    val panel: JPanel = Chip8Panel(emulator)

    init {
        setupUI()
    }

    private fun setupUI() {
        jpMain.layout = GridLayoutManager(8, 2, Insets(0, 0, 0, 0), -1, -1)
        jpMain.add(panel, GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false))
    }

    companion object {

        fun createNewMainForm(emulator: EmulatorMain): Chip8Form {
            val frame = Chip8Frame("Chip8 Emulator", emulator)
            val form = Chip8Form(emulator)
            frame.contentPane = form.jpMain
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.pack()
            frame.isVisible = true

            return form
        }

    }

}
