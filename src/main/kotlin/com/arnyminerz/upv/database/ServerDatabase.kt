package com.arnyminerz.upv.database

import com.arnyminerz.upv.database.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.slf4j.LoggerFactory

object ServerDatabase {
    private const val DRIVER_POSTGRES = "org.postgresql.Driver"
    private const val DRIVER_H2 = "org.h2.Driver"

    private val tables = listOf(Users)

    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var database: Database

    fun initialize() {
        val databaseHost = System.getenv("DATABASE_HOST") ?: "./database.db"
        val databasePort = System.getenv("DATABASE_PORT")
        val databaseName = System.getenv("DATABASE_NAME")
        val databaseFile = System.getenv("DATABASE_FILE") ?: "./database.db"
        val databaseUser = System.getenv("DATABASE_USER") ?: ""
        val databasePassword = System.getenv("DATABASE_PASSWORD") ?: ""
        val databaseDriver = System.getenv("DATABASE_DRIVER") ?: "org.postgresql.Driver"

        logger.info("Connecting to database...")
        database = Database.connect(
            url = when (databaseDriver) {
                DRIVER_POSTGRES -> "jdbc:postgresql://$databaseHost:$databasePort/$databaseName"
                DRIVER_H2 -> "jdbc:h2:file:$databaseFile"
                else -> error("Unsupported database driver: $databaseDriver")
            },
            driver = databaseDriver,
            user = databaseUser,
            password = databasePassword,
        )

        logger.info("Connected to database.")
        logger.info("Initializing tables...")
        for (table in tables) {
            SchemaUtils.createMissingTablesAndColumns(table)
        }
    }
}
