package net.saikatsune.uhc.commands;

import net.saikatsune.uhc.Game;
import net.saikatsune.uhc.enums.Scenarios;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ConfigCommand implements CommandExecutor {

    private final Game game = Game.getInstance();

    private final String mColor = game.getmColor();
    private final String sColor = game.getsColor();

    @Override
    public boolean onCommand(CommandSender player, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("config")) {
            player.sendMessage("§8§m-----------------------");
            player.sendMessage(mColor + ChatColor.BOLD +  "UHC Game Information");
            player.sendMessage("");
            if(!game.getGameManager().isTeamGame()) {
                player.sendMessage(sColor + "Game Type: " + mColor + "FFA");
            } else {
                player.sendMessage(sColor + "Game Type: " + mColor + "To" + game.getTeamManager().getTeamSize());
            }
            player.sendMessage(sColor + "Nether: " + mColor + game.getConfigManager().isNether());
            player.sendMessage(sColor + "Speed I/II: " + mColor + game.getConfigManager().isSpeed1() + "/" + game.getConfigManager().isSpeed2());
            player.sendMessage(sColor + "Strength I/II: " + mColor + game.getConfigManager().isStrength1() + "/" + game.getConfigManager().isStrength2());
            player.sendMessage(sColor + "Enderpearl Damage: " + mColor + game.getConfigManager().isEnderpearlDamage());
            player.sendMessage(sColor + "God Apples: " + mColor + "false");
            if(Scenarios.Horseless.isEnabled()) {
                player.sendMessage(sColor + "Horses: " + mColor + "false");
            } else {
                player.sendMessage(sColor + "Horses: " + mColor + "true");
            }
            player.sendMessage(sColor + "Horse Healing: " + mColor + "true");
            player.sendMessage(sColor + "Apple Rate: " + mColor + game.getConfigManager().getAppleRate() + "%");
            player.sendMessage(sColor + "Flint Rate: " + mColor + game.getConfigManager().getFlintRate() + "%");
            player.sendMessage(sColor + "Shears: " + mColor + game.getConfigManager().isShears());
            player.sendMessage(sColor + "Starter Food: " + mColor + game.getConfigManager().getStarterFood() + " Steaks");
            player.sendMessage(sColor + "Final Heal: " + mColor + game.getConfigManager().getFinalHeal() + " minute(s)");
            player.sendMessage(sColor + "Grace Period: " + mColor + game.getConfigManager().getGraceTime() + " minute(s)");
            player.sendMessage(sColor + "First Shrink: " + mColor + game.getConfigManager().getBorderTime() + " minute(s)");
            player.sendMessage(sColor + "Current Border: " + mColor + game.getConfigManager().getBorderSize());
            player.sendMessage("§8§m-----------------------");
        }
        return false;
    }
}
