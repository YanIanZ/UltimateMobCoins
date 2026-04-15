package nl.chimpgamer.ultimatemobcoins.paper.listeners

import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerInventoryListener(private val plugin: UltimateMobCoinsPlugin) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        if (!isValidMergeAction(event)) return
        val currentItem = event.currentItem ?: return
        val cursor = event.cursor

        if (cursor.type != currentItem.type) return

        val player = event.whoClicked as Player

        val mobCoinManager = plugin.mobCoinsManager
        if (!mobCoinManager.isMobCoinItem(cursor)) return
        if (!mobCoinManager.isMobCoinItem(currentItem)) return

        event.isCancelled = true

        val value1 = mobCoinManager.getMobCoinValue(cursor) ?: return
        val value2 = mobCoinManager.getMobCoinValue(currentItem) ?: return

        val newItem = mobCoinManager.createMobCoinItem(value1 + value2)
        event.currentItem = newItem

        player.setItemOnCursor(null)
    }

    private fun isValidMergeAction(event: InventoryClickEvent): Boolean {
        return when (event.action) {
            InventoryAction.PLACE_ALL,
            InventoryAction.PLACE_ONE,
            InventoryAction.SWAP_WITH_CURSOR -> true
            else -> false
        }
    }
}