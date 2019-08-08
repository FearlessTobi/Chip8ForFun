package de.fearlesstobi.chip8.emulator.instructions

import de.fearlesstobi.chip8.EmulatorMain
import java.util.concurrent.ThreadLocalRandom
import kotlin.experimental.and

@ExperimentalUnsignedTypes
class InstructionParser(private val emulator: EmulatorMain) {

    private val memory = emulator.memory

    fun nextInstruction() {
        val fromMem = memory.read16(memory.PC.toInt())
        parseInstruction(fromMem.toShort())
    }

    private fun parseInstruction(instruction: Short) {
        Thread.sleep(3)

        when (instruction.toInt() and 0xF000) {
            0x0000 -> {
                when (instruction.toInt() and 0x00FF) {
                    0x00E0 -> handle00E0()
                    0x00EE -> handle00EE()
                    else -> {
                        unhandled(instruction)
                    }
                }
            }
            0x1000 -> handle1nnn(instruction)
            0x2000 -> handle2nnn(instruction)
            0x3000 -> handle3xkk(instruction)
            0x4000 -> handle4xkk(instruction)
            0x6000 -> handle6xkk(instruction)
            0x7000 -> handle7xkk(instruction)
            0x8000 -> {
                when (instruction.toInt() and 0x000F) {
                    0x0000 -> handle8xy0(instruction)
                    0x0001 -> handle8xy1(instruction)
                    0x0002 -> handle8xy2(instruction)
                    0x0003 -> handle8xy3(instruction)
                    0x0004 -> handle8xy4(instruction)
                    0x0005 -> handle8xy5(instruction)
                    0x0006 -> handle8xy6(instruction)
                    0x000E -> handle8xyE(instruction)
                    else -> {
                        unhandled(instruction)
                    }
                }
            }
            0x9000 -> handle9xy0(instruction)
            0xA000 -> handleAnnn(instruction)
            0xC000 -> handleCxkk(instruction)
            0xD000 -> handleDxyn(instruction)
            0xE000 -> {
                when (instruction.toInt() and 0x00FF) {
                    0x009E -> handleEx9E(instruction)
                    0x00A1 -> handleExA1(instruction)
                    else -> {
                        unhandled(instruction)
                    }
                }
            }
            0xF000 -> {
                when (instruction.toInt() and 0x00FF) {
                    0x0007 -> handleFx07(instruction)
                    0x000A -> handleFx0A(instruction)
                    0x0015 -> handleFx15(instruction)
                    0x0018 -> handleFx18(instruction)
                    0x001E -> handleFx1E(instruction)
                    0x0029 -> handleFx29(instruction)
                    0x0033 -> handleFx33(instruction)
                    0x0055 -> handleFx55(instruction)
                    0x0065 -> handleFx65(instruction)
                    else -> {
                        unhandled(instruction)
                    }
                }
            }
            else -> {
                unhandled(instruction)
            }
        }
    }

    private fun unhandled(instruction: Short) {
        System.out.printf("[Unhandled] 0x%x \n", instruction)
        return
    }

    private fun handle00E0() {
        memory.resetScreen()
        emulator.drawFlag = true

        println("CLS: Clearing the screen")
        incrementPC()
    }

    private fun handle00EE() {
        memory.SP--
        memory.PC = memory.stack[memory.SP.toInt()]

        println("RET: Returning")
        incrementPC()
    }

    private fun handle1nnn(instruction: Short) {
        val nnn = instruction and 0x0FFF
        println("JP: Jumping to $nnn")

        memory.PC = nnn.toUShort()
    }

    private fun handle2nnn(instruction: Short) {
        val nnn = instruction and 0x0FFF
        println("CALL: Calling $nnn")

        memory.stack[memory.SP.toInt()] = memory.PC
        memory.SP++
        memory.PC = nnn.toUShort()
    }

    private fun handle3xkk(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val kk = (instruction and 0x00FF).toUByte()
        val registerValue = memory.V[x.toInt()]

        if (registerValue == kk) {
            incrementPC()
            incrementPC()
            println("SE: $registerValue(V[$x]) == $kk(kk). Skipping!")
        } else {
            incrementPC()
            println("SE: $registerValue(V[$x]) != $kk(kk). No Skipping!")
        }
        print("STOP")
    }

    private fun handle4xkk(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val kk = (instruction and 0x00FF).toUByte()
        val registerValue = memory.V[x.toInt()]

        if (registerValue != kk) {
            incrementPC()
            incrementPC()
            println("SNE: $registerValue(V[$x]) != $kk(kk). Skipping!")
        } else {
            incrementPC()
            println("SNE: $registerValue(V[$x]) == $kk(kk). No Skipping!")
        }
    }

    private fun handle6xkk(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val kk = (instruction and 0x00FF).toUByte()

        println("LD: [6] Putting $kk into V[$x]")

        memory.V[x.toInt()] = kk
        incrementPC()
    }

