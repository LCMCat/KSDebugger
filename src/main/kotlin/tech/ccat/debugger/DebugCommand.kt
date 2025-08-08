package tech.ccat.debugger

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.ccat.kstats.api.KStatsAPI
import tech.ccat.kstats.model.StatType
import java.util.logging.Level

/**
 * KStats调试命令执行器
 *
 * 提供实时调整玩家属性的能力，用于开发和测试场景
 *
 * @property plugin 调试插件实例
 * @property provider 统计提供者实例
 * @property kstatsAPI KStats核心API服务
 */
class DebugCommand(
    private val plugin: DebugPlugin,
    private val provider: DebugStatProvider,
    private val kstatsAPI: KStatsAPI
) : CommandExecutor {

    /**
     * 处理/ksdebug命令的执行
     *
     * 参数结构: /ksdebug <属性名> <调整值> [玩家]
     *
     * @param sender 命令发送者
     * @param command 命令实例
     * @param label 命令标签
     * @param args 命令参数
     * @return 命令处理结果（true表示成功）
     */
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        // 检查参数数量
        if (args.isEmpty() || args.size < 2) {
            sendUsage(sender)
            return true
        }

        // 解析属性类型
        val statType = try {
            StatType.valueOf(args[0].uppercase())
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("§c无效属性类型！可用类型:")
            sender.sendMessage(StatType.values().joinToString(", ") { it.name })
            return true
        }

        // 解析数值
        val amount = try {
            args[1].toDouble()
        } catch (e: NumberFormatException) {
            sender.sendMessage("§c无效数值格式！请输入数字")
            return true
        }

        // 获取目标玩家
        val target = getTargetPlayer(sender, args)
        if (target == null) {
            sender.sendMessage("§c找不到目标玩家")
            return true
        }

        // 执行属性调整
        performStatAdjust(sender, statType, amount, target)
        return true
    }

    /**
     * 发送命令使用说明
     */
    private fun sendUsage(sender: CommandSender) {
        sender.sendMessage("§eKStats调试命令用法:")
        sender.sendMessage("§6/ksdebug <属性名> <调整值> [玩家]")
        sender.sendMessage("§6属性列表: ${StatType.values().joinToString { it.name }}")
        sender.sendMessage("§6示例: /ksdebug STRENGTH 10.0 或 /ksdebug SPEED -20 PlayerName")
    }

    /**
     * 获取目标玩家实例
     *
     * 优先级:
     * 1. 如果参数指定了玩家名，查找在线玩家
     * 2. 如果发送者是玩家，使用自己
     */
    private fun getTargetPlayer(sender: CommandSender, args: Array<out String>): Player? {
        // 如果指定了玩家名参数
        if (args.size >= 3) {
            return Bukkit.getPlayer(args[2])?.takeIf { it.isOnline }
        }

        // 发送者必须是玩家
        return (sender as? Player)?.takeIf { it.isOnline }
    }

    /**
     * 执行属性调整操作
     *
     * @param sender 命令发送者
     * @param statType 属性类型
     * @param amount 调整值
     * @param target 目标玩家
     */
    private fun performStatAdjust(
        sender: CommandSender,
        statType: StatType,
        amount: Double,
        target: Player
    ) {
        // 执行属性调整
        provider.adjustStat(target, statType, amount)
        sender.sendMessage("§a成功调整玩家§b${target.name}§a的属性:")
        sender.sendMessage("§7$statType ${if (amount >= 0) "§a+$amount" else "§c$amount"}")

        // 同步执行属性更新（确保在主线程）
        Bukkit.getScheduler().runTask(plugin, Runnable {
            try {
                // 使用API服务更新玩家状态
                kstatsAPI.forceUpdate(target)
                sender.sendMessage("§a属性更新完成 - 当前值: ${kstatsAPI.getStat(target, statType)}")
            } catch (e: Exception) {
                // 错误处理
                plugin.logger.log(Level.SEVERE, "更新玩家属性时出错", e)
                sender.sendMessage("§c更新玩家属性时出错: ${e.message}")
            }
        })
    }
}