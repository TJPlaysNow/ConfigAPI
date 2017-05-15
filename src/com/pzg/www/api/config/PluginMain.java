package com.pzg.www.api.config;
import org.bukkit.plugin.java.JavaPlugin;
public class PluginMain extends JavaPlugin {
	/** Creating a new config */
	Config config;
	@Override
	public void onEnable() {
		/** Setting up the config */
		config = new Config(/** Config Path */"plugins/config", /** Config file name */"config.yml", /** The creation method */ new ConfigCreate() {
			@Override
			public void configCreate() {
				/** Stuff happens here */
			}
		}, /** The default Plugin class */ this);
	}
}