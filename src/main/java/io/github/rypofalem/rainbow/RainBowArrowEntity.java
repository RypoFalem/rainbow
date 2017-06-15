package io.github.rypofalem.rainbow;


import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityContext;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.entity.TickableEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class RainBowArrowEntity implements CustomEntity, TickableEntity, Listener {
	boolean ignoreNextEvent = false;
	static final String ID = "rainbow:arrow";
	static final float[][] colors = {
			{1, 0, 0}, 	//red
			{1, .5f, 0},//orange
			{1, 1, 0},	//yellow
			{0, 1, 0},	//green
			{0, 0, 1},	//blue
			{.5f, 0, 1},//violet
	};
	static double damage;

	RainBowArrowEntity(){
		loadConfig();
	}

	static void loadConfig(){
		Configuration config = RainBowPlugin.instance.getConfig();
		damage = config.getDouble("DDamage");
	}

	@Override
	public String getCustomId() {
		return ID;
	}

	@Override
	public Entity spawnEntity(Location location) {
		return location.getWorld().spawnEntity(location, EntityType.ARROW);
	}

	@Override
	public void onTick(EntityWatcher entityWatcher) {
		Location loc = entityWatcher.getEntity().getLocation();
		float[] color = colors[ Math.abs((loc.getBlockY()/6)) % colors.length ];
		loc.getWorld().spawnParticle(Particle.SPELL_MOB, loc, 0, color[0],color[1],color[2], 1);
		if(entityWatcher.getEntity().isOnGround()) entityWatcher.getEntity().remove();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event, EntityContext context) {
		if(ignoreNextEvent) return;
		if(event.getDamager() != context.getEntity()) return;
		event.setCancelled(true);
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity) event.getEntity();
		ignoreNextEvent = true;
		entity.damage(damage,event.getDamager());
		ignoreNextEvent = false;
	}
}