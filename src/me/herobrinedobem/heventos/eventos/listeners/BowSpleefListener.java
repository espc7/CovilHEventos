package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.BowSpleef;

public class BowSpleefListener extends EventoBaseListener {

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!(HEventos.getHEventos().getEventosController().getEvento() instanceof BowSpleef)) {
			return;
		}
		BowSpleef bows = (BowSpleef) HEventos.getHEventos().getEventosController().getEvento();
		if (!bows.getParticipantes().contains(e.getPlayer().getName()))
			return;
		if (bows.isAberto())
			return;
		if (!(e.getFrom() != e.getTo()))
			return;
		if (!(e.getTo().getY() < (bows.getChao().getLowerLocation().getY() - 2)))
			return;
		e.getPlayer().sendMessage(bows.getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§").replace("$EventoName$", bows.getNome()));
		PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(),
				HEventos.getHEventos().getEventosController().getEvento());
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		String msg = bows.getConfig().getString("Mensagens.FoiEliminado");
		for (String sa : bows.getParticipantes()) {
			Player p = bows.getPlayerByName(sa);
			p.sendMessage(msg.replace("&", "§").replace("$EventoName$", bows.getNome()).replace("$player$", e.getPlayer().getName()));

		}
	}
}
