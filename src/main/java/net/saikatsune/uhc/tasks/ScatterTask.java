package net.saikatsune.uhc.tasks;

import net.saikatsune.uhc.Game;
import net.saikatsune.uhc.enums.Scenarios;
import net.saikatsune.uhc.gamestate.GameState;
import net.saikatsune.uhc.handler.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;
import net.saikatsune.uhc.util.CC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class ScatterTask {

    private final Game game = Game.getInstance();

    private final String prefix = game.getPrefix();

    private final String mColor = game.getmColor();
    private final String sColor = game.getsColor();

    private int taskID;

    private final List<UUID> playersToScatter = new ArrayList<>();
    private final List<UUID> playersScattered = new ArrayList<>();

    private final List<UUID> queuedPlayers = new ArrayList<>();

    public void runTask() {
        game.setChatMuted(true);

        for (int i = 0; i < 100; i++) {
            Bukkit.broadcastMessage("");
        }

        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            if(game.getPlayers().contains(allPlayers.getUniqueId())) {
                if(!game.getSpectators().contains(allPlayers)) {
                    Random randomLocation = new Random();

                    int xPositive = randomLocation.nextInt(game.getConfigManager().getBorderSize() - 1);
                    int xNegative = randomLocation.nextInt(game.getConfigManager().getBorderSize() - 1);
                    int x = (Math.random() <= 0.5) ? xPositive : -xNegative;


                    int zPositive = randomLocation.nextInt(game.getConfigManager().getBorderSize() - 1);
                    int zNegative = randomLocation.nextInt(game.getConfigManager().getBorderSize() - 1);
                    int z = (Math.random() <= 0.5) ? zPositive : -zNegative;

                    int y = Bukkit.getWorld("uhc_world").getHighestBlockYAt(x, z);

                    Location location = new Location(Bukkit.getWorld("uhc_world"), x, y ,z);

                    game.getGameManager().setScatterLocation(allPlayers, location);

                    playersToScatter.add(allPlayers.getUniqueId());
                }
            }
        }

        Bukkit.broadcastMessage(prefix + sColor + "Starting scatter of all players!");

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(game, new BukkitRunnable() {
            @Override
            public void run() {

                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                    if(game.getSpectators().contains(allPlayers)) {
                        playersToScatter.remove(allPlayers.getUniqueId());
                    }
                }

                if(playersToScatter.size() >= 5) {
                    for (int y = 0; y < 5; y++) {
                        queuedPlayers.add(playersToScatter.get(y));
                    }
                } else {
                    queuedPlayers.addAll(playersToScatter);
                }

                for (int i = 0; i < queuedPlayers.size(); i++) {
                    Player toScatter = Bukkit.getPlayer(queuedPlayers.get(i));

                    if(toScatter != null) {
                        toScatter.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -5));
                        toScatter.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 127));

                        toScatter.teleport(game.getScatterLocation().get(toScatter));

                        playersToScatter.remove(toScatter.getUniqueId());
                        playersScattered.add(toScatter.getUniqueId());
                    } else {
                        playersToScatter.remove(playersToScatter.get(i));
                    }

                    queuedPlayers.clear();
                }

                if (playersToScatter.size() == 0) {
                    Bukkit.getScheduler().cancelTask(taskID);
                    Bukkit.broadcastMessage(prefix + ChatColor.GREEN + "Finished scatter of all players!");

                    if(game.getGameManager().isTeamGame()) {
                        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                            if(game.getPlayers().contains(allPlayers.getUniqueId())) {
                                if(game.getTeamNumber().get(allPlayers.getUniqueId()) == -1) {
                                    game.getTeamManager().createTeam(allPlayers.getUniqueId());
                                }
                            }
                        }

                        for (TeamHandler teamHandler : game.getTeamManager().getTeams().values()) {
                            for (UUID toTeleport : teamHandler.getTeamMembers()) {
                                Player playerToTeleport = Bukkit.getPlayer(toTeleport);
                                if(playerToTeleport != null) {
                                    if(teamHandler.getTeamLeader() != null) {
                                        if(teamHandler.getTeamMembers().contains(teamHandler.getTeamLeader())) {
                                            playerToTeleport.teleport(Bukkit.getPlayer(teamHandler.getTeamLeader()));
                                        }
                                    }
                                }
                            }
                        }

                        Bukkit.broadcastMessage(prefix + sColor + "All teams have been teleported to their leaders!");
                    }

                    for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                        if(game.getPlayers().contains(allPlayers.getUniqueId())) {
                            game.getLoggedPlayers().add(allPlayers.getUniqueId());
                            game.getWhitelisted().add(allPlayers.getUniqueId());

                            game.getGameManager().resetPlayer(allPlayers);
                        }
                    }

                    game.getGameManager().setWhitelisted(true);

                    Bukkit.broadcastMessage(prefix + sColor + "All players have been whitelisted!");
                    Bukkit.broadcastMessage(prefix + mColor + "The game starts in 10 seconds!");

                    String uhcType =
                            Game.getInstance().getConfig().getInt("SETUP.FIRST-SHRINK") == 5 ? mColor + "&lUHC &7┃ &fMini"
                                    : game.getConfigManager().isSpeed1() ? mColor + "&lUHC &7┃ &fFast"
                                    : game.getConfigManager().isSpeed2() ? mColor + "&lUHC &7┃ &fFast"
                                    : game.getConfigManager().isNether() ? mColor + "&lUHC &7┃ &fNether"
                                    : Game.getInstance().getConfig().getInt("SETUP.GRACE-TIME") == 30 ? mColor + "&lUHC &7┃ &fCup"
                                    : mColor + "&lUltra Hardcore";

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            game.getGameStateManager().setGameState(GameState.INGAME);
                            Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle(new Title(CC.translate(uhcType),CC.translate("&fgame started."),20,70,20)));
                            Bukkit.broadcastMessage(prefix + mColor + "The game has started. Good Luck!");

                            for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                                for (PotionEffect potionEffect : allPlayers.getActivePotionEffects()) {
                                    allPlayers.removePotionEffect(potionEffect.getType());
                                }
                            }

                            if(Scenarios.GoneFishing.isEnabled()) {
                                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                                    if(game.getPlayers().contains(allPlayers.getUniqueId())) {
                                        allPlayers.getInventory().addItem(new ItemStack(Material.ANVIL, 20));

                                        allPlayers.setLevel(30000);

                                        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
                                        fishingRod.addUnsafeEnchantment(Enchantment.LUCK, 250);
                                        fishingRod.addUnsafeEnchantment(Enchantment.LURE, 3);
                                        fishingRod.addUnsafeEnchantment(Enchantment.DURABILITY, 100);
                                        allPlayers.getInventory().addItem(fishingRod);
                                    }
                                }
                            }

                            if(Scenarios.InfiniteEnchant.isEnabled()) {
                                for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                                    if(game.getPlayers().contains(allPlayers.getUniqueId())) {
                                        allPlayers.setLevel(30000);
                                        allPlayers.getInventory().addItem(new ItemStack(Material.ENCHANTMENT_TABLE, 64));
                                        allPlayers.getInventory().addItem(new ItemStack(Material.ANVIL, 64));
                                        allPlayers.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 64));
                                        allPlayers.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 64));
                                    }
                                }
                            }
                        }
                    }.runTaskLater(game, 10 * 20);
                }
            }
        }, 0, 10);
    }

    public void cancelTask() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public List<UUID> getPlayersToScatter() {
        return playersToScatter;
    }

    public List<UUID> getPlayersScattered() {
        return playersScattered;
    }
}
