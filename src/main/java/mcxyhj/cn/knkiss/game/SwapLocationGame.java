package mcxyhj.cn.knkiss.game;

import mcxyhj.cn.knkiss.manager.Manager;
import mcxyhj.cn.knkiss.room.SwapLocationRoom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.LinkedList;

public class SwapLocationGame extends Game implements Listener {
	
	public SwapLocationGame(String name, int startTimeMax, int playerMin, int playerMax) {
		super(name, startTimeMax, playerMin, playerMax);
		initIcon(Material.ENDER_EYE,new ArrayList<>());
		Bukkit.getPluginManager().registerEvents(this, Manager.plugin);
		this.canQuit = true;
	}
	
	@Override
	public void start() {
		while(waitList.size() >= playerMin){
			int playerNum = Math.min(playerMax,waitList.size());
			LinkedList<String> playerList = new LinkedList<>();
			for(int i=0;i<playerNum;i++){
				String name = waitList.removeFirst();
				playerList.offer(name);
				Player p = Bukkit.getPlayerExact(name);
				if(p!=null) {
					p.sendTitle("§6游戏正式开始","",10,30,0);
					waitBar.removePlayer(p);
				}
			}
			roomList.add(new SwapLocationRoom(this,playerList));
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(inGame(p.getName())){
			p.setGameMode(GameMode.SPECTATOR);
			quit(p.getName());
		}
	}
}
