package me.herobrinedobem.heventos;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.herobrinedobem.heventos.api.EventoBaseAPI;
import me.herobrinedobem.heventos.api.EventoCancellType;
import me.herobrinedobem.heventos.api.EventoType;
import me.herobrinedobem.heventos.api.EventoUtils;
import me.herobrinedobem.heventos.api.events.PlayerEnterEvent;
import me.herobrinedobem.heventos.api.events.PlayerLeaveEvent;
import me.herobrinedobem.heventos.api.events.StartEvent;
import me.herobrinedobem.heventos.api.events.StopEvent;
import me.herobrinedobem.heventos.eventos.BatataQuente;
import me.herobrinedobem.heventos.eventos.BowSpleef;
import me.herobrinedobem.heventos.eventos.EventoNormal;
import me.herobrinedobem.heventos.eventos.Frog;
import me.herobrinedobem.heventos.eventos.Killer;
import me.herobrinedobem.heventos.eventos.MinaMortal;
import me.herobrinedobem.heventos.eventos.Paintball;
import me.herobrinedobem.heventos.eventos.Semaforo;
import me.herobrinedobem.heventos.eventos.Spleef;

public class Comandos implements CommandExecutor {

	private HEventos instance;

	public Comandos(HEventos instance) {
		this.instance = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("evento")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("entrar")) {
					if (HEventos.getHEventos().getEventosController().getEvento() == null) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgNenhumEvento());
						return true;
					}
					if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
							.contains(p.getName())) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgJaParticipa());
						return true;
					}
					if (!HEventos.getHEventos().getEventosController().getEvento().isAberto()) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgEventoFechado());
						return true;
					}
					if (HEventos.getHEventos().getEventosController().getEvento().isVip()) {
						if (!(p.hasPermission("heventos.vip") || p.hasPermission("heventos.admin"))) {
							p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgEventoVip());
							return true;
						}
					}
					if (HEventos.getHEventos().getEventosController().getEvento().isInventoryEmpty()) {
						if (EventoUtils.isInventoryEmpty(p)) {
							p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgInventarioVazio());
							return true;
						}
					}
					for (String s : HEventos.getHEventos().getEventosController().getEvento().getParticipantes()) {
						Player pa = HEventos.getHEventos().getServer().getPlayer(s);
						pa.sendMessage(
								HEventos.getHEventos().getConfigUtil().getMsgEntrou().replace("$player$", p.getName()));
					}
					HEventos.getHEventos().getEventosController().getEvento().getParticipantes().add(p.getName());
					p.teleport(HEventos.getHEventos().getEventosController().getEvento().getAguarde());
					PlayerEnterEvent event = new PlayerEnterEvent(p,
							HEventos.getHEventos().getEventosController().getEvento(), false);
					HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
				} else if (args[0].equalsIgnoreCase("sair")) {
					if (HEventos.getHEventos().getEventosController().getEvento() == null) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgNenhumEvento());
						return true;
					}
					if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
							.contains(p.getName())) {
						PlayerLeaveEvent event = new PlayerLeaveEvent(p,
								HEventos.getHEventos().getEventosController().getEvento(), false);
						HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
						for (String s : HEventos.getHEventos().getEventosController().getEvento().getParticipantes()) {
							Player pa = HEventos.getHEventos().getServer().getPlayer(s);
							pa.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgSaiu().replace("$player$",
									p.getName()));
						}
						return true;
					}
					if (!HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers()
							.contains(p.getName())) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgNaoParticipa());
						return true;
					}
					PlayerLeaveEvent event = new PlayerLeaveEvent(p,
							HEventos.getHEventos().getEventosController().getEvento(), true);
					HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
				} else if (args[0].equalsIgnoreCase("cancelar")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					if (HEventos.getHEventos().getEventosController().getEvento() == null) {
						p.sendMessage("§4[Evento] §cNao existe um evento ocorrendo no momento!");
						return true;
					}
					StopEvent event = new StopEvent(HEventos.getHEventos().getEventosController().getEvento(),
							EventoCancellType.CANCELLED);
					HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
					p.sendMessage("§4[Evento] §cEvento cancelado com sucesso!");
				} else if (args[0].equalsIgnoreCase("assistir")) {
					if (HEventos.getHEventos().getEventosController().getEvento() == null) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgNenhumEvento());
						return true;
					}
					if (!HEventos.getHEventos().getEventosController().getEvento().isAssistirAtivado()) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgAssistirDesativado());
						return true;
					}
					if (HEventos.getHEventos().getEventosController().getEvento().getParticipantes()
							.contains(p.getName())) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgJaParticipa());
						return true;
					}
					if (HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers()
							.contains(p.getName())) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgJaEstaCamarote());
						return true;
					}
					if (!(p.hasPermission("heventos.assistir") || p.hasPermission("heventos.admin"))) {
						p.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgAssistirBloqueado());
						return true;
					}
					HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers().add(p.getName());
					if (HEventos.getHEventos().getEventosController().getEvento().isAberto()) {
						p.teleport(HEventos.getHEventos().getEventosController().getEvento().getAguarde());
					} else {
						p.teleport(HEventos.getHEventos().getEventosController().getEvento().getCamarote());
					}
					for (String s : HEventos.getHEventos().getEventosController().getEvento().getParticipantes()) {
						Player pa = HEventos.getHEventos().getServer().getPlayer(s);
						pa.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgAssistindo().replace("$player$",
								p.getName()));
					}
					for (String s : HEventos.getHEventos().getEventosController().getEvento().getCamarotePlayers()) {
						Player pa = HEventos.getHEventos().getServer().getPlayer(s);
						pa.sendMessage(HEventos.getHEventos().getConfigUtil().getMsgAssistindo().replace("$player$",
								p.getName()));
					}
					PlayerEnterEvent event = new PlayerEnterEvent(p,
							HEventos.getHEventos().getEventosController().getEvento(), true);
					HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					HEventos.getHEventos().reloadConfig();
					HEventos.getHEventos().getConfigUtil().setupConfigUtils();
					p.sendMessage("§4[Evento] §cConfiguracao recarregada com sucesso!");
				} else if (args[0].equalsIgnoreCase("lista")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					StringBuilder builder = new StringBuilder();
					for (File file : new File(this.instance.getDataFolder().getAbsolutePath() + "/Eventos/")
							.listFiles()) {
						builder.append("§6" + file.getName().replace(".yml", "") + " §0- ");
					}
					p.sendMessage("§4[Evento] §cLista de eventos:");
					p.sendMessage(builder.toString());
				} else if (args[0].equalsIgnoreCase("multiplicador")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					p.sendMessage("§4[Evento] §cUtilize /evento multiplicador <valor>");
				} else if (args[0].equalsIgnoreCase("iniciar")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					p.sendMessage("§4[Evento] §cUtilize /evento iniciar <nome> <true/false>");
				} else {
					MsgDefault(p);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("iniciar")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					p.sendMessage("§4[Evento] §cUtilize /evento iniciar <nome> <true/false>");
				} else if (args[0].equalsIgnoreCase("multiplicador") && !(args[1].equalsIgnoreCase("reset"))) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					try {
						this.instance.getConfig().set("Money_Multiplicador", Integer.parseInt(args[1]));
						this.instance.saveConfig();
						p.sendMessage("§4[Evento] §cRate alterado com sucesso!");
						HEventos.getHEventos().getServer().broadcastMessage(
								"§4[Eventos] §cMultiplicador de money dos eventos alterado para §4" + args[1] + "*");
					} catch (NumberFormatException e) {
						p.sendMessage("§4[Evento] §cUtilize apenas numeros no rate.");
					}
				} else if (args[0].equalsIgnoreCase("multiplicador") && (args[1].equalsIgnoreCase("reset"))) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					this.instance.getConfig().set("Money_Multiplicador", 1);
					this.instance.saveConfig();
					p.sendMessage("§4[Evento] §cRate alterado com sucesso!");
					HEventos.getHEventos().getServer().broadcastMessage("  ");
					HEventos.getHEventos().getServer()
							.broadcastMessage("§4[Eventos] §cRate de money dos eventos voltou ao normal!");
					HEventos.getHEventos().getServer().broadcastMessage("  ");
				} else if (args[0].equalsIgnoreCase("top")) {
					if (args[1].equalsIgnoreCase("participacoes")) {
						HEventos.getHEventos().getDatabaseManager().getTOPParticipations(p);
					} else if (args[1].equalsIgnoreCase("vencedores")) {
						HEventos.getHEventos().getDatabaseManager().getTOPWins(p);
					} else {
						MsgDefault(p);
					}
				} else if (args[0].equalsIgnoreCase("setentrada")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					if (!HEventos.getHEventos().getEventosController().hasEvento(args[1])) {
						p.sendMessage("§4[Evento] §cEvento nao encontrado na pasta.");
						return true;
					}
					EventoBaseAPI evento = HEventos.getHEventos().getEventosController().loadEvento(args[1]);
					evento.getConfig().set("Localizacoes.Entrada", this.getLocationForConfig(p.getLocation()));
					File file = new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + args[1] + ".yml");
					try {
						evento.getConfig().save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					p.sendMessage("§4[Evento] §cEntrada do evento " + args[1] + " setada com sucesso!");
				} else if (args[0].equalsIgnoreCase("setsaida")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					if (!HEventos.getHEventos().getEventosController().hasEvento(args[1])) {
						p.sendMessage("§4[Evento] §cEvento nao encontrado na pasta.");
						return true;
					}
					EventoBaseAPI evento = HEventos.getHEventos().getEventosController().loadEvento(args[1]);
					evento.getConfig().set("Localizacoes.Saida", this.getLocationForConfig(p.getLocation()));
					File file = new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + args[1] + ".yml");
					try {
						evento.getConfig().save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					p.sendMessage("§4[Evento] §cSaida do evento " + args[1] + " setada com sucesso!");
				} else if (args[0].equalsIgnoreCase("setcamarote")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					if (!HEventos.getHEventos().getEventosController().hasEvento(args[1])) {
						p.sendMessage("§4[Evento] §cEvento nao encontrado na pasta.");
						return true;
					}
					EventoBaseAPI evento = HEventos.getHEventos().getEventosController().loadEvento(args[1]);
					evento.getConfig().set("Localizacoes.Camarote", this.getLocationForConfig(p.getLocation()));
					File file = new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
							+ File.separator + args[1] + ".yml");
					try {
						evento.getConfig().save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					p.sendMessage("§4[Evento] §cCamarote do evento " + args[1] + " setado com sucesso!");
				} else if (args[0].equalsIgnoreCase("setaguardando")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					if (HEventos.getHEventos().getEventosController().hasEvento(args[1])) {
						EventoBaseAPI evento = HEventos.getHEventos().getEventosController().loadEvento(args[1]);
						evento.getConfig().set("Localizacoes.Aguardando", this.getLocationForConfig(p.getLocation()));
						File file = new File(HEventos.getHEventos().getDataFolder() + File.separator + "Eventos"
								+ File.separator + args[1] + ".yml");
						try {
							evento.getConfig().save(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
						p.sendMessage("§4[Evento] §cLocal de espera do evento " + args[1] + " setado com sucesso!");
					} else {
						p.sendMessage("§4[Evento] §cEvento nao encontrado na pasta.");
					}
				} else if (args[0].equalsIgnoreCase("tool")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					if (args[1].equalsIgnoreCase("spleef")) {
						ItemStack item = new ItemStack(Material.IRON_AXE, 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("§4§lEvento Spleef");
						meta.setLore(Arrays.asList("§6* Clique com o botao direito para marcar a posicao 1 do chao",
								"§6* Clique com o botao esquerdo para marcar a posicao 2 do chao"));
						item.setItemMeta(meta);
						p.getInventory().addItem(item);
						p.updateInventory();
					} else if (args[1].equalsIgnoreCase("minamortal")) {
						ItemStack item = new ItemStack(Material.IRON_AXE, 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("§4§lEvento MinaMortal");
						meta.setLore(Arrays.asList("§6* Clique com o botao direito para marcar a posicao 1 da mina",
								"§6* Clique com o botao esquerdo para marcar a posicao 2 da mina"));
						item.setItemMeta(meta);
						p.getInventory().addItem(item);
						p.updateInventory();
					} else if (args[1].equalsIgnoreCase("bowspleef")) {
						ItemStack item = new ItemStack(Material.IRON_AXE, 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("§4§lEvento BowSpleef");
						meta.setLore(Arrays.asList("§6* Clique com o botao direito para marcar a posicao 1 do chao",
								"§6* Clique com o botao esquerdo para marcar a posicao 2 do chao"));
						item.setItemMeta(meta);
						p.getInventory().addItem(item);
						p.updateInventory();
					} else if (args[1].equalsIgnoreCase("paintball")) {
						ItemStack item = new ItemStack(Material.IRON_AXE, 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("§4§lEvento Paintball");
						meta.setLore(Arrays.asList("§6* Clique com o botao direito para marcar a posicao 1 do chao",
								"§6* Clique com o botao esquerdo para marcar a posicao 2 do chao"));
						item.setItemMeta(meta);
						p.getInventory().addItem(item);
						p.updateInventory();
					} else if (args[1].equalsIgnoreCase("frog")) {
						ItemStack item = new ItemStack(Material.IRON_AXE, 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("§4§lEvento Frog");
						meta.setLore(Arrays.asList("§6* Clique com o botao direito para marcar a posicao 1 do chao",
								"§6* Clique com o botao esquerdo para marcar a posicao 2 do chao"));
						item.setItemMeta(meta);
						p.getInventory().addItem(item);
						p.updateInventory();
					} else {
						p.sendMessage("§4[Evento] §cUtilize /evento tool <spleef/minamortal/bowspleef/paintball/frog>");
					}
				} else {
					MsgDefault(p);
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("iniciar")) {
					if (!p.hasPermission("heventos.admin"))
						return true;
					if (HEventos.getHEventos().getEventosController().getEvento() != null) {
						p.sendMessage("§4[Evento] §cJa existe um evento ocorrendo no momento!");
						return true;
					}
					if (!(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {
						p.sendMessage("§4[Evento] §cUtilize /evento iniciar <nome> <true/false>");
						return true;
					}
					if (HEventos.getHEventos().getEventosController().externalEvento(args[1])) {
						return true;
					}
					if (!HEventos.getHEventos().getEventosController().hasEvento(args[1])) {
						p.sendMessage("§4[Evento] §cEvento nao encontrado!");
						return true;
					}
					switch (EventoType.getEventoType(args[1])) {
					case SPLEEF:
						Spleef spleef = new Spleef(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(spleef);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case FROG:
						Frog frog = new Frog(HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(frog);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case BOW_SPLEEF:
						BowSpleef bowspleef = new BowSpleef(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(bowspleef);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case BATATA_QUENTE:
						BatataQuente batata = new BatataQuente(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(batata);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case KILLER:
						Killer killer = new Killer(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(killer);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case MINA_MORTAL:
						MinaMortal mina = new MinaMortal(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(mina);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case PAINTBALL:
						Paintball paint = new Paintball(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(paint);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case SEMAFORO:
						Semaforo semaforo = new Semaforo(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(semaforo);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					case NORMAL:
						EventoNormal evento = new EventoNormal(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(evento);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					default:
						EventoNormal evento2 = new EventoNormal(
								HEventos.getHEventos().getEventosController().getConfigFile(args[1]));
						HEventos.getHEventos().getEventosController().setEvento(evento2);
						HEventos.getHEventos().getEventosController().getEvento().setVip(Boolean.parseBoolean(args[2]));
						HEventos.getHEventos().getEventosController().getEvento().run();
						break;
					}
					StartEvent event = new StartEvent(HEventos.getHEventos().getEventosController().getEvento(), false);
					HEventos.getHEventos().getServer().getPluginManager().callEvent(event);
					p.sendMessage("§4[Evento] §cEvento iniciado com sucesso!");
				} else {
					MsgDefault(p);
				}
			} else {
				MsgDefault(p);
			}
		}
		return true;
	}

	private void MsgDefault(Player p) {
		for (String s : HEventos.getHEventos().getConfig().getStringList("Mensagens.Default")) {
			p.sendMessage(s.replace("&", "§"));
		}
		if (p.hasPermission("heventos.admin")) {
			for (String s : HEventos.getHEventos().getConfig().getStringList("Mensagens.DefaultAdmin")) {
				p.sendMessage(s.replace("&", "§"));
			}
		}
	}

	private String getLocationForConfig(Location loc) {
		String world = loc.getWorld().getName();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		float yaw = (float) loc.getYaw();
		float pitch = (float) loc.getPitch();
		return world + ";" + String.valueOf(x) + ";" + String.valueOf(y) + ";" + String.valueOf(z) + ";"
				+ String.valueOf(yaw) + ";" + String.valueOf(pitch);
	}

}
