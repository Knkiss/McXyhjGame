package mcxyhj.cn.knkiss.room;

import mcxyhj.cn.knkiss.game.Game;
import mcxyhj.cn.knkiss.manager.Manager;
import mcxyhj.cn.knkiss.manager.ManagerTimer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SplatoonRoom extends Room implements Listener {
	List<String> ATeam,BTeam;
	int time = 300;
	int ANumber = 0,BNumber = 0;
	
	public static World pltWorld = Bukkit.getWorld("Splatoon");
	
	public SplatoonRoom(Game game,List<String> playerList) {
		super(game,playerList);
		Bukkit.getPluginManager().registerEvents(this,Manager.plugin);
		playBar.setColor(BarColor.WHITE);
		
		Collections.shuffle(playerList);
		ATeam = playerList.subList(0,playerList.size()/2);
		BTeam = playerList.subList(playerList.size()/2,playerList.size());
		
		teamBar.put("ATeam",Bukkit.createBossBar("§6 A 队 ", BarColor.BLUE, BarStyle.SOLID));
		teamBar.put("BTeam",Bukkit.createBossBar("§6 B 队 ", BarColor.RED, BarStyle.SOLID));
		
		ATeam.forEach(name -> {
			Player p = Bukkit.getPlayerExact(name);
			if(p!=null) teamBar.get("ATeam").addPlayer(p);
		});
		BTeam.forEach(name -> {
			Player p = Bukkit.getPlayerExact(name);
			if(p!=null) teamBar.get("BTeam").addPlayer(p);
		});
		
		//传送到地图并开始监听
		Queue<Player> playerQueue = new LinkedList<>();
		playerList.forEach(s -> {
			Player player = Bukkit.getPlayerExact(s);
			if (player!=null) playerQueue.offer(player);
		});
		
		Bukkit.getScheduler().runTask(Manager.plugin,()->{
			assert pltWorld != null;
			pltWorld.setTime(0);
			
			for(Player p : playerQueue) {
				p.setGameMode(GameMode.CREATIVE);
				p.getInventory().clear();
				//TODO 固定地图
				p.teleport(new Location(pltWorld, 0, 255, 0));
				
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 255));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,400,255));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,400,255));
			}
		});
	}
	
	@Override
	public boolean isFinish() {
		return time<=0;
	}
	
	@Override
	public void finish() {
		List<String> winner;
		String winnerTeam;
		
		if(ANumber>BNumber){
			winner = ATeam;
			winnerTeam = "§bA队 蓝色";
		}else if(ANumber<BNumber){
			winner = BTeam;
			winnerTeam = "§cB队 红色";
		}else{
			winner = null;
			winnerTeam = "无人";
		}
		
		playerList.forEach(s -> {
			Player p = Bukkit.getPlayerExact(s);
			if (p!=null){
				if(winner == null){
					p.sendTitle("§6平局！","§6实力均衡",20,100,20);
				}else{
					p.sendTitle(winnerTeam+"获胜！","§6队员是 "+org.apache.commons.lang.StringUtils.join(winner.toArray(), ","),20,100,20);
				}
				
				Bukkit.getScheduler().runTask(Manager.plugin,()->{
					p.setGameMode(GameMode.SURVIVAL);
					p.teleport(Manager.spawnPoint);
				});
				
			}
		});
		ManagerTimer.roomTime.remove(this);
		playBar.removeAll();
		teamBar.forEach((s, bossBar) -> bossBar.removeAll());
		game.removeRoom(this);
		playerList.clear();
		quitList.clear();
	}
	
	@Override
	public boolean inGame(String name) {
		return !quitList.contains(name);
	}
	
	@Override
	public boolean hasPlayer(String name) {//所有在线玩家
		return playerList.contains(name);
	}
	
	@Override
	public boolean quit(String name) {
		if(isFinish())return true;
		if(!quitList.contains(name)){
			quitList.add(name);
		}
		return false;
	}
	
	@Override
	public void reConnect(Player player){
		if(quitList.contains(player.getName())){//重连判断
			quitList.remove(player.getName());
			player.sendMessage("你将被重连回游戏中");
			
			playBar.addPlayer(player);
			if(ATeam.contains(player.getName())){
				teamBar.get("ATeam").addPlayer(player);
			}else{
				teamBar.get("BTeam").addPlayer(player);
			}
			//角色独立重新初始化
			//player.teleport()
		}
	}
	
	@Override
	public void timer() {
		time--;
		playBar.setProgress((time+1) / 300.0);
		playBar.setTitle("§f游戏即将结束:§6"+time+"§f秒");
		teamBar.get("ATeam").setTitle("§f我方 §6§l"+ANumber+" §f:§6§l "+BNumber+" §f对方");
		teamBar.get("BTeam").setTitle("§f我方 §6§l"+BNumber+" §f:§6§l "+ANumber+" §f对方");
		double num = ANumber+BNumber==0?0:ANumber/(ANumber*1.0+BNumber);
		teamBar.get("ATeam").setProgress(num);
		if(num==0) teamBar.get("BTeam").setProgress(num);
		else teamBar.get("BTeam").setProgress(1.0-num);
		if(time==0)finish();
	}
	
	@EventHandler
	public void onShoot(ProjectileHitEvent e){
		if(e.getHitBlock() == null) return;
		if(!e.getEntity().getType().equals(EntityType.SNOWBALL))return;
		if(e.getEntity().getShooter() instanceof Player) {
			Player player = (Player) e.getEntity().getShooter();
			if (!hasPlayer(player.getName())) return;
			Block block = e.getHitBlock();
			if (ATeam.contains(player.getName())) {//A = 蓝队
				if (block.getType().equals(Material.RED_CONCRETE)) {
					ANumber++;
					if(BNumber>0) BNumber--;
					
					block.setType(Material.BLUE_CONCRETE);
				}else{
					ANumber++;
					block.setType(Material.BLUE_CONCRETE);
				}
			}
			if (BTeam.contains(player.getName())) {//B = 红队
				if (block.getType().equals(Material.BLUE_CONCRETE)) {
					BNumber++;
					if(ANumber>0) ANumber--;
					block.setType(Material.RED_CONCRETE);
				}else{
					BNumber++;
					block.setType(Material.RED_CONCRETE);
				}
			}
		}
	}
}
