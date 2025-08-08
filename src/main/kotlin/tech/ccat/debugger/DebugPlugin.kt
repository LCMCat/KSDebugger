package tech.ccat.debugger

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tech.ccat.kstats.api.KStatsAPI
import tech.ccat.kstats.api.StatProvider

class DebugPlugin : JavaPlugin() {
    private lateinit var statProvider: DebugStatProvider

    /**
     * 插件启用时调用
     */
    override fun onEnable() {
        // 获取KStatsAPI服务
        val service = getService()
        if (service == null) {
            logger.warning("KStats API 服务未找到，调试功能不可用")
            return
        }

        // 初始化并提供器
        statProvider = DebugStatProvider()
        service.registerProvider(statProvider)

        // 注册调试命令
        getCommand("ksdebug")?.setExecutor(DebugCommand(this, statProvider, service))
        logger.info("KStats调试插件已启用")
    }

    /**
     * 插件禁用时调用
     */
    override fun onDisable() {
        try {
            // 尝试获取服务进行反注册
            getService()?.unregisterProvider(statProvider)
        } catch (e: Exception) {
            logger.warning("取消注册统计提供者时出错: ${e.message}")
        }
        logger.info("KStats调试插件已禁用")
    }

    private fun getService(): KStatsAPI? {
        return Bukkit.getServicesManager().getRegistration(KStatsAPI::class.java)?.provider
    }
}