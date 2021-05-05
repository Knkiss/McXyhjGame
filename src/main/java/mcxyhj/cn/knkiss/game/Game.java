package mcxyhj.cn.knkiss.game;

import mcxyhj.cn.knkiss.manager.ManagerTimer;
import mcxyhj.cn.knkiss.room.Room;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public abstract class Game {
	public String name; //游戏名
	public List<Room> roomList = new ArrayList<>(); //房间列表
	public LinkedList<String> waitList = new LinkedList<>(); //等待队列
	public BossBar waitBar; //游戏独立标题
	
	public int startTimeMax; //最大等待时间
	public int startTime; //当前等待时间
	
	public int playerMin; //最少玩家数量
	public int playerMax; //最大玩家数量
	
	public ItemStack icon = new ItemStack(Material.STONE,1);
	
	public Game(String name,int startTimeMax,int playerMin,int playerMax){
		this.name = name;
		this.startTimeMax = startTimeMax;
		this.startTime = startTimeMax;
		this.playerMin = playerMin;
		this.playerMax = playerMax;
		waitBar = Bukkit.createBossBar(name+" §f请等待其他玩家加入", BarColor.RED, BarStyle.SOLID);
	}
	
	void initIcon(Material material){
		icon = new ItemStack(material,1);
		ItemMeta im = icon.getItemMeta();
		assert im != null;
		im.setDisplayName(name);
		List<String> loreList = new ArrayList<>();
		
		loreList.add("§8单局最低玩家数量: §7§l"+playerMin+"§8 人");
		loreList.add("§8单局最高玩家数量: §7§l"+playerMax+"§8 人");
		
		
		im.setLore(loreList);
		icon.setItemMeta(im);
	}
	
	public void start(){
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
			newRoom(playerList);
		}
	}
	
	public abstract void newRoom(List<String> playerList);
	
	public void join(String name){
		//玩家进入后准备开始
		if(hasPlayer(name))return;
		waitList.addLast(name);
		Player p = Bukkit.getPlayerExact(name);
		if(p!=null) waitBar.addPlayer(p);
		if(waitList.size()>=playerMin) if(!ManagerTimer.gameTime.contains(this)) ManagerTimer.gameTime.add(this);
		if(waitList.size()>=playerMax && startTime>startTimeMax/2)  startTime = startTimeMax/2; //超大玩家数量时
		if(waitList.size()>=playerMin && Bukkit.getOnlinePlayers().size() <= waitList.size()) startTime = startTimeMax/2; //小数量玩家 测试取消
	}
	
	public void quit(String name){
		//玩家离开判断取消
		if (!hasPlayer(name))return;
		Player p = Bukkit.getPlayerExact(name);
		if(p!=null) waitBar.removePlayer(p);
		List<Room> finishRoomList = new ArrayList<>();
		if(!waitList.contains(name)){
			roomList.forEach(room -> {
				if(room.quit(name)){
					finishRoomList.add(room);
				}
			});
		}else{
			waitList.remove(name);
		}
		roomList.removeAll(finishRoomList);
		if(waitList.size()<playerMin){
			startTime = startTimeMax;
			waitBar.setProgress(1.0);
			waitBar.setTitle(this.name +" §f请等待其他玩家加入");
			waitBar.setColor(BarColor.RED);
			ManagerTimer.gameTime.remove(this);
		}
	}
	
	public boolean hasPlayer(String name){
		boolean has = waitList.contains(name);
		for(Room room: roomList){
			if (room.hasPlayer(name)) has = true;
		}
		return has;
	}
	
	public boolean inGame(String name){
		boolean has = false;
		for(Room room: roomList){
			if (room.hasPlayer(name)) has = true;
		}
		return has;
	}
	
	public void timer(){
		if(startTime > 0){
			startTime --;
			waitBar.setProgress((1.0*startTime)/startTimeMax);
			waitBar.setTitle("§f队列人数:§6"+waitList.size()+"  §f即将开始于:§6"+(startTime+1)+"§f秒");
			waitBar.setColor(BarColor.GREEN);
		}else{
			start();
			startTime = startTimeMax;
			waitBar.setProgress(1.0);
			waitBar.setTitle(this.name +" §f请等待其他玩家加入");
			waitBar.setColor(BarColor.RED);
			ManagerTimer.gameTime.remove(this);
		}
	}
}
