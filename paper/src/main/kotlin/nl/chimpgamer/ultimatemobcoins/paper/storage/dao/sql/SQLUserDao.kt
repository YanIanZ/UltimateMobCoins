package nl.chimpgamer.ultimatemobcoins.paper.storage.dao.sql

import kotlinx.coroutines.withContext
import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import nl.chimpgamer.ultimatemobcoins.paper.models.User
import nl.chimpgamer.ultimatemobcoins.paper.storage.dao.UserDao
import nl.chimpgamer.ultimatemobcoins.paper.storage.sql.UserEntity
import nl.chimpgamer.ultimatemobcoins.paper.storage.sql.UsersTable
import nl.chimpgamer.ultimatemobcoins.paper.storage.sql.toUser
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

class SQLUserDao(private val plugin: UltimateMobCoinsPlugin) : UserDao {
    override suspend fun getAll(): Set<User> = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction { UserEntity.all() }.map { it.toUser(plugin) }.toSet()
    }

    override suspend fun getUser(uuid: UUID): User? = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction { UserEntity.findById(uuid) }?.toUser(plugin)
    }

    override suspend fun createUser(uuid: UUID, username: String): User = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction {
            UserEntity.new(uuid) {
                this.username = username
                this.coins = plugin.settingsConfig.mobCoinsStartingBalance.toBigDecimal(MathContext(3))
                this.coinsCollected = BigDecimal.ZERO
                this.coinsSpent = BigDecimal.ZERO
            }
        }.toUser(plugin)
    }

    override suspend fun setUsername(user: User, username: String) = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction {
            val userEntity = UserEntity[user.uuid]
            userEntity.username = username
        }
    }

    override suspend fun setCoins(user: User, coins: BigDecimal) = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction {
            val userEntity = UserEntity[user.uuid]
            userEntity.coins = user.coins
        }
    }

    override suspend fun setCoinsCollected(user: User, coinsCollected: BigDecimal) = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction {
            val userEntity = UserEntity[user.uuid]
            userEntity.coinsCollected = coinsCollected
        }
    }

    override suspend fun setCoinsSpent(user: User, coinsSpent: BigDecimal) = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction {
            val userEntity = UserEntity[user.uuid]
            userEntity.coinsSpent = coinsSpent
        }
    }

    override suspend fun getTopMobCoins(): List<User> = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction {
            UserEntity
                .let {
                    if (plugin.settingsConfig.mobCoinsLeaderboardShowZero) {
                        it.find { UsersTable.coins greater BigDecimal.ZERO }
                    } else {
                        it.all()
                    }
                }
                .orderBy(UsersTable.coins to SortOrder.DESC)
                .toList()
        }.map { it.toUser(plugin) }
    }

    override suspend fun getGrindTop(): List<User> = withContext(plugin.databaseManager.databaseDispatcher) {
        suspendTransaction {
            UserEntity
                .let {
                    if (plugin.settingsConfig.mobCoinsLeaderboardShowZero) {
                        it.find { UsersTable.coinsCollected greater BigDecimal.ZERO }
                    } else {
                        it.all()
                    }
                }
                .orderBy(UsersTable.coinsCollected to SortOrder.DESC)
                .toList()
        }.map { it.toUser(plugin) }
    }
}