package mcxyhj.cn.knkiss.manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ManagerGui {
	static final Inventory guiMain = Bukkit.createInventory(null,54,"星夜幻境 小游戏");
	static final LinkedList<String> gameList = new LinkedList<>();
	
	public static void guiInit(){
		Manager.gameMap.forEach((s, game) -> {
			guiMain.addItem(game.icon);
			gameList.addLast(s);
		});
		ItemStack quit = new ItemStack(Material.RED_BED,1);
		ItemMeta im = quit.getItemMeta();
		assert im != null;
		im.setDisplayName("§c退出游戏");
		List<String> loreList = new ArrayList<>();
		loreList.add("§4Exit");
		im.setLore(loreList);
		quit.setItemMeta(im);
		guiMain.setItem(53,quit);
	}
	
	public static void openMenu(Player player){
		player.openInventory(guiMain);
	}
}
