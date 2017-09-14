package me.herobrinedobem.heventos.utils;

import java.util.ArrayList;
import java.util.List;

import me.herobrinedobem.heventos.HEventos;

public class ConfigUtil {

	private String topVencedores, topParticipacoes, topVencedoresPosicao,
			topParticipacoesPosicao, msgMorreu, msgDesconect, msgSaiu,
			msgComandoBloqueado, msgNenhumEvento, msgEventoFechado,
			msgJaParticipa, msgEntrou, msgNaoParticipa, msgJaEstaCamarote,
			msgAssistindo, msgEventoVip, msgAssistirDesativado,
			msgInventarioVazio, msgAssistirBloqueado, msgTeleporteBloqueado;
	private List<String> comandosBloqueados = new ArrayList<>();
	private boolean mysqlAtivado;
	
	
	public ConfigUtil() {
		setupConfigUtils();
	}

	public void setupConfigUtils(){
		this.setMsgTeleporteBloqueado(HEventos.getHEventos().getConfig().getString("Mensagens.Teleporte_Bloqueado").replace("&", "§"));
		this.setMsgAssistirBloqueado(HEventos.getHEventos().getConfig().getString("Mensagens.Assistir_SemPermission").replace("&", "§"));
		this.setMsgInventarioVazio(HEventos.getHEventos().getConfig().getString("Mensagens.Inventario_Vazio").replace("&", "§"));
		this.setMsgAssistirDesativado(HEventos.getHEventos().getConfig().getString("Mensagens.Assistir_Desativado").replace("&", "§"));
		this.setMsgEventoVip(HEventos.getHEventos().getConfig().getString("Mensagens.Evento_VIP").replace("&", "§"));
		this.setMsgMorreu(HEventos.getHEventos().getConfig().getString("Mensagens.Morreu").replace("&", "§"));
		this.setMsgDesconect(HEventos.getHEventos().getConfig().getString("Mensagens.Desconectou").replace("&", "§"));
		this.setMsgSaiu(HEventos.getHEventos().getConfig().getString("Mensagens.Saiu").replace("&", "§"));
		this.setMsgComandoBloqueado(HEventos.getHEventos().getConfig().getString("Mensagens.Comando_Bloqueado").replace("&", "§"));
		this.setMsgNenhumEvento(HEventos.getHEventos().getConfig().getString("Mensagens.Nenhum_Evento").replace("&", "§"));
		this.setMsgEventoFechado(HEventos.getHEventos().getConfig().getString("Mensagens.Evento_Fechado").replace("&", "§"));
		this.setMsgJaParticipa(HEventos.getHEventos().getConfig().getString("Mensagens.Ja_Participa").replace("&", "§"));
		this.setMsgEntrou(HEventos.getHEventos().getConfig().getString("Mensagens.Entrou").replace("&", "§"));
		this.setMsgNaoParticipa(HEventos.getHEventos().getConfig().getString("Mensagens.Nao_Participa").replace("&", "§"));
		this.setMsgJaEstaCamarote(HEventos.getHEventos().getConfig().getString("Mensagens.Ja_Esta_Camarote").replace("&", "§"));
		this.setMsgAssistindo(HEventos.getHEventos().getConfig().getString("Mensagens.Assistindo").replace("&", "§"));
		this.setTopParticipacoes(HEventos.getHEventos().getConfig().getString("Mensagens.Top_Participacoes").replace("&", "§"));
		this.setTopParticipacoesPosicao(HEventos.getHEventos().getConfig().getString("Mensagens.Top_Participacoes_Posicao").replace("&", "§"));
		this.setTopVencedores(HEventos.getHEventos().getConfig().getString("Mensagens.Top_Vencedores").replace("&", "§"));
		this.setTopVencedoresPosicao(HEventos.getHEventos().getConfig().getString("Mensagens.Top_Vencedores_Posicao").replace("&", "§"));
		this.setMysqlAtivado(HEventos.getHEventos().getConfig().getBoolean("MySQL.Ativado"));
	}
	
	public String getMsgInventarioVazio() {
		return this.msgInventarioVazio;
	}

	public void setMsgInventarioVazio(final String msgInventarioVazio) {
		this.msgInventarioVazio = msgInventarioVazio;
	}

	public String getMsgAssistirDesativado() {
		return this.msgAssistirDesativado;
	}

	public void setMsgAssistirDesativado(final String msgAssistirDesativado) {
		this.msgAssistirDesativado = msgAssistirDesativado;
	}

