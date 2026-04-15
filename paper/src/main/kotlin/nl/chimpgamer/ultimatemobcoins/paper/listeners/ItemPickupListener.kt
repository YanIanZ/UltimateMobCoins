package nl.chimpgamer.ultimatemobcoins.paper.listeners

import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

class ItemPickupListener(private val plugin: UltimateMobCoinsPlugin) : Listener {

    @EventHandler(ignoreCancelled = true)
    suspend fun PlayerAttemptPickupItemEvent.onPlayerAttemptPickupItem() {
        if (!plugin.settingsConfig.mobCoinsDropsAutoRedeemOnPickup) {
            val itemStack = item.itemStack
            val isMobCoinItem = plugin.mobCoinsManager.isMobCoinItem(itemStack)
            if (!isMobCoinItem) return

            val playerInventory = player.inventory
            val mobCoinItemsInInventory = playerInventory.storageContents
                .withIndex()
                .filter { it.value != null }
                .filter { plugin.mobCoinsManager.isMobCoinItem(it.value!!) }

            if (mobCoinItemsInInventory.isEmpty()) return

            var cancel = false

            val valueToAdd = plugin.mobCoinsManager.getMobCoinValue(itemStack) ?: return

            for ((pos, mobCoinItem) in mobCoinItemsInInventory) {
                val mobCoinValue = plugin.mobCoinsManager.getMobCoinValue(mobCoinItem!!) ?: continue
                if (mobCoinValue >= 1000.toBigDecimal()) continue
                if (mobCoinValue + valueToAdd >= 1000.toBigDecimal()) continue // Does it fit in another existing coin?
                cancel = true

                playerInventory.setItem(pos, plugin.mobCoinsManager.createMobCoinItem(mobCoinValue + valueToAdd))
                break
            }
            if (cancel) {
                isCancelled = true
                item.remove()
            }
            return
        }
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