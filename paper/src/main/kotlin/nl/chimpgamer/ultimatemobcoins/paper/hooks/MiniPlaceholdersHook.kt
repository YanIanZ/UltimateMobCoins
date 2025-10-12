package nl.chimpgamer.ultimatemobcoins.paper.hooks

import io.github.miniplaceholders.api.Expansion
import io.github.miniplaceholders.kotlin.audience
import net.kyori.adventure.text.minimessage.tag.Tag
import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import nl.chimpgamer.ultimatemobcoins.paper.models.menu.RefreshableShopMenu
import nl.chimpgamer.ultimatemobcoins.paper.utils.NumberFormatter
import org.bukkit.entity.Player

class MiniPlaceholdersHook(plugin: UltimateMobCoinsPlugin) : PluginHook(plugin, "MiniPlaceholders") {
    private lateinit var expansion: Expansion

    override fun load() {
        if (!canHook()) return

        expansion = Expansion.builder("ultimatemobcoins")
            .globalPlaceholder("shop_refresh_time") { argumentQueue, _ ->
                val shopName = argumentQueue.popOr("shop_refresh_time tag requires a valid rotating shop name.").value()
                val menu = plugin.shopMenus[shopName] ?: return@globalPlaceholder null
                if (menu !is RefreshableShopMenu) return@globalPlaceholder null

                Tag.preProcessParsed(plugin.formatDuration(menu.getTimeRemaining()))
            }
            .globalPlaceholder("spinner_price") { _, _ ->
                Tag.preProcessParsed(plugin.spinnerConfig.usageCosts.toString())
            }
            .globalPlaceholder("leaderboard_mobcoins") { argumentQueue, _ ->
                val position = argumentQueue.popOr("need position").value()
                val type = argumentQueue.popOr("need type").value()

                plugin.leaderboardManager.getTopMobCoinsPosition(position.toInt())?.let { user ->
                    Tag.preProcessParsed(
                        when (type.lowercase()) {
                            "name" -> user.username
                            "value" -> user.coins.toString()
                            "value_formatted" -> user.coinsPretty
                            else -> "null"
                        }
                    )
                } ?: Tag.preProcessParsed("...")
            }
            .globalPlaceholder("leaderboard_mobcoins_grind") { argumentQueue, _ ->
                val position = argumentQueue.popOr("need position").value()
                val type = argumentQueue.popOr("need type").value()

                plugin.leaderboardManager.getTopMobCoinsGrindPosition(position.toInt())?.let { user ->
                    Tag.preProcessParsed(
                        when (type.lowercase()) {
                            "name" -> user.username
                            "value" -> user.coins.toString()
                            "value_formatted" -> user.coinsPretty
                            else -> "null"
                        }
                    )
                } ?: Tag.preProcessParsed("...")
            }
            .audience<Player>("balance") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(user.coinsAsDouble.toString())
            }
            .audience<Player>("balance_formatted") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(user.coinsPretty)
            }
            .audience<Player>("balance_fixed") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.FIXED_FORMAT.format(user.coinsAsDouble))
            }
            .audience<Player>("balance_commas") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.COMMAS_FORMAT.format(user.coinsAsDouble))
            }
            .audience<Player>("balance_formatted_compact") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.compactDecimalFormat(user.coins))
            }
            .audience<Player>("collected") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(user.coinsCollectedAsDouble.toString())
            }
            .audience<Player>("collected_formatted") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(user.coinsCollectedPretty)
            }
            .audience<Player>("collected_fixed") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.FIXED_FORMAT.format(user.coinsCollectedAsDouble))
            }
            .audience<Player>("collected_commas") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.COMMAS_FORMAT.format(user.coinsCollectedAsDouble))
            }
            .audience<Player>("collected_formatted_compact") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.compactDecimalFormat(user.coinsCollected))
            }
            .audience<Player>("spent") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(user.coinsSpentAsDouble.toString())
            }
            .audience<Player>("spent_formatted") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(user.coinsSpentPretty)
            }
            .audience<Player>("spent_fixed") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.FIXED_FORMAT.format(user.coinsSpentAsDouble))
            }
            .audience<Player>("spent_commas") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.COMMAS_FORMAT.format(user.coinsSpentAsDouble))
            }
            .audience<Player>("spent_formatted_compact") { audience, _, _ ->
                val user = plugin.userManager.getIfLoaded(audience) ?: return@audience null
                Tag.preProcessParsed(NumberFormatter.compactDecimalFormat(user.coinsSpent))
            }
            .build()
        expansion.register()
        isLoaded = true
        plugin.logger.info("Successfully loaded $pluginName hook!")
    }

    override fun unload() {
        if (this::expansion.isInitialized && expansion.registered()) {
            expansion.unregister()
        }
        isLoaded = false
    }
}