    private fun handle7xkk(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val kk = (instruction and 0x00FF).toUByte()

        val sum = memory.V[x.toInt()] + kk
        memory.V[x.toInt()] = sum.toUByte()

        println("ADD: [7] Putting $sum into V[$x]")
        incrementPC()
    }

    private fun handle8xy0(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val regY = memory.V[y.toInt()]

        memory.V[x.toInt()] = regY

        println("LD: V[$x] = $regY")
        incrementPC()
    }

    private fun handle8xy1(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val regX = memory.V[x.toInt()]
        val regY = memory.V[y.toInt()]

        val finalVal = regX or regY
        memory.V[x.toInt()] = finalVal

        println("OR: V[$x] = $finalVal")
        incrementPC()
    }

    private fun handle8xy2(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val regX = memory.V[x.toInt()]
        val regY = memory.V[y.toInt()]

        val finalVal = regX and regY
        memory.V[x.toInt()] = finalVal

        println("AND: V[$x] = $finalVal")
        incrementPC()
    }

    private fun handle8xy3(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val regX = memory.V[x.toInt()]
        val regY = memory.V[y.toInt()]

        val finalVal = regX xor regY
        memory.V[x.toInt()] = finalVal

        println("XOR: V[$x] = $finalVal")
        incrementPC()
    }

    private fun handle8xy4(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val regX = memory.V[x.toInt()]
        val regY = memory.V[y.toInt()]

        memory.V[x.toInt()] = (memory.V[x.toInt()] + regY).toUByte()
        if (regY > 0xFFu - memory.V[x.toInt()])
            memory.V[0xF] = 1u //carry
        else
            memory.V[0xF] = 0u

        println("ADD: V[$x] = $regX + $regY")
        incrementPC()
    }

    private fun handle8xy5(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val regX = memory.V[x.toInt()]
        val regY = memory.V[y.toInt()]

        if (regY > regX)
            memory.V[0xF] = 0u // there is a borrow
        else
            memory.V[0xF] = 1u

        memory.V[x.toInt()] = (memory.V[x.toInt()] - regY).toUByte()

        println("SUB: V[$x] = $regX - $regY")
        incrementPC()
    }

    private fun handle8xy6(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val regX = memory.V[x.toInt()]

        memory.V[0xF] = regX and 0x1u
        val finalValue = (regX.toUInt() shr 1).toUByte()
        memory.V[x.toInt()] = finalValue

        println("SHR: V[$x] = $finalValue")
        incrementPC()
    }

    private fun handle8xyE(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8

        var mostSignificant = memory.V[x.toInt()] and 0x80u
        if (mostSignificant.toInt() != 0) {
            //If 0x10000000 -> set to 0x01
            mostSignificant = 0x01u
        }
        memory.V[0xF] = mostSignificant //Set VF to the least significant bit of Vx before the shift.

        //We have to cast it to unsigned int to work properly. If we don't do it, Bitwise operation does the cast
        //with sign, so the result is incorrect.
        val Vx = memory.V[x.toInt()] and 0xFFu
        memory.V[x.toInt()] = (Vx.toInt() shl 1).toUByte() // >>> operator means right shift one bit without sign propagation.

        println("8xyE hackjobbed (copy paste)")
        incrementPC()
    }

    private fun handle9xy0(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val regX = memory.V[x.toInt()]
        val regY = memory.V[y.toInt()]

        if (regX != regY) {
            incrementPC()
            incrementPC()
            println("SNE: V[$x]) != V[$y]). Skipping!")
        } else {
            incrementPC()
            println("SNE: V[$x]) == V[$y]). Not Skipping!")
        }
    }

    private fun handleAnnn(instruction: Short) {
        val nnn = instruction and 0x0FFF
        println("LD: Setting I to $nnn")

        memory.I = nnn.toUShort()
        incrementPC()
    }

    private fun handleCxkk(instruction: Short) {
        //TODO: Check if this actually works
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val kk = (instruction and 0x00FF).toUByte()

        val newVal = ThreadLocalRandom.current().nextInt(0, 255) and kk.toInt()
        memory.V[x.toInt()] = newVal.toUByte()

        println("RND: V[$x] = $newVal(delayTimer)?") //why delay??
        incrementPC()
    }

    private fun isBitSet(b: Byte, bit: Int): Boolean {
        return b.toUInt() and (1u shl bit) != 0u
    }

