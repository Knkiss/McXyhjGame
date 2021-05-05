package mcxyhj.cn.knkiss.manager;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class ManagerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
		e.getPlayer().teleport(new Location(Bukkit.getWorld("world"),-120,64,-268));
		e.getPlayer().setBedSpawnLocation(new Location(Bukkit.getWorld("world"),-120,64,-268),true);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		e.getPlayer().teleport(new Location(Bukkit.getWorld("world"),-120,64,-268));
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
		p.closeInventory();
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
