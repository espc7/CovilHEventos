package me.herobrinedobem.heventos.api;

public interface EventoBaseImplements {

	public void startEvent();

	public void stopEvent();
	
	public void resetEvent();

	public void startEventMethod();

	public void scheduledMethod();

	public void stopEventMethod();

	public void cancelEventMethod();
	
	public void externalPluginStart(boolean vip);

}