    private fun handleDxyn(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val y = (instruction.toUInt() and 0x00F0u) shr 4
        val nibble = instruction and 0x000F

        var future0xF = 0x0
        var readBytes: Byte = 0
        while (readBytes < nibble) {
            val currentByte = memory.read8(memory.I.toInt() + readBytes) //Read one byte
            for (i in 0 until 8) {
                //For every pixel calculate real coordinate
                val regX = memory.V[x.toInt()]
                val regY = memory.V[y.toInt()]
                val realX = (regX.toInt() + i) % 64
                val realY = (regY.toInt() + readBytes) % 32

                val previousPixel = (memory.display[realX + realY * 64]).toUInt() != 0u //Previous value of pixel
                val newPixel = previousPixel xor isBitSet(currentByte.toByte(), 7 - i) //XOR

                memory.display[realX + realY * 64] = if (newPixel) 1u else 0u

                if (previousPixel && newPixel) {
                    //A pixel has been erased
                    future0xF = 0x1
                }

            }
            readBytes++
        }
        memory.V[0xF] = future0xF.toUByte()
        emulator.drawFlag = true

        println("DRW: Drawing at x=$x y=$y")
        incrementPC()
    }

    private fun handleEx9E(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val registerValue = memory.V[x.toInt()]

        if (memory.keys[registerValue.toInt()].toUInt() != 1u) {
            incrementPC()
            println("SKP: $registerValue(V[$x]) is not pressed. Not skipping!")
        } else {
            incrementPC()
            incrementPC()
            println("SKP: $registerValue(V[$x]) is pressed. Skipping!")
        }
    }

    private fun handleExA1(instruction: Short) {
        val x = (instruction.toUInt() and 0x0F00u) shr 8
        val registerValue = memory.V[x.toInt()]

        if (memory.keys[registerValue.toInt()].toUInt() == 1u) {
            incrementPC()
            println("SKNP: $registerValue(V[$x]) is pressed. Not skipping!")
        } else {
            incrementPC()
            incrementPC()
            println("SKNP: $registerValue(V[$x]) is not pressed. Skipping!")
        }
    }

    private fun handleFx07(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        val newVal = memory.delayTimer
        memory.V[x.toInt()] = newVal

        println("LD: V[$x] = $newVal(delayTimer)")
        incrementPC()
    }

    private fun handleFx0A(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        var keyPressed = false
        for (i in 0 until memory.keys.size) {
            if (memory.keys[i].toUInt() != 0u) {
                memory.V[x.toInt()] = i.toUByte()
                keyPressed = true
            }
        }

        // If no key is pressed, return and try again.
        if (!keyPressed)
            return

        println("LD: Key Pressed")
        incrementPC()
    }

    private fun handleFx15(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        val newVal = memory.V[x.toInt()]
        memory.delayTimer = newVal

        println("LD: Set DelayTimer to $newVal")
        incrementPC()
    }

    private fun handleFx18(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        val newVal = memory.V[x.toInt()]
        memory.soundTimer = newVal

        println("LD: Set SoundTimer to $newVal")
        incrementPC()
    }

    private fun handleFx1E(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        if (memory.I + memory.V[x.toInt()] > 0xFFFu)
            memory.V[0xF] = 1u
        else
            memory.V[0xF] = 0u

        val newVal = memory.V[x.toInt()] + memory.I
        memory.I = newVal.toUShort()

        println("ADD: Setting I to $newVal")
        incrementPC()
    }

    private fun handleFx29(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        val newVal = memory.V[x.toInt()] * 5u
        memory.I = newVal.toUShort()

        println("LD: Setting I to $newVal")
        incrementPC()
    }

    private fun handleFx33(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8
        val regX = memory.V[x.toInt()].toUInt() and 0xffu //Get unsigned int from register Vx

        memory.write8((memory.I.toInt() + 0), ((regX / 100u).toUByte()))
        memory.write8((memory.I.toInt() + 1), ((regX.toInt() % 100) / 10).toUByte())
        memory.write8((memory.I.toInt() + 2), (regX.toInt() % 10).toUByte())

        println("LD: Storing BCDs in mem[I]")
        println("RegX: $regX")
        println(memory.read8(memory.I.toInt() + 0))
        println(memory.read8(memory.I.toInt() + 1))
        println(memory.read8(memory.I.toInt() + 2))
        incrementPC()
    }

    private fun handleFx55(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        var p = 0
        while (p <= x.toInt()) {
            memory.write8(((memory.I + p.toUInt()) and 0xFFFu).toInt(), memory.V[p])
            println(memory.V[p])
            p++
            // don't modify I intentionally to prevent bugs
        }

        println("LD: Finished Fx55")
        incrementPC()
    }

    private fun handleFx65(instruction: Short) {
        val x = instruction.toUInt() and 0x0F00u shr 8

        var p = 0
        while (p <= x.toInt()) {
            memory.V[p] = memory.read8(((memory.I + p.toUInt()) and 0xFFFu).toInt())
            println(memory.V[p])
            p++
            // don't modify I intentionally to prevent bugs
        }

        println("LD: Finished Fx65")
        incrementPC()
    }

    private fun incrementPC() {
        memory.PC = (memory.PC + 2u).toUShort()
    }

}
