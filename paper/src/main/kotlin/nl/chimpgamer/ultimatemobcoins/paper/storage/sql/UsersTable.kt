package nl.chimpgamer.ultimatemobcoins.paper.storage.sql

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import java.math.BigDecimal

object UsersTable : UUIDTable("users", "uuid") {
    val username: Column<String> = varchar("username", 16)
    val coins: Column<BigDecimal> = decimal("coins", 12, 3)
    val coinsCollected: Column<BigDecimal> = decimal("coins_collected", 12, 3)
    val coinsSpent: Column<BigDecimal> = decimal("coins_spent", 12, 3)
}