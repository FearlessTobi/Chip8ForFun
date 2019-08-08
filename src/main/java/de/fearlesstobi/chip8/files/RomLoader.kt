package de.fearlesstobi.chip8.files

import java.nio.file.Files
import java.nio.file.Paths

object RomLoader {

    fun getRomBytes(path: String): ByteArray? {
        return Files.readAllBytes(Paths.get(path))
    }

}
