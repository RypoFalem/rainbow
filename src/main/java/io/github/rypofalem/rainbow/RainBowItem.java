package io.github.rypofalem.rainbow;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.item.CustomItem;
import com.winthier.custom.item.ItemDescription;
import com.winthier.custom.item.UpdatableItem;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class RainBowItem implements CustomItem, UpdatableItem {
	static ItemStack template;
	static ItemDescription description;
	static final String ID = "rainbow:rainbow";
	static int maxArrow;
	static int explodeTicks;
	static double arrowDensity;
	static int radius;

	static final ChatColor[] rainbowColors = {ChatColor.RED, ChatColor.GOLD, ChatColor.GREEN,
			ChatColor.BLUE, ChatColor.LIGHT_PURPLE};

	RainBowItem(){
		loadConfig();
		template = new ItemStack(Material.BOW);
		description.apply(template);
	}

	public static void loadConfig(){
		Configuration config = RainBowPlugin.instance.getConfig();
		maxArrow = config.getInt("IMaxArrows");
		radius = config.getInt("IRadius");
		arrowDensity = config.getDouble("DArrowDensity");
		explodeTicks = config.getInt("IArrowTimer");
		description = new ItemDescription();
		description.setDisplayName(rainbowizeString(config.getString("description.displayName")));
		description.setCategory(config.getString("description.category"));
		description.setDescription(config.getString("description.description"));
		description.setUsage(String.format(config.getString("description.usage"), maxArrow, explodeTicks/20f));
	}

	@Override
	public void updateItem(ItemStack itemStack){
		description.apply(itemStack);
	}

	@Override
	public String getCustomId() {
		return ID;
	}

	@Override
	public ItemStack spawnItemStack(int amount) {
		return template.clone();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onArrowLoose(EntityShootBowEvent event){
		if(!(event.getEntity() instanceof Player)) return;
		updateItem(event.getBow());
		if(!(event.getProjectile() instanceof Arrow)) return;
		Player player = (Player) event.getEntity();
		Arrow arrow = (Arrow) event.getProjectile();
		int ammo = 0;
		if(player.getGameMode() != GameMode.CREATIVE) {
			for (int slot = 0; slot < player.getInventory().getSize() && ammo < maxArrow; slot++) {
				ItemStack item = player.getInventory().getItem(slot);
				if (!isAmmo(item)) continue;
				if (ammo + item.getAmount() <= maxArrow) {
					ammo += item.getAmount();
					item = new ItemStack(Material.AIR);
				} else {
					item.setAmount(item.getAmount() - maxArrow + ammo);
					ammo = maxArrow;
				}
				player.getInventory().setItem(slot, item);
			}
		}else{
			ammo = maxArrow;
		}
		if(ammo < 1){
			event.setCancelled(true);
			return;
		}
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, RainBowPlugin.random.nextFloat() + .5f);
		RainBowArrowBundleEntity.Watcher watcher = (RainBowArrowBundleEntity.Watcher) CustomPlugin.getInstance().getEntityManager()
				.spawnEntity(arrow.getLocation(), RainBowArrowBundleEntity.ID);
		Arrow arrowBundle = watcher.getEntity();
		watcher.arrows =  (int) (ammo * Math.pow(radius*2+1, 2) * arrowDensity / maxArrow);
		watcher.radius = radius;
		watcher.explodeTicks = explodeTicks;
		arrowBundle.setVelocity(arrow.getVelocity());
		arrowBundle.setShooter(arrow.getShooter());
		arrow.remove();
	}

	public static boolean isAmmo(ItemStack item){
		return item != null && item.getType() == Material.ARROW && !item.hasItemMeta();
	}

	public static String rainbowizeString(String input){
		if(input == null) return null;
		input = ChatColor.stripColor(input);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<input.length(); i++){
			sb.append(rainbowColors[i%rainbowColors.length]);
			sb.append(input.charAt(i));
		}
		sb.append(ChatColor.RESET);
		return sb.toString();
	}
}