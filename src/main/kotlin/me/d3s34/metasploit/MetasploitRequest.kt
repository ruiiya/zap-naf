package me.d3s34.metasploit

import me.d3s34.lib.command.Commandline
import me.d3s34.lib.command.buildCommand
import androidx.compose.runtime.snapshots.SnapshotStateList

import androidx.compose.runtime.*

data class MetasploitRequest(
    val modulo: String,
    val options: SnapshotStateList<Pair<String, String>>? = null
)

fun MetasploitRequest.toCommand(): Commandline {
    return buildCommand {
        path = "./msfconsole"
        shortFlag = buildMap {
            put("r", "docker/msfconsole.rc")
            put("y", "\$APP_HOME/config/database.yml")
        }
    }
}