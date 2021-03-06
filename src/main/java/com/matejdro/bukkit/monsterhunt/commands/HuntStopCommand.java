package com.matejdro.bukkit.monsterhunt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Setting;
import com.matejdro.bukkit.monsterhunt.Settings;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntStopCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (args.length < 1 && Settings.globals.getBoolean(Setting.HuntZoneMode.getString(), false)) {
            args = new String[] { "something" };
        } else if (args.length < 1) {
            Util.Message("Usage: /huntstop [World Name]", sender);
            return true;
        } else if (HuntWorldManager.getWorld(args[0]) == null) {
            Util.Message("There is no such world!", sender);
            return true;
        }
        MonsterHuntWorld world = HuntWorldManager.getWorld(args[0]);
        world.stop();
        world.manual = false;
        world.waitday = true;
        return true;
    }

}
