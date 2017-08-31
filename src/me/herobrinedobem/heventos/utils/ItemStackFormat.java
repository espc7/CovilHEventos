package me.herobrinedobem.heventos.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackFormat {

	/*
	 * # ID:DATA, QUANTIDADE, nome:, lore:, enchantName:level # O plugin usa o
	 * "name:" para anunciar o nome do item # Em nome: use "_" para fazer espaço,
	 * exemplo: # nome:Protection_4 = Protection 4
	 */
	
	public static ItemStack getItem(String line) {
		String[] lines = line.split(" ");
		String tid = lines[0];
		int quantity = Integer.parseInt(lines[1]);
		byte data = 0;
		int id;
		if (tid.contains(":")) {
			id = Integer.parseInt(tid.split(":")[0]);
			data = Byte.parseByte(tid.split(":")[1]);
		} else {
			id = Integer.parseInt(tid);
		}
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(id), quantity, (byte) data);
		ItemMeta meta = item.getItemMeta();
		Double chance = 100.0;
		if (lines.length > 1) {
			List<String> lore = new ArrayList<String>();
			for (int i = 2; i < lines.length; ++i) {
				String temp = lines[i];
				if (temp.startsWith("nome:")) {
					meta.setDisplayName(lines[i].split("nome:")[1].replace("&", "§").replace("_", " "));
				} else if (temp.startsWith("lore:")) {
					lore.add(lines[i].split("lore:")[1].replace("&", "§").replace("_", " "));
				} else if (temp.startsWith("chance:")) {
					chance = Double.parseDouble(lines[i].split("chance:")[1]);
				} else {
					Enchantment enchant = getEnchant(temp.split(":")[0]);
					if (enchant != null) {
						int power = 1;
						if (temp.contains(":")) {
							power = Integer.parseInt(temp.split(":")[1]);
						}
						meta.addEnchant(enchant, power, true);
					}
				}
			}
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		if (chance(chance)) {
			return item;
		}
		return null;
	}

	private static boolean chance(double e) {
		double d = Math.random();
		return d < e / 100.0;
	}

	private static Enchantment getEnchant(String name) {
		if (name.equalsIgnoreCase("sharpness") || name.equalsIgnoreCase("afiada")) {
			return Enchantment.DAMAGE_ALL;
		}
		if (name.equalsIgnoreCase("baneofarthropods") || name.equalsIgnoreCase("ruinadosartropodes")) {
			return Enchantment.DAMAGE_ARTHROPODS;
		}
		if (name.equalsIgnoreCase("smite") || name.equalsIgnoreCase("julgamento")) {
			return Enchantment.DAMAGE_UNDEAD;
		}
		if (name.equalsIgnoreCase("efficiency") || name.equalsIgnoreCase("eficiencia")) {
			return Enchantment.DIG_SPEED;
		}
		if (name.equalsIgnoreCase("unbreaking") || name.equalsIgnoreCase("inquebravel")) {
			return Enchantment.DURABILITY;
		}
		if (name.equalsIgnoreCase("fireaspect") || name.equalsIgnoreCase("aspectoflamejante")) {
			return Enchantment.FIRE_ASPECT;
		}
		if (name.equalsIgnoreCase("knockback") || name.equalsIgnoreCase("repulsao")) {
			return Enchantment.KNOCKBACK;
		}
		if (name.equalsIgnoreCase("fortune") || name.equalsIgnoreCase("fortuna")) {
			return Enchantment.LOOT_BONUS_BLOCKS;
		}
		if (name.equalsIgnoreCase("looting") || name.equalsIgnoreCase("pilhagem")) {
			return Enchantment.LOOT_BONUS_MOBS;
		}
		if (name.equalsIgnoreCase("respiration") || name.equalsIgnoreCase("respiracao")) {
			return Enchantment.OXYGEN;
		}
		if (name.equalsIgnoreCase("protection") || name.equalsIgnoreCase("protecao")) {
			return Enchantment.PROTECTION_ENVIRONMENTAL;
		}
		if (name.equalsIgnoreCase("explosionsprotection") || name.equalsIgnoreCase("protecaocontraexplosao")) {
			return Enchantment.PROTECTION_EXPLOSIONS;
		}
		if (name.equalsIgnoreCase("featherfalling") || name.equalsIgnoreCase("pesopena")) {
			return Enchantment.PROTECTION_FALL;
		}
		if (name.equalsIgnoreCase("fireprotection") || name.equalsIgnoreCase("protecaocontrafogo")) {
			return Enchantment.PROTECTION_FIRE;
		}
		if (name.equalsIgnoreCase("projectileprotection") || name.equalsIgnoreCase("protecaocontraprojeteis")) {
			return Enchantment.PROTECTION_PROJECTILE;
		}
		if (name.equalsIgnoreCase("silktouch") || name.equalsIgnoreCase("toquesuave")) {
			return Enchantment.SILK_TOUCH;
		}
		if (name.equalsIgnoreCase("aquaaffinity") || name.equalsIgnoreCase("afinidadeaquatica")) {
			return Enchantment.WATER_WORKER;
		}
		if (name.equalsIgnoreCase("flame") || name.equalsIgnoreCase("chama")) {
			return Enchantment.ARROW_FIRE;
		}
		if (name.equalsIgnoreCase("power") || name.equalsIgnoreCase("poder")) {
			return Enchantment.ARROW_DAMAGE;
		}
		if (name.equalsIgnoreCase("punch") || name.equalsIgnoreCase("impacto")) {
			return Enchantment.ARROW_KNOCKBACK;
		}
		if (name.equalsIgnoreCase("infinity") || name.equalsIgnoreCase("infinidade")) {
			return Enchantment.ARROW_INFINITE;
		}
		if (name.equalsIgnoreCase("thorns") || name.equalsIgnoreCase("espinhos")) {
			return Enchantment.THORNS;
		}
		return null;
	}
}
