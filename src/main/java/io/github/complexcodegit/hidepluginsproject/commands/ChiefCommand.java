package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.LanguageManager;
import io.github.complexcodegit.hidepluginsproject.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class ChiefCommand implements CommandExecutor {
    private HashMap<UUID, String> selectGroup = new HashMap<>();
    private HashMap<UUID, String> selectWorld = new HashMap<>();
    private HashMap<UUID, String> selectGlobal = new HashMap<>();
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    private LanguageManager languageManager;
    public ChiefCommand(HidePluginsProject plugin, GroupManager groupManager, LanguageManager languageManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
        this.languageManager = languageManager;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args) {
        FileConfiguration groups = plugin.getGroups();
        List<String> groupList = groupManager.getGroups();
        List<String> serverWorlds = groupManager.getWorlds();
        if(!cmd.getName().equalsIgnoreCase("hproject") || !(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("hidepluginsproject.hproject") && !groupManager.getCommands(player, false).contains("/hproject")) {
            return false;
        }

        if(args.length == 1){
            /*
             /hproject help
             */
            if(args[0].equalsIgnoreCase("help")){
                if(!selectGroup.containsKey(player.getUniqueId())){
                    List<String> lines = languageManager.getList("commands.help.no-group-selected");
                    for(String line : lines)
                        player.sendMessage(plugin.colors(line));
                } else {
                    List<String> lines = languageManager.getList("commands.help.group-selected.1");
                    for(String line : lines)
                        player.sendMessage(plugin.colors(line));
                }
            }
            /*
             /hproject reload
             */
            else if(args[0].equalsIgnoreCase("reload")) {
                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.reload")));
                plugin.reloadGroups();
                plugin.reloadLanguages();
                plugin.reloadPlayers();
                plugin.reloadConfig();
            }
            /*
             /hproject finish
             */
            else if(args[0].equalsIgnoreCase("finish")) {
                if(selectGroup.containsKey(player.getUniqueId())) {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.finish").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    selectGroup.remove(player.getUniqueId());
                    selectWorld.remove(player.getUniqueId());
                    selectGlobal.remove(player.getUniqueId());
                } else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
            }
        }
        else if(args.length == 2){
            /*
             /hproject help <page>
             */
            if(args[0].equalsIgnoreCase("help")){
                if(selectGroup.containsKey(player.getUniqueId())){
                    if(args[1].equalsIgnoreCase("1")){
                        List<String> lines1 = languageManager.getList("commands.help.group-selected.1");
                        for(String line : lines1)
                            player.sendMessage(plugin.colors(line));
                    } else if(args[1].equalsIgnoreCase("2")){
                        List<String> lines2 = languageManager.getList("commands.help.group-selected.2");
                        for(String line : lines2)
                            player.sendMessage(plugin.colors(line));
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.page-not-exist")));
                    }
                } else {
                    player.sendMessage(plugin.prefix + plugin.colors(languageManager.getInternal("commands.select-group-first")));
                }
            }
            /*
             /hproject select <global>
             */
            else if(args[0].equalsIgnoreCase("select")) {
                if(args[1].equalsIgnoreCase("global")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(!selectGlobal.containsKey(player.getUniqueId())) {
                            if(groups.get("groups." + selectGroup.get(player.getUniqueId()) + ".global", true) != null) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix + plugin.colors(languageManager.getInternal("commands.world.deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                selectGlobal.put(player.getUniqueId(), "global");
                                player.sendMessage(plugin.prefix + plugin.colors(languageManager.getInternal("commands.global.selected")));
                            } else {
                                player.sendMessage(plugin.prefix + plugin.colors(languageManager.getInternal("commands.global.not-exist").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                            }
                        } else {
                            player.sendMessage(plugin.prefix + plugin.colors(languageManager.getInternal("commands.global.already-selected")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix + plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject add <global, commands, tabs>
             */
            else if(args[0].equalsIgnoreCase("add")){
                /*
                 /hproject add <commands, tabs>
                 */
                if(args[1].equalsIgnoreCase("commands") || args[1].equalsIgnoreCase("tabs")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())) {
                            if(Utils.getItem("§aAdd Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aAdd Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.prefix+"§7You already have a book.");
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                ItemStack book = new ItemStack(Material.WRITABLE_BOOK, 1);
                                BookMeta meta = (BookMeta)book.getItemMeta();
                                if(args[1].equalsIgnoreCase("commands")){
                                    meta.setDisplayName("§aAdd Commands §8- §7HidePlugins Project");
                                } else {
                                    meta.setDisplayName("§aAdd Tabs §8- §7HidePlugins Project");
                                }
                                if(selectWorld.containsKey(player.getUniqueId())){
                                    meta.setLore(Arrays.asList("§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectWorld.get(player.getUniqueId())));
                                } else {
                                    meta.setLore(Arrays.asList("§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectGlobal.get(player.getUniqueId())));
                                }
                                book.setItemMeta(meta);
                                player.getInventory().setItem(slot, book);
                                if(player.getInventory().getHeldItemSlot() == slot){
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20), 4);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20), 4);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 3);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 5);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 7);
                                }
                            } else {
                                player.sendMessage(plugin.prefix+"§7There is no space in your hotbar.");
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                /*
                 /hproject add global
                 */
                if(args[1].equalsIgnoreCase("global")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(!groups.contains("groups."+selectGroup.get(player.getUniqueId())+".global", true)) {
                            selectGlobal.put(player.getUniqueId(), "global");
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.set").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.commands", "");
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.tab", "");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.already-set")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject remove <commands, tabs>
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("commands") || args[1].equalsIgnoreCase("tabs")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())) {
                            if(Utils.getItem("§aRemove Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aRemove Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.prefix+"§7You already have a book.");
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                ItemStack book = new ItemStack(Material.WRITABLE_BOOK, 1);
                                BookMeta meta = (BookMeta)book.getItemMeta();
                                if(args[1].equalsIgnoreCase("commands")){
                                    meta.setDisplayName("§aRemove Commands §8- §7HidePlugins Project");
                                } else {
                                    meta.setDisplayName("§aRemove Tabs §8- §7HidePlugins Project");
                                }
                                if(selectWorld.containsKey(player.getUniqueId())){
                                    meta.setLore(Arrays.asList("§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectWorld.get(player.getUniqueId())));
                                } else {
                                    meta.setLore(Arrays.asList("§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectGlobal.get(player.getUniqueId())));
                                }
                                book.setItemMeta(meta);
                                player.getInventory().setItem(slot, book);
                                if(player.getInventory().getHeldItemSlot() == slot){
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20), 4);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20), 4);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 3);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 5);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 7);
                                }
                            } else {
                                player.sendMessage(plugin.prefix+"§7There is no space in your hotbar.");
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
            }
        }
        else if(args.length == 3) {
            /*
             /hproject add <inherit, world> [inheritname, worldname]
             */
            if(args[0].equalsIgnoreCase("add")){
                /*
                 /hproject add inherit [inheritname]
                 */
                if(args[1].equalsIgnoreCase("inherit")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(selectGroup.get(player.getUniqueId()))){
                                if(groups.contains("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", true)){
                                    String inherit = groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    if(!list.contains(args[2])){
                                        list.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", String.join(", ", list));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.add").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.already-add").replace("[GROUP]", selectGroup.get(player.getUniqueId())).replace("[INHERIT]", args[2])));
                                    }
                                } else {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.add").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", args[2]);
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.you-can-not-inherit")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                /*
                 /hproject add world [worldname]
                 */
                if(args[1].equalsIgnoreCase("world")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(!groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.deselected")));
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.set").replace("[WORLD]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".commands", "");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".tab", "");
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.already-set").replace("[GROUP]", selectGroup.get(player.getUniqueId())).replace("[WORLD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.server-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject remove inherit [inheritname]
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("inherit")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(selectGroup.get(player.getUniqueId()))){
                                if(groupManager.getInherit(selectGroup.get(player.getUniqueId())).contains(args[2])) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.remove").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    String inherit = groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.remove(args[2]);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                } else {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.not-valid")));
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.you-can-not-inherit")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject select <world, group> [worldname, groupname]
             */
            else if(args[0].equalsIgnoreCase("select")) {
                /*
                 /hproject select world [worldname]
                 */
                if(args[1].equalsIgnoreCase("world")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.deselected")));
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.selected").replace("[WORLD]", args[2])));
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.not-exist")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.server-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                /*
                 /hproject select group [groupname]
                 */
                else if(args[1].equalsIgnoreCase("group")) {
                    if(!selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])) {
                            selectGroup.put(player.getUniqueId(), args[2]);
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.selected").replace("[GROUP]", args[2])));
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.first-ends-group").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject set help [true, false]
             */
            else if(args[0].equalsIgnoreCase("set")) {
                if(args[1].equalsIgnoreCase("help")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(args[2].equals("true") || args[2].equals("false")) {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.custom-help.set").replace("[BOOLEAN]", args[2])));
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.custom-help.enable", Boolean.valueOf(args[2]));
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.custom-help.not-valid")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject create group [groupname]
             */
            else if(args[0].equalsIgnoreCase("create")) {
                if(args[1].equalsIgnoreCase("group")) {
                    if(!selectGroup.containsKey(player.getUniqueId())) {
                        if(!groupList.contains(args[2])) {
                            selectGroup.put(player.getUniqueId(), args[2]);
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.set").replace("[GROUP]", args[2])));
                            groups.set("groups." + args[2] + ".options.inheritances", "");
                            groups.set("groups." + args[2] + ".options.custom-help.enable", true);
                            groups.createSection("groups."+args[2]+".options.custom-help.worlds");
                            groups.createSection("groups."+args[2]+".worlds");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.already-set").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.first-ends-group").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
            }
        }
        else {
            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
        }
        return false;
    }
}
