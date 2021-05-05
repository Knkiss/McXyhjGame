package mcxyhj.cn.knkiss.manager;

import mcxyhj.cn.knkiss.game.Game;
import mcxyhj.cn.knkiss.game.SplatoonGame;
import mcxyhj.cn.knkiss.game.SwapLocationGame;
import mcxyhj.cn.knkiss.room.SplatoonRoom;
import mcxyhj.cn.knkiss.room.SwapLocationRoom;
import org.bukkit.*;
import org.bukkit.entity.Player;
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
	public static World mainWorld = Bukkit.getWorld("world");
	public static Location spawnPoint = new Location(mainWorld, -120, 64, -268);
	
	public static void onEnable(){
		plugin.getLogger().info("onEnable");
		systemInit();
		gameInit();
		ManagerGui.guiInit();
		worldInit();
		
	}
	
	public static void onDisable(){
		plugin.getLogger().info("onDisable");
		barClear();
	}
	
	public static void onReload(){
		plugin.getLogger().info("onReload");
		for (Player player : Bukkit.getOnlinePlayers()) {
			Bukkit.getScheduler().runTask(plugin, () -> {
				player.setGameMode(GameMode.SURVIVAL);
				player.teleport(spawnPoint);
			});
		}
	}
	
	private static void systemInit(){
		ManagerTimer.timerInit();
		Bukkit.getPluginManager().registerEvents(new ManagerListener(),plugin);
		Objects.requireNonNull(plugin.getCommand("game")).setExecutor(new ManagerCommand());
	}
	
	private static void gameInit(){
		gameMap.put("swaplocation", new SwapLocationGame("§b交换位置大冒险",60,2,10));
		gameMap.put("splatoon", new SplatoonGame("§b涂鸦大冒险",60,2,10));
	}
	
	private static void barClear(){
		gameMap.forEach((s, game) -> {
			game.waitBar.removeAll();
			game.roomList.forEach(room -> {
				room.playBar.removeAll();
				room.teamBar.forEach((s1, bossBar) -> bossBar.removeAll());
			});
		});
	}
	
	public static boolean hasPlayer(Player player){
		boolean has = false;
		for (Map.Entry<String, Game> entry : gameMap.entrySet()) {
			if(entry.getValue().hasPlayer(player.getName()))has = true;
		}
		return has;
	}
	
	public static boolean inGame(Player player){
		boolean has = false;
		for (Map.Entry<String, Game> entry : gameMap.entrySet()) {
			if(entry.getValue().inGame(player.getName()))has = true;
		}
		return has;
	}
	
	public static void worldInit(){
		Bukkit.getScheduler().runTask(Manager.plugin,()->{
			SwapLocationRoom.slgWorld = Bukkit.createWorld(new WorldCreator("SwapLocation").seed(new Date().getTime()));
			SplatoonRoom.pltWorld = Bukkit.createWorld(new WorldCreator("Splatoon").seed(new Date().getTime()));
			mainWorld.setDifficulty(Difficulty.PEACEFUL);
		});
	}
}
