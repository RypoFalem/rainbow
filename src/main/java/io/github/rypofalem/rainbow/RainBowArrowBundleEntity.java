package io.github.rypofalem.rainbow;

import com.winthier.custom.CustomPlugin;
import com.winthier.custom.entity.CustomEntity;
import com.winthier.custom.entity.EntityWatcher;
import com.winthier.custom.entity.TickableEntity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class RainBowArrowBundleEntity implements CustomEntity, TickableEntity {
	static final String ID = "rainbow:arrowbundle";

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
		((Watcher)entityWatcher).onTick();
	}

	@Override
	public Watcher createEntityWatcher(Entity entity) {
		return new Watcher((Arrow)entity, this);
	}

	@Getter
	public final class Watcher implements EntityWatcher{
		private final Arrow entity;
		private final RainBowArrowBundleEntity customEntity;
		double radius = 4;
		int arrows = 0;
		int explodeTicks = 15;

		Watcher(Arrow entity, RainBowArrowBundleEntity customEntity){
			this.entity = entity;
			this.customEntity = customEntity;
		}

		void onTick(){
			if(getEntity().isOnGround()) getEntity().remove();
			if(getEntity().getTicksLived() < explodeTicks) return;
			new DropArrowTask().runTaskTimer(RainBowPlugin.instance, 0, 1);
			getEntity().getWorld().playSound(getEntity().getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
					SoundCategory.WEATHER, 5, 1);
			getEntity().remove();
		}

		class DropArrowTask extends BukkitRunnable{
			private ArrayList<Location> spawnLocations;

			DropArrowTask(){
				spawnLocations = new ArrayList<>();
				for(double x = -radius; x <= radius; x++){
					for(double z = -radius; z <= radius; z++){
						spawnLocations.add(entity.getLocation().clone().add(x, 0, z));
					}
				}
				Collections.shuffle(spawnLocations, RainBowPlugin.random);
			}

			@Override
			public void run() {
				for(int i = 0; i<2 && arrows > 0; i++){
					Location loc = spawnLocations.get(arrows%spawnLocations.size());
					Arrow arrow = (Arrow)CustomPlugin.getInstance().getEntityManager()
							.spawnEntity(loc, RainBowArrowEntity.ID).getEntity();
					arrow.setShooter(entity.getShooter());
					arrow.setVelocity(new Vector(0, -.5, 0));
					arrows--;
				}
				if(arrows <= 0){
					this.cancel();
					return;
				}
			}
		}
	}
}