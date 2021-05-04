package mcxyhj.cn.knkiss;

import mcxyhj.cn.knkiss.manager.Manager;
import org.bukkit.plugin.java.JavaPlugin;

public final class McXyhjGame extends JavaPlugin {
	
	@Override
	public void onEnable() {
		Manager.plugin = this;
		Manager.onEnable();
		if(!this.getServer().getOnlinePlayers().isEmpty())Manager.onReload();
	}
	
	@Override
	public void onDisable() {
		Manager.onDisable();
	}
	
	public static void main(String[] args){}
}
