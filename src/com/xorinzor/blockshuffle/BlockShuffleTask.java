package com.xorinzor.blockshuffle;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.xorinzor.blockshuffle.events.BlockShuffleGameFinishedEvent;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class BlockShuffleTask extends BukkitRunnable {
    Logger logger;
    boolean hasRoundEnded;
    Main plugin;
    BlockShuffleTaskHelper helper;
    int counter;
    SendTitle titleSender;

    public BlockShuffleTask(Main plugin){
        this.logger 			= Bukkit.getLogger();
        this.plugin 			= plugin;
        this.hasRoundEnded 		= true;
        this.counter 			= 100;
        this.titleSender 		= new SendTitle();
        this.helper 			= new BlockShuffleTaskHelper(this.plugin);
    }
    
    @Override
    public void run() {
    	//Countdown from 5 to 0
        if(counter > 0) {
            if(counter % 20 == 0) {
                for (BlockShufflePlayer player : this.plugin.params.getAvailablePlayers())
                    titleSender.sendTitle(player.getPlayer(), 5, 10, 5, ChatColor.BLUE + "Game Starting", ChatColor.RED + "" + (counter / 20));
            }

            counter -= 10;
        }
        //Game logic
        else {
            if (hasRoundEnded) {
            	this.plugin.params.setCurrentRound(this.plugin.params.getCurrentRound() + 1);
            	this.plugin.params.setCurrentRoundTime(0);
                Bukkit.broadcastMessage("Starting Round : " + ChatColor.BOLD + "" + this.plugin.params.getCurrentRound());
                this.hasRoundEnded = false;
                helper.startRound();
            } 
            else {
            	//Reset active player count
            	int activePlayers = 0;
            	
            	//Check all players
                for (BlockShufflePlayer player : this.plugin.params.getAvailablePlayers()) {
                	//Players who lost or already have their block don't count.
                    if (player.getHasFoundBlock() == false && player.hasLost() == false) {
                    	//Check if the player is standing on their block
                    	helper.checkPlayer(player);
                    	
                    	//If the player hasn't found their block yet, this counts as an active player
                    	if(!player.getHasFoundBlock()) {
                    		activePlayers++;
                    	}
                    }
                }

                //Get the remaining time of the round
                int timeRemaining = this.plugin.params.getRoundTime() - this.plugin.params.getCurrentRoundTime();

                //Check if everyone found their block or if the time is up
                if (activePlayers <= 0 || timeRemaining <= 0) {
                    this.hasRoundEnded = true;
                    activePlayers = 0;
                    
                    //Send the game over message to all remaining players
                    for(BlockShufflePlayer p : this.plugin.params.getAvailablePlayers()) {
                    	if(p.getHasFoundBlock() == false && p.hasLost() == false) {
                    		p.defeated();
                    		p.roundReset();
                    		p.getPlayer().setGameMode(GameMode.SPECTATOR);
                    		p.getPlayer().sendMessage(ChatColor.RED + "Game over - You didn't find your block in time!");
                    		p.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You are now a spectator, when the game finishes you can play again");
                    		continue;
                    	}
                    	
                    	activePlayers++;
                    }
                    
                    //If only 1 player remains, the game is over.
                    if(activePlayers > 1) {
                    	Bukkit.broadcastMessage(ChatColor.AQUA + "" + activePlayers + " players remain!");
                    } else {
                    	this.cancel();
                    }
                } 
                //Else check for the remaining time (countdown)
                else if (timeRemaining <= 200) {
                    if(timeRemaining % 20 == 0) {
                    	Bukkit.broadcastMessage(ChatColor.RED + "Time Remaining : " + ChatColor.BOLD + (timeRemaining / 20) + " seconds");
                    }
                    
                    this.plugin.params.increaseCurrentRoundTime(10);
                } 
                //Finally increase the round time
                else {
                	this.plugin.params.increaseCurrentRoundTime(10);
                }
            }
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        helper.endGame();
    }
}

class BlockShuffleTaskHelper {
    private Main plugin;
    private SendTitle titleSender;
    
    public BlockShuffleTaskHelper(Main plugin){
        this.plugin = plugin;
        this.titleSender = new SendTitle();
    }

    public void startRound(){
        for(BlockShufflePlayer player : this.plugin.params.getAvailablePlayers()){
        	if(player.hasLost()) {
        		continue;
        	}
        	
        	player.roundReset();
            player.setBlockToBeFound(getRandomBlock());
            Bukkit.getLogger().info("Assigned " + player.getName() + " with " + player.getBlockToBeFound());
        }
    }

    public Material getRandomBlock(){
        Random rand = new Random();
        int randomNumber = rand.nextInt(this.plugin.params.getAvailableBlocks().size());
        return this.plugin.params.getAvailableBlocks().get(randomNumber);
    }

    public boolean checkPlayer(BlockShufflePlayer player) {
    	if(player.getPlayer().isOnline() == false) {
    		return false;
    	}
    	
        Material standingOn = player.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        
        if(standingOn.equals(player.getBlockToBeFound())) {
            player.setHasFoundBlock(true);
            player.incrementRoundsSurvived();
            broadcastSound(Sound.BLOCK_END_PORTAL_SPAWN);
            Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.GOLD + player.getPlayer().getDisplayName() + " has found their block!");
            return true;
        }

        return false;
    }

    public void broadcastSound(Sound sound) {
        for(BlockShufflePlayer player : this.plugin.params.getAvailablePlayers()){
            player.getPlayer().playSound(player.getPlayer().getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public void endGame(){
        this.plugin.params.setGameRunning(false);
        broadcastSound(Sound.UI_TOAST_CHALLENGE_COMPLETE);
        
        TreeMap<Integer, String> scores = new TreeMap<>(Collections.reverseOrder());

        for(BlockShufflePlayer player : this.plugin.params.getAvailablePlayers()) {
            Player p = Bukkit.getPlayer(player.getName());
            scores.put(player.getRoundsSurvived(), player.getName());
            titleSender.sendTitle(p, 10, 30, 10, ChatColor.RED + "" + "Game Over", "");
            player.fullReset();
            p.setGameMode(GameMode.SURVIVAL);
        }

        Bukkit.broadcastMessage(ChatColor.GOLD + "===== highscore =====");
        
        Iterator<Entry<Integer, String>> iterator = scores.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            Bukkit.broadcastMessage(ChatColor.BOLD.toString() + mapEntry.getValue() + ChatColor.RESET + ": " + mapEntry.getKey());
        }
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "=====================");
        
        BlockShuffleGameFinishedEvent event = new BlockShuffleGameFinishedEvent();
        Bukkit.getPluginManager().callEvent(event);
    }
}
