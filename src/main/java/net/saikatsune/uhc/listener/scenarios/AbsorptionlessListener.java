package net.saikatsune.uhc.listener.scenarios;

import net.saikatsune.uhc.Game;
import net.saikatsune.uhc.enums.Scenarios;
import net.saikatsune.uhc.gamestate.states.IngameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;

public class AbsorptionlessListener implements Listener {

    private final Game game = Game.getInstance();

    @EventHandler
    public void handlePlayerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if ((event.getItem().getType() != null) &&
                (event.getItem().getType() == Material.GOLDEN_APPLE)) {
            if(game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                if(Scenarios.Absorptionless.isEnabled()) {
                    Bukkit.getScheduler().runTaskLater(game, () -> {
                        player.removePotionEffect(PotionEffectType.ABSORPTION);
                    }, 1);
                }
            }
        }
    }

}
