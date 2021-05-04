package mcxyhj.cn.knkiss.manager;

import mcxyhj.cn.knkiss.game.Game;
import mcxyhj.cn.knkiss.game.SwapLocationGame;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/*  需求
 *   高性能、多记录
 *   多个游戏、多个房间、多个地图
 *
 *  概念：一个玩家同时只存在于一个游戏的一个房间的一个队伍中，所以可以将在线的玩家仅保存在对应的房间中，游戏结束时退回玩家
 *  玩家状态：不在游戏中，在游戏中正常，在游戏中离开
 *
 *
 * */

public class Manager implements Listener {
	public static JavaPlugin plugin;
	public static HashMap<String, Game> gameMap = new HashMap<>();//游戏名对应游戏
	
	public static void onEnable(){
		plugin.getLogger().info("onEnable");
		systemInit();
		gameInit();
	}
	
	public static void onDisable(){
		plugin.getLogger().info("onDisable");
		barClear();
	}
	
	public static void onReload(){
		plugin.getLogger().info("onReload");
		Bukkit.getOnlinePlayers().forEach(player -> {
			Bukkit.getScheduler().runTask(plugin,()->{
				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(new Location(Bukkit.getWorld("world"),-26.5,80,-150));
			});
		});
	}
	
	private static void systemInit(){
		ManagerTimer.timerInit();
		Bukkit.getPluginManager().registerEvents(new ManagerListener(),plugin);
		Objects.requireNonNull(plugin.getCommand("game")).setExecutor(new ManagerCommand());
	}
	
	private static void gameInit(){
		gameMap.put("SwapLocation", new SwapLocationGame("交换位置大冒险",60,2,10));
	}
	
	private static void barClear(){
		gameMap.forEach((s, game) -> {
			game.waitBar.removeAll();
			game.roomList.forEach(room -> {
				room.playBar.removeAll();
			});
		});
	}
}
