/*
 * This file is part of mmoPet <http://github.com/mmoMinecraftDev/mmoPet>.
 *
 * mmoPet is free software: you can redistribute it and/or modify
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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

public class MMOPet extends MMOPlugin {
	static int config_max_per_player = 1;

	@Override
	public void onEnable() {
		super.onEnable();

		pm.registerEvents(new mmoListener(), this);

		for (Player player : server.getOnlinePlayers()) {
			for (LivingEntity entity : player.getWorld().getLivingEntities()) {
				if (entity instanceof Tameable && ((Tameable) entity).isTamed()) {
					setTitle(player, entity, MMO.getSimpleName(entity, true));
				}
			}
		}
	}

	@Override
	public void loadConfiguration(FileConfiguration cfg) {
		config_max_per_player = cfg.getInt("max_per_player", config_max_per_player);
		//		cfg.getInt("Wolf.train." + Material.BONE.getId(), 75);
		//		cfg.getInt("Wolf.food." + Material.PORK.getId(), 20);
		//		cfg.getInt("Spider.train." + Material.SUGAR.getId(), 15);
	}

	public class mmoListener implements Listener {
		@EventHandler
		public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
			Player player = event.getPlayer();
			for (LivingEntity entity : player.getWorld().getLivingEntities()) {
				if (entity instanceof Tameable && ((Tameable) entity).isTamed()) {
					if (player.equals(((Tameable) entity).getOwner())) {
						setTitle(entity, MMO.getSimpleName(entity, true));
					} else {
						setTitle(player, entity, MMO.getSimpleName(entity, true));
					}
				}
			}
		}

		@EventHandler
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
}
