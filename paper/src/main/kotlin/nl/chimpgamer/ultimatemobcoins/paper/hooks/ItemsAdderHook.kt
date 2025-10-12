package nl.chimpgamer.ultimatemobcoins.paper.hooks

import dev.lone.itemsadder.api.CustomStack
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent
import nl.chimpgamer.ultimatemobcoins.paper.UltimateMobCoinsPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

class ItemsAdderHook(plugin: UltimateMobCoinsPlugin) : PluginHook(plugin, "ItemsAdder") {
    private lateinit var itemsAdderItemsLoadedEventListener: Listener

    override fun load() {
        if (isLoaded) return
        if (!canHook()) return

        itemsAdderItemsLoadedEventListener = object : Listener {
            @EventHandler(priority = EventPriority.MONITOR)
            fun onItemsAdderLoadData(event: ItemsAdderLoadDataEvent) {
                plugin.shopMenus.values.forEach { it.loadAllItems() }
                plugin.debug { "[ItemsAdderLoadDataEvent] Reloaded items of all shop menus" }
            }
        }
        plugin.server.pluginManager.registerEvents(itemsAdderItemsLoadedEventListener, plugin)
        isLoaded = true
        plugin.logger.info("Successfully loaded $pluginName hook!")
    }

    override fun unload() {
        if (this::itemsAdderItemsLoadedEventListener.isInitialized) {
            HandlerList.unregisterAll(itemsAdderItemsLoadedEventListener)
        }
        isLoaded = false
    }

    fun itemsAdderItem(id: String): ItemStack? {
        if (!isLoaded) return null
        return CustomStack.getInstance(id)?.itemStack
    }
}