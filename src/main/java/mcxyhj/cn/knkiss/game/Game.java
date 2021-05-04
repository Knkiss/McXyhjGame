package mcxyhj.cn.knkiss.game;

import mcxyhj.cn.knkiss.manager.ManagerTimer;
import mcxyhj.cn.knkiss.room.Room;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

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
	
	public Game(String name,int startTimeMax,int playerMin,int playerMax){
		this.name = name;
		this.startTimeMax = startTimeMax;
		this.startTime = startTimeMax;
		this.playerMin = playerMin;
		this.playerMax = playerMax;
		waitBar = Bukkit.createBossBar(name+" 请等待其他玩家加入", BarColor.RED, BarStyle.SOLID);
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
					p.sendTitle("游戏正式开始","",10,30,0);
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
			waitBar.setTitle(this.name +" 请等待其他玩家加入");
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
	
	public void timer(){
		if(startTime > 0){
			startTime --;
			waitBar.setProgress((1.0*startTime)/startTimeMax);
			waitBar.setTitle("队列人数:"+waitList.size()+"  即将开始于:"+(startTime+1)+"秒");
			waitBar.setColor(BarColor.GREEN);
		}else{
			start();
			startTime = startTimeMax;
			waitBar.setProgress(1.0);
			waitBar.setTitle(this.name +" 请等待其他玩家加入");
			waitBar.setColor(BarColor.RED);
			ManagerTimer.gameTime.remove(this);
		}
	}
}
