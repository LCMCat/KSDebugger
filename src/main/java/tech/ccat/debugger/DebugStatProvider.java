package tech.ccat.debugger;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.ccat.kstats.api.StatProvider;
import tech.ccat.kstats.model.PlayerStat;
import tech.ccat.kstats.model.StatType;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DebugStatProvider implements StatProvider {
    // 存储玩家调试数值：UUID -> (属性类型 -> 调整值)
    private final ConcurrentHashMap<UUID, Map<StatType, Double>> debugValues = new ConcurrentHashMap<>();

    public void adjustStat(Player player, StatType statType, double amount) {
        Map<StatType, Double> playerData = debugValues.computeIfAbsent(
                player.getUniqueId(),
                k -> new ConcurrentHashMap<>()
        );
        playerData.merge(statType, amount, Double::sum);
    }

    @NotNull
    @Override
    public PlayerStat provideStats(Player player) {
        Map<StatType, Double> adjustments = debugValues.get(player.getUniqueId());
        final Map<StatType, Double> stats = adjustments != null ? adjustments : Collections.emptyMap();

        return new PlayerStat(
                stats.getOrDefault(StatType.HEALTH, 0.0),
                stats.getOrDefault(StatType.DEFENSE, 0.0),
                stats.getOrDefault(StatType.STRENGTH, 0.0),
                stats.getOrDefault(StatType.SPEED, 0.0),
                stats.getOrDefault(StatType.CRIT_CHANCE, 0.0),
                stats.getOrDefault(StatType.CRIT_DAMAGE, 0.0),
                stats.getOrDefault(StatType.WISDOM, 0.0),
                stats.getOrDefault(StatType.BASE_DAMAGE, 0.0),
                stats.getOrDefault(StatType.DAMAGE_MULTIPLIER, 0.0),
                stats.getOrDefault(StatType.HEALING, 0.0)
        );
    }
}