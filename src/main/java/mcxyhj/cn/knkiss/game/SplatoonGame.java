package mcxyhj.cn.knkiss.game;

import mcxyhj.cn.knkiss.manager.Manager;
import mcxyhj.cn.knkiss.room.SplatoonRoom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.LinkedList;

public class SplatoonGame extends Game implements Listener {
	public SplatoonGame(String name, int startTimeMax, int playerMin, int playerMax) {
		super(name, startTimeMax, playerMin, playerMax);
		initIcon(Material.SNOWBALL,new ArrayList<>());
		Bukkit.getPluginManager().registerEvents(this, Manager.plugin);
		this.canQuit = false;
	}
	
	@Override
	public void start() {
		while(waitList.size() >= playerMin){
			int playerNum = Math.min(playerMax,waitList.size());
			if(playerNum % 2 != 0) playerNum-=1;//这个游戏仅允许偶数个玩家进入，公平对抗
			
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
			roomList.add(new SplatoonRoom(this,playerList));
		}
	}
}
