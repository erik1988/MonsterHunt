package com.matejdro.bukkit.monsterhunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.HuntZone;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Setting;
import com.matejdro.bukkit.monsterhunt.Settings;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntTeleCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)) {
            sender.sendMessage("Sorry, but you need to execute this command as player.");
            return true;
        }
        Player player = (Player) sender;
        MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());
        if (!Settings.globals.getBoolean(Setting.HuntZoneMode.getString(), false) || world == null || world.getWorld() == null)
            return false;

        boolean permission = !sender.hasPermission("monsterhunt.noteleportrestrictions");

        if (world.state == 0 && permission) {
            Util.Message(world.settings.getString(Setting.MessageHuntTeleNoHunt), player);
            return true;
        } else if (!world.Score.containsKey(player.getName()) && world.settings.getBoolean(Setting.EnableSignup) && permission) {
            Util.Message(world.settings.getString(Setting.MessageHuntTeleNotSignedUp), player);
            return true;
        }

        world.tplocations.put(player, player.getLocation());
        player.teleport(HuntZone.teleport);
        return true;
    }

}
