package me.d3s34.lib.command

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

internal class CommandTest {

    private lateinit var command: Command

    @BeforeTest
    fun init() {
        command = buildCommand {
            path = "echo"
            args = listOf("hello", "world")
            shortFlag = mapOf("n" to null)
        }
    }

    @Test
    fun getEscapedArgs() {
        assertEquals(listOf( "-n", "hello", "world"), command.escapedArgs)
    }

    @Test
    fun toCommandline() {
        assertEquals("echo -n hello world", command.toCommandline())
    }

    @Test
    fun testToString() {
        assertEquals("echo -n hello world", command.toString())

    }

    @Test
    fun getPath() {
        assertEquals("echo", command.path)
    }
}