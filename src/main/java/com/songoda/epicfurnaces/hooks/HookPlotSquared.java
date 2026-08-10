package com.songoda.epicfurnaces.hooks;

import com.plotsquared.bukkit.BukkitPlatform;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import com.songoda.epicfurnaces.api.utils.ProtectionPluginHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Rewritten against modern PlotSquared (7.x): the old hook used the
 * long-gone {@code com.intellectualcrafters.plot.api.PlotAPI} facade and
 * {@code com.plotsquared.bukkit.BukkitMain}. Both were replaced wholesale by
 * a {@code com.plotsquared.core}/{@code com.plotsquared.bukkit} split, with
 * {@link BukkitPlatform} (still extending {@code JavaPlugin}, same as the
 * old {@code BukkitMain}) as the plugin's main class.
 * {@link BukkitUtil#adapt(Location)} converts a Bukkit {@code Location}
 * into PlotSquared's own location type, whose {@code getPlot()} resolves
 * the {@link Plot} occupying it (null if unclaimed/not in a plot world).
 * {@link Plot#isAdded(java.util.UUID)} covers denied/owner/member/trusted
 * membership in one call, which is a closer match to "may this player build
 * here" than the old hook's owner-only identity check. See
 * CLAUDE.md/PLAN.md.
 */
public class HookPlotSquared implements ProtectionPluginHook {

    @Override
    public JavaPlugin getPlugin() {
        return JavaPlugin.getPlugin(BukkitPlatform.class);
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        Plot plot = BukkitUtil.adapt(location).getPlot();
        return plot == null || plot.isAdded(player.getUniqueId());
    }

}
