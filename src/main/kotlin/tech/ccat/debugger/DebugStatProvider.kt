//// tech/ccat/debugger/DebugStatProvider.kt
//package tech.ccat.debugger
//
//import org.bukkit.entity.Player
//import tech.ccat.kstats.api.StatProvider
//import tech.ccat.kstats.model.PlayerStat
//import tech.ccat.kstats.model.StatType
//import java.util.*
//import java.util.concurrent.ConcurrentHashMap
//
//class DebugStatProvider : StatProvider {
//    // 存储玩家调试数值：UUID -> (属性类型 -> 调整值)
//    private val debugValues = ConcurrentHashMap<UUID, MutableMap<StatType, Double>>()
//
//    fun adjustStat(player: Player, statType: StatType, amount: Double) {
//        val playerData = debugValues.computeIfAbsent(player.uniqueId) { ConcurrentHashMap() }
//        playerData.merge(statType, amount) { a, b -> a + b }
//    }
//
//    override fun provideStats(player: Player): PlayerStat {
//        return debugValues[player.uniqueId].let { adjustments ->
//            PlayerStat(
//                health = adjustments?.getOrDefault(StatType.HEALTH, 0.0).,
//                defense = adjustments?.getOrDefault(StatType.DEFENSE, 0.0),
//                strength = adjustments?.getOrDefault(StatType.STRENGTH, 0.0),
//                speed = adjustments?.getOrDefault(StatType.SPEED, 0.0),
//                critChance = adjustments?.getOrDefault(StatType.CRIT_CHANCE, 0.0),
//                critDamage = adjustments?.getOrDefault(StatType.CRIT_DAMAGE, 0.0),
//                wisdom = adjustments?.getOrDefault(StatType.WISDOM, 0.0),
//                baseDamage = adjustments?.getOrDefault(StatType.BASE_DAMAGE, 0.0),
//                damageMultiplier = adjustments?.getOrDefault(StatType.DAMAGE_MULTIPLIER, 0.0),
//                healing = adjustments?.getOrDefault(StatType.HEALING, 0.0)
//            )
//        }
//    }
//}