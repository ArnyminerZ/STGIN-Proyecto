package com.arnyminerz.upv.database

import com.arnyminerz.upv.database.table.Matches
import com.arnyminerz.upv.database.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

object ServerDatabase {
    private const val DRIVER_POSTGRES = "org.postgresql.Driver"
    private const val DRIVER_H2 = "org.h2.Driver"

    private val tables = listOf(Users, Matches)

    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var database: Database

    suspend fun initialize() {
        val databaseHost = System.getenv("DATABASE_HOST") ?: "localhost"
        val databasePort = System.getenv("DATABASE_PORT") ?: "5432"
        val databaseName = System.getenv("DATABASE_NAME")
        val databaseFile = System.getenv("DATABASE_FILE") ?: "./database"
        val databaseUser = System.getenv("DATABASE_USER") ?: ""
        val databasePassword = System.getenv("DATABASE_PASSWORD") ?: ""
        val databaseDriver = System.getenv("DATABASE_DRIVER") ?: DRIVER_H2

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
        for (table in tables) invoke {
            SchemaUtils.create(table)
        }
    }

    /**
     * Invokes the given block of code within a transaction context.
     *
     * @param block The code block to be executed within the transaction.
     * The block has access to the [Transaction] receiver for performing operations within the transaction.
     */
    suspend operator fun <Result> invoke(
        context: CoroutineContext? = null,
        block: suspend Transaction.() -> Result
    ): Result = newSuspendedTransaction(context, database, statement = block)

}
