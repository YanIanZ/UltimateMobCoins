package nl.chimpgamer.ultimatemobcoins.paper.hooks

import com.nexomc.nexo.api.NexoItems
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent
import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

class NexoHook(plugin: UltimateMobCoinsPlugin) : PluginHook(plugin, "Nexo") {
    private lateinit var nexoItemsLoadedEventListener: Listener

    override fun load() {
        if (isLoaded) return
        if (!canHook()) return

        nexoItemsLoadedEventListener = object : Listener {
            @EventHandler(priority = EventPriority.MONITOR)
            fun onNexoItemsLoaded(event: NexoItemsLoadedEvent) {
                plugin.shopMenus.values.forEach { it.loadAllItems() }
                plugin.debug { "[NexoItemsLoadedEvent] Reloaded items of all shop menus" }
            }
        }
        plugin.server.pluginManager.registerEvents(nexoItemsLoadedEventListener, plugin)
        isLoaded = true
        plugin.logger.info("Successfully loaded $pluginName hook!")
    }

    override fun unload() {
        if (this::nexoItemsLoadedEventListener.isInitialized) {
            HandlerList.unregisterAll(nexoItemsLoadedEventListener)
        }
        isLoaded = false
    }

    fun nexoItem(id: String): ItemStack? {
        if (!isLoaded) return null
        return NexoItems.itemFromId(id)?.build()
    }
}