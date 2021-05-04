package mcxyhj.cn.knkiss.game;

import mcxyhj.cn.knkiss.manager.Manager;
import mcxyhj.cn.knkiss.room.SwapLocationRoom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class SwapLocationGame extends Game implements Listener {
	
	public SwapLocationGame(String name, int startTimeMax, int playerMin, int playerMax) {
		super(name, startTimeMax, playerMin, playerMax);
		Bukkit.getPluginManager().registerEvents(this, Manager.plugin);
	}
	
	@Override
	public void newRoom(List<String> playerList) {
		roomList.add(new SwapLocationRoom(playerList));
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(hasPlayer(p.getName())) quit(p.getName());
	}
}
