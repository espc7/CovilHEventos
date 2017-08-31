package me.herobrinedobem.heventos.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.utils.ConfigUtil;
import me.herobrinedobem.heventos.utils.Cuboid;

public class EventoUtils {

	public static Location getLocation(YamlConfiguration config, String local) {
		if (config.getString(local).split(";").length == 4) {
			String world = config.getString(local).split(";")[0];
			double x = Double.parseDouble(config.getString(local).split(";")[1]);
			double y = Double.parseDouble(config.getString(local).split(";")[2]);
			double z = Double.parseDouble(config.getString(local).split(";")[3]);
			return new Location(HEventos.getHEventos().getServer().getWorld(world), x, y, z);
		} else if (config.getString(local).split(";").length == 6) {
			String world = config.getString(local).split(";")[0];
			double x = Double.parseDouble(config.getString(local).split(";")[1]);
			double y = Double.parseDouble(config.getString(local).split(";")[2]);
			double z = Double.parseDouble(config.getString(local).split(";")[3]);
			float yaw = Float.parseFloat(config.getString(local).split(";")[4]);
			float pitch = Float.parseFloat(config.getString(local).split(";")[5]);
			return new Location(HEventos.getHEventos().getServer().getWorld(world), x, y, z, yaw, pitch);
		} else {
			return null;
		}
	}

	public static boolean hasEventoOcorrendo() {
		if (HEventos.getHEventos().getEventosController().getEvento() != null) {
			return true;
		}
		return false;
	}

	public static EventoBaseAPI getEventoOcorrendo() {
		return HEventos.getHEventos().getEventosController().getEvento();
	}

	public static Cuboid getCuboID(Location loc1, Location loc2) {
		return new Cuboid(loc1, loc2);
	}

	public static ConfigUtil getMessagesConfig() {
		return HEventos.getHEventos().getConfigUtil();
	}

	public static List<EventoBaseAPI> getExternalEventos() {
		return HEventos.getHEventos().getExternalEventos();
	}

	public static boolean isInventoryEmpty(Player p) {
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = p.getInventory().getContents()).length;
		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			if ((item != null) && (item.getType() != Material.AIR)) {
				return true;
			}
		}
		int j1 = (arrayOfItemStack = p.getInventory().getArmorContents()).length;
		for (int i = 0; i < j1; i++) {
			ItemStack item = arrayOfItemStack[i];
			if ((item != null) && (item.getType() != Material.AIR)) {
				return true;
			}
		}
		return false;
	}
}
