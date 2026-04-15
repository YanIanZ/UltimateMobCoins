package nl.chimpgamer.ultimatemobcoins.paper.listeners

import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PlayerInteractListener(private val plugin: UltimateMobCoinsPlugin) : Listener {

    @EventHandler
    suspend fun PlayerInteractEvent.onPlayerInteract() {
        if (hand !== EquipmentSlot.HAND) return
        if (!(action === Action.RIGHT_CLICK_BLOCK || action === Action.RIGHT_CLICK_AIR)) return
        val itemInHand = item ?: return
        val isMobCoinItem = plugin.mobCoinsManager.isMobCoinItem(itemInHand)
        if (!isMobCoinItem) return
        if (!plugin.settingsConfig.mobCoinsItemSelfRedeemable) return
        isCancelled = true
        plugin.mobCoinsManager.redeemMobCoinItem(player, itemInHand)
    }
}