	public String getMsgEventoVip() {
		return this.msgEventoVip;
	}

	public void setMsgEventoVip(final String msgEventoVip) {
		this.msgEventoVip = msgEventoVip;
	}

	public boolean isMysqlAtivado() {
		return this.mysqlAtivado;
	}

	public void setMysqlAtivado(final boolean mysqlAtivado) {
		this.mysqlAtivado = mysqlAtivado;
	}

	public String getTopVencedores() {
		return this.topVencedores;
	}

	public void setTopVencedores(final String topVencedores) {
		this.topVencedores = topVencedores;
	}

	public String getTopParticipacoes() {
		return this.topParticipacoes;
	}

	public void setTopParticipacoes(final String topParticipacoes) {
		this.topParticipacoes = topParticipacoes;
	}

	public String getTopVencedoresPosicao() {
		return this.topVencedoresPosicao;
	}

	public void setTopVencedoresPosicao(final String topVencedoresPosicao) {
		this.topVencedoresPosicao = topVencedoresPosicao;
	}

	public String getTopParticipacoesPosicao() {
		return this.topParticipacoesPosicao;
	}

	public void setTopParticipacoesPosicao(final String topParticipacoesPosicao) {
		this.topParticipacoesPosicao = topParticipacoesPosicao;
	}

	public String getMsgMorreu() {
		return this.msgMorreu;
	}

	public void setMsgMorreu(final String msgMorreu) {
		this.msgMorreu = msgMorreu;
	}

	public String getMsgDesconect() {
		return this.msgDesconect;
	}

	public void setMsgDesconect(final String msgDesconect) {
		this.msgDesconect = msgDesconect;
	}

	public String getMsgSaiu() {
		return this.msgSaiu;
	}

	public void setMsgSaiu(final String msgSaiu) {
		this.msgSaiu = msgSaiu;
	}

	public String getMsgComandoBloqueado() {
		return this.msgComandoBloqueado;
	}

	public void setMsgComandoBloqueado(final String msgComandoBloqueado) {
		this.msgComandoBloqueado = msgComandoBloqueado;
	}

	public String getMsgNenhumEvento() {
		return this.msgNenhumEvento;
	}

	public void setMsgNenhumEvento(final String msgNenhumEvento) {
		this.msgNenhumEvento = msgNenhumEvento;
	}

	public String getMsgEventoFechado() {
		return this.msgEventoFechado;
	}

	public void setMsgEventoFechado(final String msgEventoFechado) {
		this.msgEventoFechado = msgEventoFechado;
	}

	public String getMsgJaParticipa() {
		return this.msgJaParticipa;
	}

	public void setMsgJaParticipa(final String msgJaParticipa) {
		this.msgJaParticipa = msgJaParticipa;
	}

	public String getMsgEntrou() {
		return this.msgEntrou;
	}

	public void setMsgEntrou(final String msgEntrou) {
		this.msgEntrou = msgEntrou;
	}

	public String getMsgNaoParticipa() {
		return this.msgNaoParticipa;
	}

	public void setMsgNaoParticipa(final String msgNaoParticipa) {
		this.msgNaoParticipa = msgNaoParticipa;
	}

	public String getMsgJaEstaCamarote() {
		return this.msgJaEstaCamarote;
	}

	public void setMsgJaEstaCamarote(final String msgJaEstaCamarote) {
		this.msgJaEstaCamarote = msgJaEstaCamarote;
	}

	public String getMsgAssistindo() {
		return this.msgAssistindo;
	}

	public void setMsgAssistindo(final String msgAssistindo) {
		this.msgAssistindo = msgAssistindo;
	}

	public List<String> getComandosBloqueados() {
		return this.comandosBloqueados;
	}

	public void setComandosBloqueados(final ArrayList<String> comandosBloqueados) {
		this.comandosBloqueados = comandosBloqueados;
	}

	public String getMsgAssistirBloqueado() {
		return msgAssistirBloqueado;
	}

	public void setMsgAssistirBloqueado(String msgAssistirBloqueado) {
		this.msgAssistirBloqueado = msgAssistirBloqueado;
	}

	public String getMsgTeleporteBloqueado() {
		return msgTeleporteBloqueado;
	}

	public void setMsgTeleporteBloqueado(String msgTeleporteBloqueado) {
		this.msgTeleporteBloqueado = msgTeleporteBloqueado;
	}
}
