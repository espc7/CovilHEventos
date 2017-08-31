package me.herobrinedobem.heventos.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;

public class StopEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private EventoBaseAPI evento;
	private EventoCancellType cancellType;
	
	public StopEvent(EventoBaseAPI evento, EventoCancellType cancellType) {
		this.evento = evento;
		this.cancellType = cancellType;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
		 
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public EventoBaseAPI getEvento() {
		return evento;
	}
	
	public EventoCancellType getCancellType() {
		return cancellType;
	}
	
}
