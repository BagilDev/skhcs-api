package kr.mcv.bagil.skhcsApi

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SkhcsApi : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        Bukkit.getScheduler()
            .scheduleSyncRepeatingTask(this, PostRunnable(server, config), 0L, 20L * config.getLong("delay"))
    }
}