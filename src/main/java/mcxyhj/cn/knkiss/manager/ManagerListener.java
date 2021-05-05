package mcxyhj.cn.knkiss.manager;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class ManagerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		AtomicBoolean reConnect = new AtomicBoolean(false);
		Manager.gameMap.forEach((s, game) -> {
			if(game.inGame(e.getPlayer().getName())){
				game.roomList.forEach(room -> {
					if(!room.inGame(e.getPlayer().getName())){
						room.reConnect(e.getPlayer());
						reConnect.set(true);
					}
				});
			}
		});
		if(!reConnect.get()){
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
			e.getPlayer().teleport(Manager.spawnPoint);
			e.getPlayer().setBedSpawnLocation(Manager.spawnPoint,true);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		e.getPlayer().teleport(Manager.spawnPoint);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Manager.gameMap.forEach((s, game) -> {
			if(game.hasPlayer(e.getPlayer().getName()))game.quit(e.getPlayer().getName());
		});
	}
	
	@EventHandler
	public void onClickGui(InventoryClickEvent e){
		if(!e.getInventory().equals(ManagerGui.guiMain)) return;
		if(e.getCurrentItem() == null) return;
		e.setCancelled(true);
		int num = e.getRawSlot();
		Player p = (Player)e.getWhoClicked();
		if(num < 53){
			p.performCommand("game join "+ManagerGui.gameList.get(num));
		}else if(num==53){
			p.performCommand("game quit");
		}
	}
	
	@EventHandler
	public void openGui(PlayerSwapHandItemsEvent e){
		if(e.getPlayer().isSneaking()){
			if(!Manager.inGame(e.getPlayer())){
				ManagerGui.openMenu(e.getPlayer());
			}
		}
	}
}
