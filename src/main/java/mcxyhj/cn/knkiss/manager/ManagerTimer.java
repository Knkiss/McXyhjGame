package mcxyhj.cn.knkiss.manager;

import mcxyhj.cn.knkiss.game.Game;
import mcxyhj.cn.knkiss.room.Room;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;


public class ManagerTimer {
	public static List<Game> gameTime = new ArrayList<>();
	public static List<Room> roomTime = new ArrayList<>();
	
	public static void timerInit(){
		new BukkitRunnable() {
			@Override
			public void run() {
				new ArrayList<>(gameTime).forEach(Game::timer);
				new ArrayList<>(roomTime).forEach(Room::timer);
			}
		}.runTaskTimerAsynchronously(Manager.plugin,0L,4L);
	}
}
