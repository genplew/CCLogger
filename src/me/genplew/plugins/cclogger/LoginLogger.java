package me.genplew.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginLogger implements Listener {

	private CCLogger plugin;

	public LoginLogger(CCLogger plugins) {
		plugins.getServer().getPluginManager().registerEvents(this, plugins);
		this.plugin = plugins;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
		Player player = event.getPlayer();
		String name = player.getName();
		Location location = player.getLocation();
		int x = (int) location.getX();
		int y = (int) location.getY();
		int z = (int) location.getZ();
		World world = location.getWorld();
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		String worldName = world.getName();
		String date = getDate();
		String login = "1";
		processInformationJoin(player, name, login, x, y, z, worldName, date, ipAddress);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) throws IOException {
		Player player = event.getPlayer();
		String name = player.getName();
		Location location = player.getLocation();
		int x = (int) location.getX();
		int y = (int) location.getY();
		int z = (int) location.getZ();
		World world = location.getWorld();
		String ipAddress = player.getAddress().getAddress().getHostAddress();
		String worldName = world.getName();
		String date = getDate();
		String login = "0";
		processInformationQuit(player, name, login, x, y, z, worldName, date, ipAddress);
	}

	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void processInformationJoin(Player player, String playerName, String login, int x, int y, int z,
			String worldName, String date, String ipAddress) {
		boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
		File chatFile = new File(this.plugin.getDataFolder(), "login.log");
		String[] log = { "[" + ipAddress + "]" + "[" + date + "] " + playerName + " conectado." };

			if (globalLogin) {
				plugin.writer.writeFile(log, chatFile);
			}

	}

	public void processInformationQuit(Player player, String playerName, String login, int x, int y, int z,
			String worldName, String date, String ipAddress) {
		boolean globalLogin = this.plugin.getConfig().getBoolean("Log.toggle.globalLogin");
		File chatFile = new File(this.plugin.getDataFolder(), "login.log");
		String[] log = { "[" + ipAddress + "]" + "[" + date + "] " + playerName + " desconectado." };

			if (globalLogin) {
				plugin.writer.writeFile(log, chatFile);
			}

	}

}