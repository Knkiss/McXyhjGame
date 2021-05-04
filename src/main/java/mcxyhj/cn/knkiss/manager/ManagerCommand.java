package mcxyhj.cn.knkiss.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ManagerCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length<1)sender.sendMessage("open the gui");
		if(args.length >= 1){
			
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
			
			String gameType = "SwapLocation";
			if(args[0].equalsIgnoreCase("join")){
				if(args.length > 1) {
					for(int i = 0; i<Integer.parseInt(args[1]); i++){
						Manager.gameMap.get(gameType).join("test"+i);
					}
				}
				else Manager.gameMap.get(gameType).join(sender.getName());
			}
			if(args[0].equalsIgnoreCase("quit")){
				if(args.length > 1)Manager.gameMap.get(gameType).quit(args[1]);
				else Manager.gameMap.get(gameType).quit(sender.getName());
			}
		}
		return false;
	}
}
