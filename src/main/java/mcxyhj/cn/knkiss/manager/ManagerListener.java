package mcxyhj.cn.knkiss.manager;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ManagerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		e.getPlayer().setGameMode(GameMode.SPECTATOR);
		e.getPlayer().teleport(new Location(Bukkit.getWorld("world"),-26.5,80,-150));
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		e.getPlayer().teleport(new Location(Bukkit.getWorld("world"),-26.5,80,-150));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Manager.gameMap.forEach((s, game) -> {
			if(game.hasPlayer(e.getPlayer().getName()))game.quit(e.getPlayer().getName());
		});
	}
}
