package nl.chimpgamer.ultimatemobcoins.paper.listeners

import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import nl.chimpgamer.ultimatemobcoins.paper.extensions.getBoolean
import nl.chimpgamer.ultimatemobcoins.paper.extensions.pdc
import nl.chimpgamer.ultimatemobcoins.paper.utils.NamespacedKeys
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

class ItemPickupListener(private val plugin: UltimateMobCoinsPlugin) : Listener {

    @EventHandler(ignoreCancelled = true)
    suspend fun PlayerAttemptPickupItemEvent.onPlayerAttemptPickupItem() {
        if (!plugin.settingsConfig.mobCoinsDropsAutoRedeemOnPickup) return
        val itemStack = item.itemStack
        val isMobCoinItem = plugin.mobCoinsManager.isMobCoinItem(itemStack)
        if (!isMobCoinItem) return
        isCancelled = true
        item.remove()
        plugin.mobCoinsManager.redeemMobCoinItem(player, itemStack)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun InventoryPickupItemEvent.onInventoryPickupItem() {
        if (inventory.type !== InventoryType.HOPPER) return
        if (plugin.settingsConfig.mobCoinsDropsAllowHopperPickup) return
        val itemStack = item.itemStack
        val isMobCoinItem = plugin.mobCoinsManager.isMobCoinItem(itemStack)
        if (!isMobCoinItem) return
        isCancelled = true
    }
}