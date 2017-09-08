package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.Spleef;

public class SpleefListener extends EventoBaseListener {

	private EventoBaseAPI evento;

	@EventHandler
	public void onBlockBreakEvento(BlockBreakEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!evento.getParticipantes().contains(e.getPlayer()))
			return;
		if ((evento.isAberto()))
			return;
		if (!evento.isOcorrendo())
			return;
		Spleef spleef = (Spleef) evento;
		if (!spleef.isPodeQuebrar()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		evento = HEventos.getHEventos().getEventosController().getEvento();
		if (evento == null)
			return;
		if (!evento.getParticipantes().contains(e.getPlayer()))
			return;
		if ((evento.isAberto()))
			return;
		if (!evento.isOcorrendo())
			return;
		Spleef spleef = (Spleef) evento;
		if ((e.getPlayer().getLocation().getY() <= spleef.getY())) {
			e.getPlayer().sendMessage(spleef.getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§")
					.replace("$EventoName$", spleef.getNome()));
			PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(), evento);
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			String msg = spleef.getConfig().getString("Mensagens.FoiEliminado");
			for (Player p : spleef.getParticipantes()) {
				p.sendMessage(msg.replace("&", "§").replace("$EventoName$", spleef.getNome()).replace("$player$",
						e.getPlayer().getName()));
			}
		}
	}
}
