package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.eventos.Semaforo;

public class SemaforoListener extends EventoBaseListener {

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer().getName())) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if ((e.getClickedBlock().getType() == Material.SIGN_POST)
						|| (e.getClickedBlock().getType() == Material.WALL_SIGN)) {
					Sign s = (Sign) e.getClickedBlock().getState();
					if (s.getLine(0).equalsIgnoreCase("§9[Evento]")) {
						PlayerWinEvent event = new PlayerWinEvent(e.getPlayer(),
								HEventos.getHEventos().getEventosController().getEvento(), false);
						HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
						HEventos.getHEventos().getEventosController().getEvento().stopEvent();
						s.setLine(1, "§6" + e.getPlayer().getName());
						s.update();
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (HEventos.getHEventos().getEventosController().getEvento().isAberto())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer().getName()))
			return;
		if ((e.getFrom().getX() != e.getTo().getX()) && (e.getFrom().getZ() != e.getTo().getZ())) {
			Semaforo semaforo = (Semaforo) HEventos.getHEventos().getEventosController().getEvento();
			if (!semaforo.isPodeAndar()) {
				e.getPlayer().sendMessage(semaforo.getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§").replace("$EventoName$", semaforo.getNome()));
				PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(),
						HEventos.getHEventos().getEventosController().getEvento());
				HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
				String msg = semaforo.getConfig().getString("Mensagens.FoiEliminado");
				for (String sa : semaforo.getParticipantes()) {
					Player p = semaforo.getPlayerByName(sa);
					p.sendMessage(msg.replace("&", "§").replace("$EventoName$", semaforo.getNome()).replace("$player$", e.getPlayer().getName()));
				}
			}
		}
	}
}
