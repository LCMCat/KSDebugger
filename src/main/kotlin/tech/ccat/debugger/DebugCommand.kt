package tech.ccat.debugger

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.ccat.kstats.model.StatType
import tech.ccat.kstats.service.StatManager
import java.util.logging.Level

class DebugCommand(
    private val plugin: DebugPlugin,
    private val provider: DebugStatProvider,
    private val statManager: StatManager
) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.size < 2) {
            sender.sendMessage("§c用法: /ksdebug <属性名> <调整值>")
            return true
        }

        // 解析属性类型
        val statType = try {
            StatType.valueOf(args[0])
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("§c无效属性类型，可用值：")
            sender.sendMessage(StatType.values().joinToString { it.name })
            return true
        }

        // 解析数值
        val amount = try {
            args[1].toDouble()
        } catch (e: NumberFormatException) {
            sender.sendMessage("§c无效数值格式")
            return true
        }

        // 获取目标玩家（默认为自己）
        val target = if (sender is Player) sender else null
            ?: run {
                sender.sendMessage("§c控制台使用时必须指定玩家")
                return true
            }

        // 执行调整
        provider.adjustStat(target, statType, amount)
        sender.sendMessage("§a成功调整属性 ${statType.name}: ${if (amount >= 0) "+" else ""}$amount")

        // 触发属性更新
        Bukkit.getScheduler().runTask(plugin, Runnable{
            try {
                statManager.updateStats(target)
                sender.sendMessage("§a属性更新完成")
            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "更新属性失败", e)
            }
        })

        return true
    }
}