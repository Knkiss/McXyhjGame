package mcxyhj.cn.knkiss.room;

import mcxyhj.cn.knkiss.game.Game;
import org.bukkit.entity.Player;

import java.util.List;

public class RichManRoom extends Room{
	public RichManRoom(Game game, List<String> playerList) {
		super(game, playerList);
	}
	
	@Override
	public boolean isFinish() {
		return false;
	}
	
	@Override
	public void finish() {
	
	}
	
	@Override
	public boolean inGame(String name) {
		return false;
	}
	
	@Override
	public boolean hasPlayer(String name) {
		return false;
	}
	
	@Override
	public boolean quit(String name) {
		return false;
	}
	
	@Override
	public void reConnect(Player player) {
	
	}
	
	@Override
	public void timer() {
	
	}
}
