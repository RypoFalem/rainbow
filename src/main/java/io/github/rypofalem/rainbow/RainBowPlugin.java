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
		switch(args[0].toLowerCase()){
			case "reload":
				reloadConfig();
				RainBowItem.loadConfig();
				RainBowArrowEntity.loadConfig();
				sender.sendMessage("Configuration reloaded.");
				break;
			case "set" :
				if(args.length < 4){
					sender.sendMessage("/rainbow set <type> <key> <value>");
					return true;
				}
				switch(args[1].toLowerCase()){

					case "string" :
						StringBuilder stringValue = new StringBuilder(args[3]);
						for(int i = 4; i < args.length; i++){
							stringValue.append(" ");
							stringValue.append(args[i]);
						}
						getConfig().set(args[2], stringValue.toString());
						break;

					case "int" :
						int intValue;
						try{
							intValue = Integer.parseInt(args[3]);
						} catch (NumberFormatException nfe){
							sender.sendMessage(args[3] + " is not a int.");
							return true;
						}
						getConfig().set(args[2], intValue);
						break;

					case "double" :
					case "float" :
					case "decimal" :
						double decValue;
						try{
							decValue = Double.parseDouble(args[3]);
						} catch (NumberFormatException nfe){
							sender.sendMessage(args[3] + " is not a decimal number.");
							return true;
						}
						getConfig().set(args[2], decValue);
						break;

					default :
						sender.sendMessage("/rainbow set <type> <key> <value>\nTypes: string, int, decimal");
						return true;
				}
				saveConfig();
				sender.sendMessage(String.format("set %s to %s (%s)\nRemember to /rainbow reload", args[1], args[2], args[3]));
				break;
			default : return false;
		}
		return true;
	}

}