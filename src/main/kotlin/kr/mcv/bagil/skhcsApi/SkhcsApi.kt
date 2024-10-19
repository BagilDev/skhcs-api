package kr.mcv.bagil.skhcsApi

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SkhcsApi : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(this, PostRunnable(this), 0L, 20L * config.getLong("delay"))
    }
}