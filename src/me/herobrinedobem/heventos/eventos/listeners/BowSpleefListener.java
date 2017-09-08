package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.BowSpleef;

public class BowSpleefListener extends EventoBaseListener {

	private EventoBaseAPI evento;

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!evento.getParticipantes().contains(e.getPlayer()))
			return;
		if (evento.isAberto())
			return;
		if (!(e.getFrom() != e.getTo()))
			return;
		BowSpleef bows = (BowSpleef) evento;
		if (!(e.getTo().getY() < (bows.getChao().getLowerLocation().getY() - 2)))
			return;
		e.getPlayer().sendMessage(bows.getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§")
				.replace("$EventoName$", bows.getNome()));
		PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(), evento);
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		String msg = bows.getConfig().getString("Mensagens.FoiEliminado");
		for (Player p : bows.getParticipantes()) {
			p.sendMessage(msg.replace("&", "§").replace("$EventoName$", bows.getNome()).replace("$player$",
					e.getPlayer().getName()));

		}
	}
}
