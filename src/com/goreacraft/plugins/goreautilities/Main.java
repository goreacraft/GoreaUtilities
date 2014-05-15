package com.goreacraft.plugins.goreautilities;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
	
	public final Logger logger = Logger.getLogger("minecraft");	
	public static Main plugin;
	private List<String> aliases;
	private File output;
	public static File polloutput;
	public static double poolcooldown;
	//private HashMap<String,Object> data= new HashMap<String,Object>();
	static YamlConfiguration datayml = new YamlConfiguration();
	
	static YamlConfiguration polldebug = new YamlConfiguration();
	public static boolean voteplayernames;
	public static long poolduration;
	public static String custompollsmessage;
	
	
	
	@Override
    public void onEnable()
    { 
		
		
		PluginDescriptionFile pdfFile = this.getDescription();
		String Version = pdfFile.getVersion();
    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled! " + pdfFile.getWebsite());
    	plugin=this;
		getConfig().options().copyDefaults(true);
      	getConfig().options().header("If you need help with this plugin you can contact goreacraft on teamspeak ip: goreacraft.com\n Website http://www.goreacraft.com");      	
      	this.getConfig().set("Version", Version);
      	saveConfig();
		output = new File(getDataFolder(), "Output.yml");
		polloutput = new File(getDataFolder(), "PollDebug.yml");
		reloadconfigs();
		
		//Bukkit.getServer().getPluginManager().registerEvents(this, this);
		aliases = getCommand("goreautilities").getAliases();
		
		commandhandler();
		
		//====================================== METRICS STUFF =====================================================
		try {
   		    Metrics metrics = new Metrics(this);
   		    metrics.start();
   		} catch (IOException e) {

   		}
    }
	public static void reloadconfigs(){
		voteplayernames = plugin.getConfig().getBoolean("Display names on vote results");
		custompollsmessage= plugin.getConfig().getString("Deny custom polls message");
		poolcooldown= plugin.getConfig().getDouble("Pool cooldown");
		poolduration=plugin.getConfig().getLong("Pool duration")*20;
		
	}
	
	@Override
    public void onDisable()
    { 
		if(!Poll.yes.isEmpty())Poll.yes.clear();
		if(!Poll.no.isEmpty())Poll.no.clear();
		Bukkit.getScheduler().cancelTasks(plugin);
		
		
		PluginDescriptionFile pdfFile = this.getDescription();
    	this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been disabled!" + pdfFile.getWebsite());   
    }
	
	
	public void setExecutor(String command, CommandExecutor ce){
		Bukkit.getPluginCommand(command).setExecutor(ce);
	}
	public void commandhandler(){
		setExecutor("poll", new Poll(this));
	}
	
	
	
	
	 public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		    
	 if (sender instanceof Player)
		{
			Player player = (Player)sender;
			
			if(args.length==0)
			{
				showplayerhelp(player);
			}
			
			
			if (label.equalsIgnoreCase("gtp"))
		    {
				
				if(args.length>=1)
				{
					if(player.isOp() || player.hasPermission("gorea.tp"))
					{
							player.sendMessage("Searching for player: " + args[0] + " ...");
							if( Metode.findPlayerByString(args[0]) != null)
							{
								Location loc = getServer().getPlayer(args[0]).getLocation();
								player.teleport(loc);
													
								
							} else {
								
								if(Metode.getPlayerLocation(args[0]) != null)
								{
								player.teleport(Metode.getPlayerLocation(args[0]));
								
								} else {player.sendMessage(ChatColor.YELLOW + "[GoreaUtilities] " + ChatColor.RESET + "There is no player with the name " + ChatColor.GOLD + args[0]);}
							}
								
							
							return true;
							
					} else player.sendMessage(ChatColor.RED +"You dont have permission gorea.tp to run this command");
				} else return false;
		    }
			
			if (label.equalsIgnoreCase("gtphere") )
		    {
				if(args.length>=1)
				{
					if(player.isOp() || player.hasPermission("gorea.tphere"))
					{
							if(Metode.findPlayerByString(args[0])!=null)
							{
								Metode.findPlayerByString(args[0]).teleport(player.getLocation());
								player.sendMessage(ChatColor.YELLOW + "[GoreaUtilities] " + ChatColor.RESET + "Player teleported to you!");
								
							} else {
							//target is offline				
							Metode.setPlayerLocation(args[0], player.getLocation());
							player.sendMessage(ChatColor.YELLOW + "[GoreaUtilities] " + ChatColor.RESET + "Player offline, location modified within files! ---- [WIP]");
							}
							return true;
					} else player.sendMessage(ChatColor.RED +"You dont have permission gorea.tphere to run this command");
				} else return false;
		    }		
			
			
			if (label.equalsIgnoreCase("vote"))
		    {
				player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " [GoreaUtilities] "+ ChatColor.YELLOW + ".......................................................");
		    	player.sendMessage( ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
		    	player.sendMessage( ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
		    	player.sendMessage( ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
		    	player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
		    	player.sendMessage("");
		    	player.sendMessage(ChatColor.YELLOW +""+ getConfig().getString("Vote message"));
		    	player.sendMessage("");
				if(getConfig().getKeys(false).contains("Vote links"))
				{
				for(String links: this.getConfig().getStringList("Vote links"))
						{
							player.sendMessage("   - " + ChatColor.AQUA + links);
						}
				} else { player.sendMessage(ChatColor.YELLOW + "[GoreaUtilities] " + ChatColor.RED + "No links in configs under entry 'Vote links'");}
				return true;
		    }
		if(aliases.contains(label))
		{	
		   if (args.length == 1 )
            	{
		    		if(args[0].equals("reload") )
		    		{
		    			if(player.isOp() || player.hasPermission("goreautilities.reload"))
		    			{
			    			Poll.yes.clear();
							Poll.no.clear();
							Bukkit.getScheduler().cancelTasks(plugin);
			    			reloadconfigs();
			    			
							
			    			this.reloadConfig();	
			    			player.sendMessage(ChatColor.YELLOW + "[GoreaUtilities] " + ChatColor.GREEN + "Reloaded Configs.");
			    			return true;
		    			}else player.sendMessage(ChatColor.RED +"You dont have permissions to run this command");
		    		}
		    		if(args[0].equals("help") ||  args[0].equals("?"))
		    		{
		    			showplayerhelp(player);
		    			return true;
		    		}
		    		
            	} 
		    if (args.length == 2)
	            {
		    	
		    	if(player.isOp() || player.hasPermission("goreautilities.advanced"))
    			{
		    		if (args[0].equalsIgnoreCase("players") || args[0].equalsIgnoreCase("p"))	
		    		{
		    			String file = null;
		    			if (args[1].equalsIgnoreCase("locations") || args[1].equalsIgnoreCase("l"))
		    			{
						       if(!output.exists())
						           {
						            try {output.createNewFile(); } 
						    	    catch (IOException e) { e.printStackTrace();}						            	
						            }
						            Metode.getPlayersDimensions();
						            datayml.createSection("PlayersLocations");
						            datayml.set("PlayersLocations", Metode.getPlayersDimensions());
						            try {
										datayml.save(output);
									} catch (IOException e) {
										e.printStackTrace();
									}	
						            file = output.getPath();
						       
					    } 
		    			player.sendMessage(ChatColor.YELLOW + "[GoreaUtilities] " + ChatColor.GREEN + "Data saved to file: " + file);
					    return true;
	            	}
	           
		    
	            }else player.sendMessage(ChatColor.RED +"You dont have permissions to run this command");
		     }
		    }
		}
	 
	 if (!(sender instanceof Player))
			{
		 
			}
			return false;
	 }


	private void showplayerhelp(Player player) {
		player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " [GoreaUtilities] "+ ChatColor.YELLOW + ".......................................................");
     	player.sendMessage( ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
     	player.sendMessage( ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
     	player.sendMessage( ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
     	player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
     	
     	player.sendMessage("");
     	player.sendMessage( ChatColor.YELLOW + "Aliases: " + ChatColor.LIGHT_PURPLE +  aliases );
     	player.sendMessage( ChatColor.YELLOW + "/gu ?/help :" + ChatColor.RESET + " Shows this.");
     	player.sendMessage( ChatColor.YELLOW + "/gu reload :" + ChatColor.RESET + " Reloads Configs.");
     	player.sendMessage( ChatColor.YELLOW + "/gu <players/P> <locations/L>" + ChatColor.RESET + " Will generate a file with all players locations" );
     	player.sendMessage( ChatColor.YELLOW + "/gtp:" + ChatColor.RESET + " Will teleport you to that player last location, even if he is offline." );
     	player.sendMessage( ChatColor.YELLOW + "/gtphere <Player_name>" + ChatColor.RESET + " Will teleport that player to you , even if he is offline." );     	
     	player.sendMessage( ChatColor.YELLOW + "/vote" + ChatColor.RESET + " Shows all voting websites." );
     	player.sendMessage( ChatColor.YELLOW + "/poll" + ChatColor.RESET + " Opens a poll for all players to vote about something." );


		
	}
	 
	 
}
