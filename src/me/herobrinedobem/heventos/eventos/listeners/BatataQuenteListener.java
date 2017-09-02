package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.eventos.BatataQuente;

public class BatataQuenteListener extends EventoBaseListener {

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEntityEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!(e.getRightClicked() instanceof Player))
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		BatataQuente batataQuente = (BatataQuente) HEventos.getHEventos().getEventosController().getEvento();
		if (!e.getPlayer().getName().equalsIgnoreCase(batataQuente.getPlayerComBatata().getName()))
			return;
		Player pa = (Player) e.getRightClicked();
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes().contains(pa))
			return;
		batataQuente.setPlayerComBatata((Player) e.getRightClicked());
		e.getPlayer().getInventory().remove(new ItemStack(Material.POTATO_ITEM, 1));
		pa.getPlayer().getInventory().addItem(new ItemStack(Material.POTATO_ITEM, 1));
		for (Player p : batataQuente.getParticipantes()) {
			for (String s : HEventos.getHEventos().getEventosController().getEvento().getConfig()
					.getStringList("Mensagens.Esta_Com_Batata")) {
				p.sendMessage(s.replace("&", "§").replace("$player$", batataQuente.getPlayerComBatata().getName())
						.replace("$EventoName$", batataQuente.getNome()));
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getView().getPlayer()))
			return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent a) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(a.getPlayer()))
			return;
		BatataQuente batataQuente = (BatataQuente) HEventos.getHEventos().getEventosController().getEvento();
		if (a.getPlayer().getName().equalsIgnoreCase(batataQuente.getPlayerComBatata().getName())) {
			a.setCancelled(true);
		}
	}
}
