package mcxyhj.cn.knkiss.room;

import mcxyhj.cn.knkiss.game.Game;
import mcxyhj.cn.knkiss.manager.Manager;
import mcxyhj.cn.knkiss.manager.ManagerTimer;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SwapLocationRoom extends Room  {
	public int changeTime = 180;
	
	public static World slgWorld = Bukkit.getWorld("SwapLocation");
	
	public SwapLocationRoom(Game game, List<String> playerList) {
		super(game,playerList);
		
		//地图初始化 TODO 相同内地图
		Queue<Player> playerQueue = new LinkedList<>();
		playerList.forEach(s -> {
			Player player = Bukkit.getPlayerExact(s);
			if (player!=null) playerQueue.offer(player);
		});
		
		Bukkit.getScheduler().runTask(Manager.plugin,()->{
			assert slgWorld != null;
			slgWorld.setTime(0);
			
			int x = 0,z = 0;
			boolean isOcean = true;
			for(Player p : playerQueue) {
				p.setGameMode(GameMode.SURVIVAL);
				p.getInventory().clear();
				Random r = new Random(new Date().getTime());
				
				while(isOcean){
					x = r.nextInt(5001);
					z = r.nextInt(5001);
					Biome biome = slgWorld.getBiome(x,200,z);
					if(!biome.equals(Biome.OCEAN)
							&&!biome.equals(Biome.DEEP_OCEAN)
							&&!biome.equals(Biome.COLD_OCEAN)
							&&!biome.equals(Biome.DEEP_COLD_OCEAN)
							&&!biome.equals(Biome.FROZEN_OCEAN)
							&&!biome.equals(Biome.DEEP_FROZEN_OCEAN)
							&&!biome.equals(Biome.LUKEWARM_OCEAN)
							&&!biome.equals(Biome.DEEP_LUKEWARM_OCEAN)
							&&!biome.equals(Biome.WARM_OCEAN)
							&&!biome.equals(Biome.DEEP_WARM_OCEAN)){
						isOcean = false;
					}
				}
				Location l = new Location(slgWorld, x, 255, z);
				p.teleport(l);
				isOcean = true;
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 255));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,400,255));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,400,255));
			}
		});
	}
	
	@Override
	public boolean isFinish() {
		return playerList.size()<=1;
	}
	
	@Override
	public void finish() {
		String winner = playerList.get(0);
		quitList.add(winner);
		quitList.forEach(s -> {
			Player p = Bukkit.getPlayerExact(s);
			if (p!=null){
				p.sendTitle(winner,"§6获胜！",20,100,20);
				Bukkit.getScheduler().runTask(Manager.plugin,()->{
					p.setGameMode(GameMode.SURVIVAL);
					p.teleport(Manager.spawnPoint);
				});
				
			}
		});
		ManagerTimer.roomTime.remove(this);
		playBar.removeAll();
	}
	
	@Override
	public boolean inGame(String name) {//离开玩家和失败玩家
		return !quitList.contains(name);
	}
	
	@Override
	public boolean hasPlayer(String name) {//在线玩家
		return playerList.contains(name);
	}
	
	@Override
	public boolean quit(String name) {
		if(playerList.contains(name)){
			playerList.remove(name);
			quitList.add(name);
		}
		if(isFinish()){
			finish();
			return true;
		}
		return false;
	}
	
	@Override
	public void reConnect(Player player) {
		player.sendMessage("交换位置大冒险无法重连，这局游戏结束后你将不再收到此提示");
	}
	
	@Override
	public void timer() {
		changeTime--;
		playBar.setProgress((changeTime+1) / 180.0);
		playBar.setTitle("§f下一次交换:§6"+changeTime+"§f秒  存活玩家:§6"+playerList.size()+"§f人");
		if(changeTime==0){
			changeTime = 180;
			playBar.setColor(BarColor.GREEN);
			swap();
		}else if(changeTime==60){
			playBar.setColor(BarColor.RED);
		}
		else if(changeTime==120){
			playBar.setColor(BarColor.YELLOW);
		}
	}
	
	public void swap(){
		Queue<Player> playerQueue = new LinkedList<>();
		
		playerList.forEach(s -> {
			Player p = Bukkit.getPlayerExact(s);
			if(p!=null) playerQueue.offer(p);
		});
		
		List<Location> lastLocation = new ArrayList<>();
		for(Player p : playerQueue) lastLocation.add(p.getLocation());
		Random rand = new Random();
		List<Location> randomLocation = new ArrayList<>(lastLocation);
		Collections.shuffle(randomLocation,rand);
		for(int i = 0; i < lastLocation.size(); i++){
			if(lastLocation.get(i) == randomLocation.get(i)){
				if(i != lastLocation.size()-1){
					Location temp = randomLocation.get(i);
					randomLocation.set(i,randomLocation.get(i+1));
					randomLocation.set(i+1,temp);
					i = 0;
				}
				else if(i == lastLocation.size()-1){
					Location temp = randomLocation.get(i);
					randomLocation.set(i,randomLocation.get(0));
					randomLocation.set(0,temp);
					i = 0;
				}
			}
		}
		Bukkit.getScheduler().runTask(Manager.plugin,()->{
			int i = 0;
			for(Player p : playerQueue){
				p.teleport(randomLocation.get(i));
				i++;
			}
		});
	}
}
