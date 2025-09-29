package nl.chimpgamer.ultimatemobcoins.paper.hooks

import io.th0rgal.oraxen.api.OraxenItems
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent
import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

class OraxenHook(plugin: UltimateMobCoinsPlugin) : PluginHook(plugin, "Oraxen") {
    private lateinit var oraxenItemsLoadedEventListener: Listener

    override fun load() {
        if (isLoaded) return
        if (!canHook()) return

        oraxenItemsLoadedEventListener = object : Listener {
            @EventHandler(priority = EventPriority.MONITOR)
            fun onOraxenItemsLoaded(event: OraxenItemsLoadedEvent) {
                plugin.shopMenus.values.forEach { it.loadAllItems() }
                plugin.debug { "[OraxenItemsLoadedEvent] Reloaded items of all shop menus" }
            }
        }
        plugin.server.pluginManager.registerEvents(oraxenItemsLoadedEventListener, plugin)
        isLoaded = true
        plugin.logger.info("Successfully loaded $pluginName hook!")
    }

    override fun unload() {
        if (this::oraxenItemsLoadedEventListener.isInitialized) {
            HandlerList.unregisterAll(oraxenItemsLoadedEventListener)
        }
        isLoaded = false
    }

    fun oraxenItem(id: String): ItemStack? {
        if (!isLoaded) return null
        return OraxenItems.getItemById(id)?.build()
    }
}