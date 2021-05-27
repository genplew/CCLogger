package me.genplew.plugins.cclogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatLogger implements Listener {

	private CCLogger plugin;

	public ChatLogger(CCLogger plugins) {
		plugins.getServer().getPluginManager().registerEvents(this, plugins);
		this.plugin = plugins;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) throws IOException {
		Player player = event.getPlayer();

		String name = player.getName();

		String message = event.getMessage();

		Location location = player.getLocation();

		int xLocation = (int) location.getX();

		int yLocation = (int) location.getY();

		int zLocation = (int) location.getZ();

		World world = location.getWorld();

		String ipAddress = player.getAddress().getAddress().getHostAddress();

		String worldName = world.getName();

		String date = getDate();

		processInformation(player, name, message, xLocation, yLocation, zLocation, worldName, date, ipAddress);
	}

	public void processInformation(Player player, String playerName, String content, int x, int y, int z,
			String worldName, String date, String ipAddress) {
		boolean globalChat = this.plugin.getConfig().getBoolean("Log.toggle.globalChat");

		File chatFile = new File(this.plugin.getDataFolder(), "chat.log");

			if (globalChat) {
				plugin.writer.writeFile(formatLog(playerName, content, x, y, z, worldName, date, ipAddress), chatFile);
			}

	}

	public String[] formatLog(String playerName, String command, int x, int y, int z, String worldName, String date,
			String ipAddress) {
		String format = this.plugin.getConfig().getString("Log.logFormat");
		String log = format;
		if (log.contains("%ip")) {
			log = log.replaceAll("%ip", ipAddress);
		}
		if (log.contains("%date")) {
			log = log.replaceAll("%date", date);
		}
		if (log.contains("%world")) {
			log = log.replaceAll("%world", worldName);
		}
		if (log.contains("%x")) {
			log = log.replaceAll("%x", Integer.toString(x));
		}
		if (log.contains("%y")) {
			log = log.replaceAll("%y", Integer.toString(y));
		}
		if (log.contains("%z")) {
			log = log.replaceAll("%z", Integer.toString(z));
		}
		if (log.contains("%name")) {
			log = log.replaceAll("%name", playerName);
		}
		if (log.contains("%content")) {
			log = log.replaceAll("%content", Matcher.quoteReplacement(command));
		}

		String[] logArray = { log };
		return logArray;
	}

	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		Date date = new Date();
		return dateFormat.format(date);
	}

}