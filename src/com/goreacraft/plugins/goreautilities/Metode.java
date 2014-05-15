package com.goreacraft.plugins.goreautilities;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;

public class Metode {
	
	//private static HashMap<String,String> data= new HashMap<String,String>();
	//private static HashMap<String,Object> data2= new HashMap<String,Object>();
	
	public static void setPlayerLocation(String name, Location location) {
		
		//World world = Bukkit.getServer().getWorlds().get(0);
		//	try {
				//Location counter = location;
				NbtList<?> tag = NbtFactory.ofList("Pos",2,3,6 );
				//NbtCompound data = tag.getCompoundOrDefault("com.comphenix.example");
				 
				// Increment the count
				//data.put("count", data.getIntegerOrDefault("count") + 1);
				Metode.findPlayerByString("goreacraft").sendMessage("Current count: " + tag.getValue(1));

				//NbtBase<?> tag;
				//tag.setValue(7);
				//aaa.add(location.getX());
				//aaa.add(location.getY());
				//aaa.add(location.getZ());
				
				//NbtCompound aaa = NbtFactory.asCompound(tag);
				
				
				//NBT.saveFile(NBT.getNBTPlayerFile(world, name), aaa);
				
			//}catch (IOException e) {e.printStackTrace();}
		
	}

	 public static Location getPlayerLocation(String name)
	 {
		Location loc = null;
		World world = Bukkit.getServer().getWorlds().get(0);
			try {
				int pdim = NBT.loadFile(NBT.getNBTPlayerFile(world, name)).getInteger("Dimension");
				NbtList<Object> ploc = NBT.loadFile(NBT.getNBTPlayerFile(world, name)).getList("Pos");						
				Object x =  ploc.getValue(0);
				Object y = ploc.getValue(1);
				Object z =  ploc.getValue(2);
				loc =new Location(Main.plugin.getServer().getWorlds().get(pdim), (double) x,(double) y,(double) z);						
				} catch (IOException e) {e.printStackTrace();}					 
		return loc; 					 
	 }
	 
	 public static YamlConfiguration getPlayersDimensions()
	 {
		 YamlConfiguration pdatayml = new YamlConfiguration();
		 for(OfflinePlayer player:Main.plugin.getServer().getOfflinePlayers())
		 {			 
			 Object dimension;
			 NbtList<Object> ploc;
			 String name = player.getName();
			 World world = Bukkit.getServer().getWorlds().get(0);
			 try {
				dimension = NBT.loadFile(NBT.getNBTPlayerFile(world, name)).getObject("Dimension");
				ploc = NBT.loadFile(NBT.getNBTPlayerFile(world, name)).getList("Pos");
				int x =  (int) Math.abs((double) ploc.getValue(0));
				int y = (int) Math.abs((double) ploc.getValue(1));
				int z =  (int) Math.abs((double) ploc.getValue(2)); 
				String pworld= String.valueOf(dimension);

				pdatayml.set(pworld + "." + name, x + " " + y+ " " + z);
			} catch (IOException e) {e.printStackTrace();}
			 
		 }
		 return pdatayml;
	 }
					 

	static Player findPlayerByString(String name) 
		{
			for ( Player player : Bukkit.getServer().getOnlinePlayers())
			{
				if(player.getName().equals(name)) 
				{
					return player;
				}
			}
			
			return null;
		}
}
