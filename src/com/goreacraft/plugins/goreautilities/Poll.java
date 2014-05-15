package com.goreacraft.plugins.goreautilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;




public class Poll implements CommandExecutor {
	

	@SuppressWarnings("unused")
	private Main plugin;
	
	static List<String> polls= new ArrayList<String>();
	static List<String> time= new ArrayList<String>();
	static List<String> weather= new ArrayList<String>();
	//static boolean ispollactive = false;
	//HashMap<String,Byte> voted = new HashMap<String,Byte>();
	static HashMap<Integer, String[]> pollsTasks= new HashMap<Integer, String[]>();
	static HashMap<String,Long> Timers = new HashMap<String,Long>();
	static  List<String> yes= new ArrayList<String>();
	static List<String> no= new ArrayList<String>();
	
	//public static int yes=0;
	//public static int no=0;
	
	
	

	public Poll(Main plugin){
		this.plugin = plugin;
			polls.add("time");
			polls.add("weather");
			
			time.add("day");
			time.add("night");
			
			weather.add("sun");
			weather.add("rain");
	
	
	
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player)
	    {
			Player player = (Player) sender;
			if(args.length>=1)
			{
				if(args[0].equalsIgnoreCase("stop") )
				{
					if(!pollsTasks.isEmpty())
					{
						//if(Metode.findPlayerByString(yes.get(0))
						Player caster = Metode.findPlayerByString(yes.get(0));
						
						if(yes.get(0).equals(player.getName()) || sender.isOp())
						{
							yes.clear();
							no.clear();
							int taskId = pollsTasks.keySet().iterator().next();
							pollsTasks.clear();							
							Bukkit.getScheduler().cancelTask(taskId);
							player.sendMessage(ChatColor.YELLOW + "You forcefully stopped the current poll.");
							
							
							if(caster!=null && !caster.getName().equals(sender.getName())) caster.sendMessage(ChatColor.RED + "Your poll has been stopped by: "+ ChatColor.YELLOW + sender.getName());
							return true;
						} else {
							player.sendMessage("You dont have permissions to stop other players polls.");
							//player.sendMessage(Metode.findPlayerByString(yes.get(0)).getName());
							return true;
						}
						
						
						
					} else {
						player.sendMessage("There is no poll running at this momment.");
						return true;
					}
					//
					
				} else
				
				if(args[0].equalsIgnoreCase("reload") )
				{ 
					if(player.isOp() || player.hasPermission("gorea.poll.reload"))
					{
						yes.clear();
						no.clear();
						int taskId = pollsTasks.keySet().iterator().next();
						pollsTasks.clear();						
						Bukkit.getScheduler().cancelTask(taskId);
						Main.reloadconfigs();
						player.sendMessage("Poll plugin reloaded");
						return true;
					} else player.sendMessage(ChatColor.RED +"You need the permission gorea.poll.reload to run this command");
				}else
				
				if(args[0].equalsIgnoreCase("debug"))
				{
					if(player.isOp() || player.hasPermission("gorea.poll.reload"))
					{
						Main.polldebug.set("Timers", Timers);
						Main.polldebug.set("pollsTasks", pollsTasks);
						Main.polldebug.set("Voters yes:", yes);
						Main.polldebug.set("Voters no:", no);
						try {
							Main.polldebug.save(Main.polloutput);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else player.sendMessage(ChatColor.RED +"You need the permission gorea.poll.debug to run this command");
				} else
				
				if(args[0].equalsIgnoreCase("yes"))
				{
					if (!pollsTasks.isEmpty())
					{
						if(!no.contains(player.getName())  && !yes.contains(player.getName()))
						{
							player.sendMessage("You voted "+ ChatColor.GREEN+  "Yes" + ChatColor.RESET+" for current poll!");
							//String caster = pollsTasks.keySet().iterator().next().toString();
							
							//player.sendMessage(""+yes.get(0));
							if(Metode.findPlayerByString(yes.get(0))!=null) Metode.findPlayerByString(yes.get(0)).sendMessage(ChatColor.YELLOW + player.getName() +ChatColor.RESET+ ": "+ ChatColor.GREEN + "Yes");
							yes.add(player.getName());
						//voted.put(player.getName(),(byte) 1);
						} else player.sendMessage(ChatColor.RED + "You voted already for this poll!");
					} else player.sendMessage(ChatColor.RED + "There is no poll active!");
					return true;
				}else 
				if(args[0].equalsIgnoreCase("no"))
				{
					if (!pollsTasks.isEmpty())
					{
						if(!no.contains(player.getName())  && !yes.contains(player.getName()))
						{
							player.sendMessage("You voted "+ ChatColor.RED+  "No" + ChatColor.RESET+" for current poll!");
							//String caster = pollsTasks.keySet().iterator().next().toString();
							
							
							//player.sendMessage(""+yes.get(0));
							if(Metode.findPlayerByString(yes.get(0))!=null) Metode.findPlayerByString(yes.get(0)).sendMessage(ChatColor.YELLOW + player.getName() +ChatColor.RESET+ ": "+ ChatColor.RED + "No");
							no.add(player.getName());
							//voted.put(player.getName(),(byte) 0);
							
						} else player.sendMessage(ChatColor.RED + "You voted already for this poll!");
					} else player.sendMessage(ChatColor.RED + "There is no poll active!");
					return true;
				} else
				
				if(args[0].equalsIgnoreCase("time"))
				{
					//player.sendMessage("time www");
					if(args.length>=2)
					{
						if(player.isOp() || player.hasPermission("gorea.poll.time"))
						{
						
							if (args[1].equalsIgnoreCase("day") || args[1].equalsIgnoreCase("night"))
							{
								boolean go=true;
								if(Timers.keySet().contains(player.getName()) && !player.hasPermission("gorea.poll.time.cooldown"))
								{
									if(!(Timers.get(player.getName())<((Calendar.getInstance().getTimeInMillis() / 1000) - Main.poolcooldown)))
									{
										go=false;
									} 
								}
								if( pollsTasks.isEmpty())
									{
									if(go)
										{
									
										Timers.put(player.getName(), Calendar.getInstance().getTimeInMillis() / 1000);
										yes.add(player.getName());
										//==========================================================================\\
										startPoll(player.getName(),player.getWorld().getName(),args[0], args[1]);
										//player.sendMessage("Starting time poll");
										return true;
									
										} else {
											player.sendMessage("You have to wait " + ChatColor.YELLOW + Math.round((Main.poolcooldown - ((Calendar.getInstance().getTimeInMillis() / 1000)-Timers.get(player.getName())))) + ChatColor.RESET + " seconds before starting a new poll." );
								
											return true;
										}
								} else {
										player.sendMessage(ChatColor.RED + "There is a poll running already");
										return true;
										}
							}
						
						}else player.sendMessage(ChatColor.RED +"You need the permission gorea.poll.time to run this command");
						
					}
					player.sendMessage(ChatColor.YELLOW +"/poll time " + time);
					return true;
					
				} else
				if(args[0].equalsIgnoreCase("weather"))
					{
							//player.sendMessage("time www");
					if(args.length>=2)
						{
						if(player.isOp() || player.hasPermission("gorea.poll.weather"))
						{
							if (args[1].equalsIgnoreCase("sun") || args[1].equalsIgnoreCase("rain") || args[1].equalsIgnoreCase("storm"))
								{
								boolean go=true;
								if(Timers.keySet().contains(player.getName()) && !player.hasPermission("gorea.poll.weather.cooldown"))
								{
									if(!(Timers.get(player.getName())<((Calendar.getInstance().getTimeInMillis() / 1000) - Main.poolcooldown)))
									{
										go=false;
									} 
								}
								if( pollsTasks.isEmpty())
									{
									if(go)
										{
									
										Timers.put(player.getName(), Calendar.getInstance().getTimeInMillis() / 1000);
										yes.add(player.getName());
										//==========================================================================\\
										startPoll(player.getName(),player.getWorld().getName(),args[0], args[1]);
									//	player.sendMessage("Starting time poll");
										return true;
									
										} else {
											player.sendMessage("You have to wait " + ChatColor.YELLOW + Math.round((Main.poolcooldown - ((Calendar.getInstance().getTimeInMillis() / 1000)-Timers.get(player.getName())))) + ChatColor.RESET + " seconds before starting a new poll." );
								
											return true;
										}
									} else {
										player.sendMessage(ChatColor.RED + "There is a poll running already.");
										return true;
										}
								}
						}else player.sendMessage(ChatColor.RED +"You need the permission gorea.poll.weather to run this command");
						
						}
					player.sendMessage(ChatColor.YELLOW +"/poll weather " + weather);
					return true;
					} else
					if(args[0].equalsIgnoreCase("create"))
						{
						
						if(player.isOp() || player.hasPermission("gorea.poll.create"))
						{	//custom polls
							if(args.length>=2)
							{	
									
									boolean go=true;
									if(Timers.keySet().contains(player.getName())  && !player.hasPermission("gorea.poll.create.cooldown"))
									{
										if(!(Timers.get(player.getName())<((Calendar.getInstance().getTimeInMillis() / 1000) - Main.poolcooldown)))
										{
											go=false;
										} 
									}
									if( pollsTasks.isEmpty())
										{
										if(go)
											{
										
											Timers.put(player.getName(), Calendar.getInstance().getTimeInMillis() / 1000);
											yes.add(player.getName());
											String message="";
											
											for(int i=1;i< args.length;i++)
												{
												message=message.concat(" "+args[i]);									
												}
											//	player.sendMessage("message: " + message);
											//==========================================================================\\
											startPoll(player.getName(),player.getWorld().getName(),args[0], message);
										//	player.sendMessage("Starting time poll");
											return true;
										
											} else {
												player.sendMessage("You have to wait " + ChatColor.YELLOW +  Math.round((Main.poolcooldown -((Calendar.getInstance().getTimeInMillis() / 1000)-Timers.get(player.getName())))) + ChatColor.RESET + " seconds before starting a new poll." );
									
												return true;
											}
									} else {
											player.sendMessage(ChatColor.RED + "There is a poll running already");
											return true;
											}
								}
						} else { 
							//player.sendMessage(ChatColor.RED + "You dont have permissions to create cutom polls");
							player.sendMessage(ChatColor.RED + Main.custompollsmessage);
							return true;}
						player.sendMessage(ChatColor.YELLOW +"/poll create <Text>");
						return true;
						
						}

			}
			
			//show help for poll
			showplayerhelp(player);
			//player.sendMessage(ChatColor.YELLOW + "[GoreaUtilities] " + ChatColor.RESET + ": /poll [yes/no/weather/time/create]");
			return true;
			
	    }
		return false;
	}
	
	private void showplayerhelp(Player player) {
		player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " Plugin made by: "+ ChatColor.YELLOW + ".......................................................");
     	player.sendMessage( ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
     	player.sendMessage( ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
     	player.sendMessage( ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
     	player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
     	
     	player.sendMessage("");
     	player.sendMessage( ChatColor.YELLOW + "/poll [yes/no] :" + ChatColor.RESET + " Cast your vote for current poll");
     	player.sendMessage( ChatColor.YELLOW + "/poll weather [sun/rain/storm] :" + ChatColor.RESET + " Start a poll to change weather in your dimension");
     	player.sendMessage( ChatColor.YELLOW + "/poll time [day/night] :" + ChatColor.RESET + " Start a poll to change time in your dimension");
     	player.sendMessage( ChatColor.YELLOW + "/poll create [Text] :" + ChatColor.RESET + " Start a poll with custom message.");
     	player.sendMessage( ChatColor.YELLOW + "/poll stop:" + ChatColor.RESET + " Stops the current poll.");
     	player.sendMessage( ChatColor.YELLOW + "/poll reload:" + ChatColor.RESET + " Resets all polls.");

		
	}
	public void startPoll(String name, String world, String args, String args2){
		for(Player player: Bukkit.getServer().getOnlinePlayers())
		{
			
			if(args.equalsIgnoreCase("create"))
					{
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC +"Voting poll has been started by ");
				player.sendMessage(ChatColor.YELLOW + name + ": " + ChatColor.DARK_PURPLE + args2);
					} else
					player.sendMessage("Player: "+ ChatColor.YELLOW + name + ChatColor.RESET + " started a poll to change "+ChatColor.YELLOW + args + ChatColor.RESET + " in world: " + ChatColor.YELLOW + world + ChatColor.RESET + " to " + ChatColor.YELLOW + args2 + ChatColor.RESET + "." );
			player.sendMessage("Type " + ChatColor.YELLOW + "/poll yes" + ChatColor.RESET + " or " + ChatColor.YELLOW + "/poll no" + ChatColor.RESET +" to cast your vote.");	
		}
		
		int taskid = new BukkitRunnable() 
		{
			@Override
					public void run() 
			{	
				
				if(Poll.yes.size()>Poll.no.size())
				{
					long time =0;
					//boolean weather = false;
					String[] aaa = pollsTasks.get(getTaskId());
					
					if(aaa[2].equalsIgnoreCase("time"))
					{
						if(aaa[3].equalsIgnoreCase("night"))
							time = 13000;
						
					Bukkit.getServer().getWorld(aaa[1]).setTime(time);
					}
					else
					if(aaa[2].equals("weather"))
					{
						if(aaa[3].equalsIgnoreCase("sun"))
							Bukkit.getServer().getWorld(aaa[1]).setStorm(false);
						
						if(aaa[3].equalsIgnoreCase("rain"))
							Bukkit.getServer().getWorld(aaa[1]).setStorm(true);	
						
						if(aaa[3].equalsIgnoreCase("storm"))
							Bukkit.getServer().getWorld(aaa[1]).setThundering(true);
					}
					else
					if(aaa[2].equals("create"))
					{
						
						
					}
					
					for(Player player: Bukkit.getServer().getOnlinePlayers())
					{
						player.sendMessage(ChatColor.GREEN +"The poll was succesfull with results: " );								
					}
					
					
				} else {
					for(Player player: Bukkit.getServer().getOnlinePlayers())
					{
						player.sendMessage(ChatColor.RED + "The poll failed with results: " );								
					}	
					//poll failed
				}
				for(Player player: Bukkit.getServer().getOnlinePlayers())
				{
				player.sendMessage(ChatColor.GREEN + "Yes: " + ChatColor.RESET+ yes.size());
				if(Main.voteplayernames)player.sendMessage(""+ChatColor.GRAY +ChatColor.ITALIC + yes);
				player.sendMessage(ChatColor.RED + "No: "+ ChatColor.RESET + no.size() );
				if(Main.voteplayernames&& no.size()>0)player.sendMessage(""+ChatColor.GRAY +ChatColor.ITALIC + no);
				player.sendMessage(ChatColor.GRAY+"" + ChatColor.ITALIC+ "Refrained: " + (Bukkit.getServer().getOnlinePlayers().length - yes.size()- no.size()));
				}
				yes.clear();
				no.clear();
				pollsTasks.remove(getTaskId());
			}
		}.runTaskLater(Main.plugin, Main.poolduration).getTaskId();
	
		String[] ddd = {name, world, args, args2};
		pollsTasks.put(taskid, ddd);
		
		
	}

}
