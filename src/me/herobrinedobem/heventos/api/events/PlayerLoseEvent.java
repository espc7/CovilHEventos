package me.herobrinedobem.heventos.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.herobrinedobem.heventos.api.EventoBaseAPI;

public class PlayerLoseEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private EventoBaseAPI evento;
	
	public PlayerLoseEvent(Player player, EventoBaseAPI evento) {
		this.player = player;
		this.evento = evento;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public EventoBaseAPI getEvento() {
		return evento;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
		 
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
