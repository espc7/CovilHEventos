package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.eventos.Spleef;

public class SpleefListener extends EventoBaseListener {

	@EventHandler
	public void onBlockBreakEvento(BlockBreakEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if ((HEventos.getHEventos().getEventosController().getEvento().isAberto()))
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer().getName()))
			return;
		Spleef spleef = (Spleef) HEventos.getHEventos().getEventosController().getEvento();
		if (!spleef.isPodeQuebrar()) {
			e.getPlayer()
					.sendMessage(spleef.getConfig().getString("Mensagens.Aguarde_Quebrar").replace("&", "§")
							.replace("$tempo$", spleef.getTempoComecarCurrent() + "")
							.replace("$EventoName$", spleef.getNome()));

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if (HEventos.getHEventos().getEventosController().getEvento() == null)
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
				.contains(e.getPlayer().getName()))
			return;
		if ((HEventos.getHEventos().getEventosController().getEvento().isAberto()))
			return;
		if (!HEventos.getHEventos().getEventosController().getEvento().isOcorrendo())
			return;
		Spleef spleef = (Spleef) HEventos.getHEventos().getEventosController().getEvento();
		if ((e.getPlayer().getLocation().getY() <= spleef.getY())) {
			e.getPlayer().sendMessage(spleef.getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§").replace("$EventoName$", spleef.getNome()));
			PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(),
					HEventos.getHEventos().getEventosController().getEvento());
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			String msg = spleef.getConfig().getString("Mensagens.FoiEliminado");
			for (String sa : spleef.getParticipantes()) {
				Player p = spleef.getPlayerByName(sa);
				p.sendMessage(msg.replace("&", "§").replace("$EventoName$", spleef.getNome()).replace("$player$", e.getPlayer().getName()));
			}
		}
	}
}
