package io.github.rypofalem.rainbow;

import com.winthier.custom.event.CustomRegisterEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class RainBowPlugin extends JavaPlugin implements Listener, CommandExecutor{
	public static RainBowPlugin instance;
	static Random random = new Random();

	@Override
	public void onEnable(){
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("rainbow").setExecutor(this);
		saveDefaultConfig();
	}

	@EventHandler
	public void onCustomRegister(CustomRegisterEvent event) {
		event.addItem(new RainBowItem());
		event.addEntity(new RainBowArrowBundleEntity());
		event.addEntity(new RainBowArrowEntity());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args == null || args.length < 1) return false;
		if(args[0].equalsIgnoreCase("reload")){
			reloadConfig();
			RainBowItem.loadConfig();
			RainBowArrowEntity.loadConfig();
			sender.sendMessage("Configuration reloaded.");
		}else{
			return false;
		}
		return true;
	}

}