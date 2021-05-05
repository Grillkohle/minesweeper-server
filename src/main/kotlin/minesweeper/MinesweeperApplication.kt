package minesweeper

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MinesweeperApplication

fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    logger.info { "RUN APP ..." }
    runApplication<MinesweeperApplication>(*args)
}
