/*
 * This file is part of mmoMinecraft (https://github.com/mmoMinecraftDev).
 *
 * mmoMinecraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Pet;

import mmo.Core.MMO;
import mmo.Core.MMOPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;

public class MMOPet extends MMOPlugin {

	static int config_max_per_player = 1;
	
	@Override
	public void onEnable() {
		super.onEnable();
		MMO.mmoPet = true;

		mmoSpoutListener sl = new mmoSpoutListener();
		pm.registerEvent(Type.CUSTOM_EVENT, sl, Priority.Normal, this);

		mmoPetEntityListener pel = new mmoPetEntityListener();
		pm.registerEvent(Type.ENTITY_TAME, pel, Priority.Highest, this);

		for (Player player : server.getOnlinePlayers()) {
			for (LivingEntity entity : player.getWorld().getLivingEntities()) {
				if (entity instanceof Wolf && ((Tameable) entity).isTamed()) {
					setTitle(player, entity, MMO.getSimpleName(entity, true));
				}
			}
		}
	}

	@Override
	public void loadConfiguration(Configuration cfg) {
		config_max_per_player = cfg.getInt("max_per_player", config_max_per_player);
//		cfg.getInt("Wolf.train." + Material.BONE.getId(), 75);
//		cfg.getInt("Wolf.food." + Material.PORK.getId(), 20);
//		cfg.getInt("Spider.train." + Material.SUGAR.getId(), 15);
	}

	@Override
	public void onDisable() {
		MMO.mmoPet = false;
		super.onDisable();
	}

	public class mmoPetEntityListener extends EntityListener {

		@Override
		public void onEntityTame(final EntityTameEvent event) {
			getServer().getScheduler().scheduleSyncDelayedTask(plugin,
					  new Runnable() {

						  @Override
						  public void run() {
							  LivingEntity entity = (LivingEntity) event.getEntity();
							  setTitle(entity, MMO.getSimpleName(entity, true));
						  }
					  });
		}
	}

	public class mmoSpoutListener extends SpoutListener {

		@Override
		public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
			Player player = event.getPlayer();
			for (LivingEntity entity : player.getWorld().getLivingEntities()) {
				if (entity instanceof Wolf && ((Tameable) entity).isTamed()) {
					if (player.equals(((Tameable) entity).getOwner())) {
						setTitle(entity, MMO.getSimpleName(entity, true));
					} else {
						setTitle(player, entity, MMO.getSimpleName(entity, true));
					}
				}
			}
		}
	}
}
