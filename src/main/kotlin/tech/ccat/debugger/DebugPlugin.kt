package tech.ccat.debugger

import org.bukkit.plugin.java.JavaPlugin
import tech.ccat.kstats.KStats

class DebugPlugin : JavaPlugin() {
    private lateinit var statProvider: DebugStatProvider

    override fun onEnable() {
        // 获取主插件实例
        val kstats = server.pluginManager.getPlugin("KStats") as? KStats
            ?: throw IllegalStateException("KStats plugin not found")

        // 初始化提供器
        statProvider = DebugStatProvider()
        kstats.statManager.registerProvider(statProvider)

        // 注册命令
        getCommand("ksdebug")?.setExecutor(DebugCommand(this, statProvider, kstats.statManager))
        logger.info("DebugPlugin enabled")
    }

    override fun onDisable() {
        (server.pluginManager.getPlugin("KStats") as? KStats)?.statManager?.unregisterProvider(statProvider)
        logger.info("DebugPlugin disabled")
    }
}