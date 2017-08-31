package me.herobrinedobem.heventos.eventos.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import me.herobrinedobem.heventos.HEventos;
import me.herobrinedobem.heventos.api.EventoBaseListener;
import me.herobrinedobem.heventos.api.events.PlayerLoseEvent;
import me.herobrinedobem.heventos.api.events.PlayerWinEvent;
import me.herobrinedobem.heventos.eventos.Frog;

public class FrogListener extends EventoBaseListener {

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
		Frog frog = (Frog) HEventos.getHEventos().getEventosController().getEvento();
		if ((e.getPlayer().getLocation().getY() <= frog.getY()) && frog.isComecou()) {
			e.getPlayer().sendMessage(frog.getConfig().getString("Mensagens.VcFoiEliminado").replace("&", "§").replace("$EventoName$", frog.getNome()));
			PlayerLoseEvent event = new PlayerLoseEvent(e.getPlayer(),
					HEventos.getHEventos().getEventosController().getEvento());
			HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
			String msg = frog.getConfig().getString("Mensagens.FoiEliminado");
			for (String sa : frog.getParticipantes()) {
				Player p = frog.getPlayerByName(sa);
				p.sendMessage(msg.replace("&", "§").replace("$EventoName$", frog.getNome()).replace("$player$", e.getPlayer().getName()));

			}
		}
		if (!frog.isFim())
			return;
		Block b = frog.getLaVermelha().getWorld().getBlockAt(frog.getLaVermelha());
		if (!(e.getTo().getBlock().getX() == b.getX())) {
			return;
		}
		if (!(e.getTo().getBlock().getZ() == b.getZ())) {
			return;
		}
		if (!(e.getTo().getBlock().getY() == b.getY()+1)) {
			return;
		}
		PlayerWinEvent event = new PlayerWinEvent(e.getPlayer(),
				HEventos.getHEventos().getEventosController().getEvento(), false);
		HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
		frog.stopEvent();
	}
}
