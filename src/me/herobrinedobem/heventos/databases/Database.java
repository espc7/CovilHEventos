package me.herobrinedobem.heventos.databases;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import me.herobrinedobem.heventos.HEventos;

/**
 * Classe que irá controlar o banco de dados do servidor, verificando se é
 * para utilizar MySQL ou SQLite!
 * 
 * @author Herobrinedobem (Gabriel Henrique)
 * @version 1.6
 */
public class Database {

	private DatabaseType type;
	private String user, password, database, host;
	private Connection connection;

	public Database(DatabaseType type) {
		this.type = type;
		startDatabase();
	}

	public Database(DatabaseType type, final String user, final String password, final String database,
			final String host) {
		this.type = type;
		this.user = user;
		this.password = password;
		this.database = database;
		this.host = host;
		startDatabase();
	}

	private void selectDriver() {
		try {
			if (type == DatabaseType.MYSQL) {
				Class.forName("com.mysql.jdbc.Driver");
			} else if (type == DatabaseType.SQLITE) {
				Class.forName("org.sqlite.JDBC");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setConnection() {
		try {
			if (type == DatabaseType.MYSQL) {
				Class.forName("com.mysql.jdbc.Driver");
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "", user,
						password);
			} else if (type == DatabaseType.SQLITE) {
				Class.forName("org.sqlite.JDBC");
				this.connection = DriverManager.getConnection("jdbc:sqlite:"
						+ HEventos.getHEventos().getDataFolder().getAbsolutePath() + File.separator + "database.db");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void startDatabase() {
		try {
			setConnection();
			Statement stmt = this.connection.createStatement();
			stmt.execute(
					"CREATE TABLE IF NOT EXISTS eventos (player VARCHAR(255), wins INTEGER, participations INTEGER)");
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNewPlayer(final String player, final int wins, final int participations) {
		try {
			selectDriver();
			PreparedStatement pstmt = connection
					.prepareStatement("INSERT INTO eventos (player, wins, participations) VALUES (?, ?, ?)");
			pstmt.setString(1, player);
			pstmt.setInt(2, wins);
			pstmt.setInt(3, participations);
			pstmt.execute();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addWinPoint(final String player, final int points) {
		try {
			if (hasPlayer(player)) {
				selectDriver();
				PreparedStatement pstmt = connection.prepareStatement("UPDATE eventos SET wins=? WHERE player=?;");
				pstmt.setInt(1, getPlayerWins(player) + points);
				pstmt.setString(2, player);
				pstmt.executeUpdate();
				pstmt.close();
			} else {
				addNewPlayer(player, points, 1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addParticipationPoint(final String player, final int points) {
		try {
			if (hasPlayer(player)) {
				selectDriver();
				PreparedStatement pstmt = connection
						.prepareStatement("UPDATE eventos SET participations=? WHERE player=?;");
				pstmt.setInt(1, getPlayerParticipations(player) + points);
				pstmt.setString(2, player);
				pstmt.executeUpdate();
				pstmt.close();
			} else {
				addNewPlayer(player, 0, points);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getPlayerWins(final String player) {
		try {
			selectDriver();
			PreparedStatement pstmt = connection
					.prepareStatement("SELECT wins FROM eventos WHERE LOWER(player)=LOWER(?);");
			pstmt.setString(1, player);
			final ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getInt("wins");
			}
			rs.close();
			pstmt.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getPlayerParticipations(final String player) {
		try {
			selectDriver();
			PreparedStatement pstmt = connection
					.prepareStatement("SELECT participations FROM eventos WHERE LOWER(player)=LOWER(?);");
			pstmt.setString(1, player);
			final ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getInt("participations");
			}
			rs.close();
			pstmt.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean hasPlayer(final String player) {
		try {
			selectDriver();
			PreparedStatement pstmt = connection
					.prepareStatement("SELECT * FROM eventos WHERE LOWER(player)=LOWER(?);");
			pstmt.setString(1, player);
			final ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				rs.close();
				pstmt.close();
				return true;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void getTOPWins(final Player p) {
		try {
			selectDriver();
			p.sendMessage(HEventos.getHEventos().getConfigUtil().getTopVencedores());
			ResultSet rs = connection.createStatement()
					.executeQuery("SELECT * FROM eventos ORDER BY wins DESC LIMIT 10");
			int i = 0;
			while (rs.next()) {
				i++;
				p.sendMessage(HEventos.getHEventos().getConfigUtil().getTopVencedoresPosicao()
						.replace("$posicao$", i + "").replace("$player$", rs.getString("player"))
						.replace("$vitorias$", rs.getInt("wins") + ""));
			}
			rs.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void getTOPParticipations(final Player p) {
		try {
			selectDriver();
			p.sendMessage(HEventos.getHEventos().getConfigUtil().getTopVencedores());
			ResultSet rs = connection.createStatement()
					.executeQuery("SELECT * FROM eventos ORDER BY participations DESC LIMIT 10");
			int i = 0;
			while (rs.next()) {
				i++;
				p.sendMessage(HEventos.getHEventos().getConfigUtil().getTopParticipacoesPosicao()
						.replace("$posicao$", i + "").replace("$player$", rs.getString("player"))
						.replace("$participacoes$", rs.getInt("participations") + ""));
			}
			rs.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
