package mcxyhj.cn.knkiss.room;

import mcxyhj.cn.knkiss.manager.ManagerTimer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Room{
	public static List<String> playerList; //正在游戏玩家 或进行分发
	public List<String> quitList = new ArrayList<>(); //已离开玩家
	public BossBar playBar;
	
	public Room(List<String> playerList){
		Room.playerList = playerList;
		playBar = Bukkit.createBossBar("游戏正式开始", BarColor.GREEN, BarStyle.SOLID);
		playerList.forEach(name -> {
			Player p = Bukkit.getPlayerExact(name);
			if(p!=null) playBar.addPlayer(p);
		});
		ManagerTimer.roomTime.add(this);
	}
	
	public abstract boolean isFinish();
	public abstract void finish();
	public abstract boolean hasPlayer(String name);
	public abstract boolean quit(String name);
	public abstract void timer();
}
