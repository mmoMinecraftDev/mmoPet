/*
 * This file is part of mmoMinecraft (http://code.google.com/p/mmo-minecraft/).
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

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Pet;

import mmo.Core.mmo;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;

public class mmoPet extends JavaPlugin {

	protected static Server server;
	protected static PluginManager pm;
	protected static PluginDescriptionFile description;
	protected static mmo mmo;
	private int updateTask;

	@Override
	public void onEnable() {
		server = getServer();
		pm = server.getPluginManager();
		description = getDescription();

		mmo = mmo.create(this);
		mmo.mmoPet = true;
		mmo.setPluginName("Pet");

		mmo.log("loading " + description.getFullName());

		mmo.cfg.getBoolean("auto_update", true);
		mmo.cfg.getInt("max_per_player", 1);
		mmo.cfg.getInt("Wolf.train." + Material.BONE.getId(), 75);
		mmo.cfg.getInt("Wolf.food." + Material.PORK.getId(), 20);
		mmo.cfg.getInt("Spider.train." + Material.SUGAR.getId(), 15);
		mmo.cfg.save();

		mmoSpoutListener sl = new mmoSpoutListener();
		pm.registerEvent(Type.CUSTOM_EVENT, sl, Priority.Normal, this);

		mmoPetEntityListener pel = new mmoPetEntityListener();
		pm.registerEvent(Type.ENTITY_TAME, pel, Priority.Highest, this);

		for (Player player : server.getOnlinePlayers()) {
			for (LivingEntity entity : player.getWorld().getLivingEntities()) {
				if (entity instanceof Wolf && ((Tameable) entity).isTamed()) {
					mmo.setTitle(player, entity, mmo.getSimpleName(entity, true));
				}
			}
		}
	}

	@Override
	public void onDisable() {
		mmo.log("Disabled " + description.getFullName());
		mmo.autoUpdate();
		mmo.mmoPet = false;
	}

	public class mmoPetEntityListener extends EntityListener {

		@Override
		public void onEntityTame(EntityTameEvent event) {
			LivingEntity entity = (LivingEntity) event.getEntity();
			mmo.setTitle(entity, mmo.getSimpleName(entity, true));
		}
	}

	public class mmoSpoutListener extends SpoutListener {

		@Override
		public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
			Player player = event.getPlayer();
			for (LivingEntity entity : player.getWorld().getLivingEntities()) {
				if (entity instanceof Wolf && ((Tameable) entity).isTamed()) {
					if (player.equals(((Tameable)entity).getOwner())) {
						mmo.setTitle(entity, mmo.getSimpleName(entity, true));
					} else {
						mmo.setTitle(player, entity, mmo.getSimpleName(entity, true));
					}
				}
			}
		}
	}
}
