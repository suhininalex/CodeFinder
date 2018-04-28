package com.github.suhininalex.codefinder.leveldb

import mu.KotlinLogging
import org.iq80.leveldb.Logger

object LevelDbLogger: Logger {
    private val logger = KotlinLogging.logger("LevelDB")

    override fun log(message: String) {
        logger.debug { message }
    }

}