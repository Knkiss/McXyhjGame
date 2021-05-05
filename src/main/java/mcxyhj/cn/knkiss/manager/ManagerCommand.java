package mcxyhj.cn.knkiss.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ManagerCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length<1) {
			if(sender instanceof Player) ManagerGui.openMenu((Player) sender);
			else sender.sendMessage("§f控制台无法打开GUI操作");
		}else{
			if(sender.hasPermission("mcxyhj.game.admin")){
				if(args[0].equalsIgnoreCase("debug")){
					Manager.gameMap.forEach((gameName, game) -> {
						sender.sendMessage(gameName+"-----------");
						sender.sendMessage("wait:"+game.waitList.toString());
						game.roomList.forEach(room -> {
							sender.sendMessage("player:"+room.playerList.toString());
							sender.sendMessage("quit:"+room.quitList.toString());
						});
					});
				}
			}
			
			if(args[0].equalsIgnoreCase("join")){
				if(args.length >= 2) {
					if(Manager.gameMap.containsKey(args[1].toLowerCase())){
						if(!(sender instanceof Player)){
							if(args.length>=3){
								for(int i=0;i<Integer.parseInt(args[2]);i++){
									Manager.gameMap.get(args[1].toLowerCase()).join("test"+i);
								}
							}else{
								Manager.gameMap.get(args[1].toLowerCase()).join(sender.getName());
							}
							return true;
						}
						if(!Manager.hasPlayer((Player)sender)){
							Manager.gameMap.get(args[1].toLowerCase()).join(sender.getName());
						}
					}
				}
			}
			if(args[0].equalsIgnoreCase("quit")){
				Manager.gameMap.forEach((s, game) -> {
					if(game.hasPlayer(sender.getName())){
						if(!game.inGame(sender.getName()))game.quit(sender.getName()); //等待队列直接离开
						else if(game.canQuit)game.quit(sender.getName()); //游戏里且可退出则离开
					}
				});
			}
		}
		return false;
	}
}
