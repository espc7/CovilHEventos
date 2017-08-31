package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;

public class EventoNormalListener extends EventoBaseListener {

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if ((HEventos.getHEventos().getEventosController().getEvento().isAberto()))
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer().getName()))
			return;
		if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK))
			return;
		if ((e.getClickedBlock().getType() == Material.SIGN_POST)
				|| (e.getClickedBlock().getType() == Material.WALL_SIGN)) {
			Sign s = (Sign) e.getClickedBlock().getState();
			if (s.getLine(0).equalsIgnoreCase("§9[Evento]")) {
				if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes().size() >= 1) {
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
