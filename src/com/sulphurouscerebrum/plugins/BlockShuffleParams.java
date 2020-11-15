package com.sulphurouscerebrum.plugins;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class BlockShuffleParams {
    private BukkitTask task;
    private int currentRound;
    private int noOfRounds;
    private int roundTime;
    private int currentRoundTime;
    private int initialFoodAmount;
    private boolean isGameRunning;
    private List<Material> availableBlocks;
    private List<BlockShufflePlayer> availablePlayers;

    public BlockShuffleParams(){
    	this.currentRound 		= 0;
    	this.noOfRounds 		= 5;
    	this.roundTime 			= 6000;
    	this.currentRoundTime 	= 0;
    	this.initialFoodAmount 	= 16;
    	this.isGameRunning 		= false;
    	this.availablePlayers 	= new ArrayList<>();
    }
    
    public int getCurrentRound() {
    	return this.currentRound;
    }
    
    public void setCurrentRound(int currentRound) {
    	this.currentRound = currentRound;
    }
    
    public int getNoOfRounds() {
        return this.noOfRounds;
    }

    public void setNoOfRounds(int noOfRounds){
        this.noOfRounds = noOfRounds;
    }

    public int getRoundTime(){
        return this.roundTime;
    }

    public void setRoundTime(int roundTime){
        this.roundTime = roundTime;
    }
    
    public int getCurrentRoundTime(){
        return this.currentRoundTime;
    }

    public void setCurrentRoundTime(int currentRoundTime){
        this.currentRoundTime = currentRoundTime;
    }
    
    public void increaseCurrentRoundTime(int increment) {
    	this.currentRoundTime += increment;
    }

    public int getInitialFoodAmount(){
        return this.initialFoodAmount;
    }

    public void setInitialFoodAmount(int initialFoodAmount){
        this.initialFoodAmount = initialFoodAmount;
    }

    public List<Material> getAvailableBlocks(){
        return this.availableBlocks;
    }

    public void setAvailableBlocks(List<Material> availableBlocks){
        this.availableBlocks = availableBlocks;
    }

    public void setTask(BukkitTask task){
        this.task = task;
    }

    public BukkitTask getTask(){
        return this.task;
    }

    public boolean getIsGameRunning(){
        return this.isGameRunning;
    }

    public void setGameRunning(boolean isGameRunning){
        this.isGameRunning = isGameRunning;
    }

    public List<BlockShufflePlayer> getAvailablePlayers(){
        return this.availablePlayers;
    }

    public boolean addAvailablePlayer(String playerString) {
        for(BlockShufflePlayer player : availablePlayers){
            if(player.getName().equalsIgnoreCase(playerString)) {
                return false;
            }
        }
        BlockShufflePlayer player = new BlockShufflePlayer(Bukkit.getPlayer(playerString));
        availablePlayers.add(player);
        return true;
    }

    public boolean removeAvailablePlayer(String playerString){
        int indexToBeRemoved = -1;
        for(BlockShufflePlayer player : availablePlayers) {
            if(player.getName().equalsIgnoreCase(playerString)) {
                indexToBeRemoved = availablePlayers.indexOf(player);
                break;
            }
        }

        if(indexToBeRemoved >= 0) {
            availablePlayers.remove(indexToBeRemoved);
            return true;
        }
        else return false;
    }
    
    public BlockShufflePlayer getPlayer(String playerString) {
    	for(BlockShufflePlayer player : availablePlayers){
            if(player.getName().equalsIgnoreCase(playerString)) {
                return player;
            }
        }
    	
    	return null;
    }
}

