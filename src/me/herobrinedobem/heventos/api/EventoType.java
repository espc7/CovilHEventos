package me.herobrinedobem.heventos.api;

public enum EventoType {

	BATATA_QUENTE,
	FROG,
	FIGHT,
	KILLER,
	MINA_MORTAL,
	SPLEEF,
	BOW_SPLEEF,
	PAINTBALL,
	SEMAFORO,
	NORMAL,
	OUTRO;

	public static EventoType getEventoType(String type) {
		switch (type) {
			case "batataquente":
				return EventoType.BATATA_QUENTE;
			case "frog":
				return EventoType.FROG;
			case "fight":
				return EventoType.FIGHT;
			case "killer":
				return EventoType.KILLER;
			case "minamortal":
				return EventoType.MINA_MORTAL;
			case "spleef":
				return EventoType.SPLEEF;
			case "bowspleef":
				return EventoType.BOW_SPLEEF;
			case "paintball":
				return EventoType.PAINTBALL;
			case "semaforo":
				return EventoType.SEMAFORO;
			case "normal":
				return EventoType.NORMAL;
			case "outro":
				return EventoType.OUTRO;
			default:
				return EventoType.NORMAL;
		}
	}

}
