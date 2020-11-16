package com.xorinzor.blockshuffle;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockShuffleAssign {
	
	Main plugin;
	
	public BlockShuffleAssign(Main plugin) {
		this.plugin = plugin;
	}
	
	public void assignBlocks() {
		for(BlockShufflePlayer player : plugin.params.getAvailablePlayers()) {
			player.setHasFoundBlock(false);
			Material assignedBlock = getRandomBlock();
			player.setBlockToBeFound(assignedBlock);
			Player play = Bukkit.getPlayer(player.getName());
			play.sendMessage("Your block is : " + assignedBlock.name());
		}
	}
	
	public Material getRandomBlock() {
		Material assignedBlock = null;
		Random rand = new Random();
		
		//Generate random number and get it from list
		while(assignedBlock == null) {
			int randomNumber = rand.nextInt(plugin.params.getAvailableBlocks().size());
			Material m = plugin.params.getAvailableBlocks().get(randomNumber);
			assignedBlock = m;
		}
		return assignedBlock;
	}
}
