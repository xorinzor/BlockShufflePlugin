package com.xorinzor.blockshuffle;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockShufflePlayer {
    private Player player;
    private int score;
    private Material blockToBeFound;
    private boolean hasFoundBlock;
    private boolean lost;
    private int roundsSurvived;

    public BlockShufflePlayer(Player player){
        this.player 		= player;
        this.score 			= 0;
        this.blockToBeFound = null;
        this.hasFoundBlock 	= false;
        this.lost 			= false;
        this.roundsSurvived = 0;
    }

    public String getName(){
        return this.player.getName();
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public boolean getHasFoundBlock(){
        return this.hasFoundBlock;
    }

    public void setHasFoundBlock(boolean hasFoundBlock){
        this.hasFoundBlock = hasFoundBlock;
    }

    public Material getBlockToBeFound(){
        return blockToBeFound;
    }

    public void setBlockToBeFound(Material blockToBeFound) {
        this.blockToBeFound = blockToBeFound;
    }
    
    public void defeated() {
    	this.lost = true;
    }
    
    public boolean hasLost() {
    	return this.lost;
    }
    
    public int getRoundsSurvived() {
    	return this.roundsSurvived;
    }
    
    public void incrementRoundsSurvived() {
    	this.roundsSurvived += 1;
    }
    
    public Player getPlayer() {
    	return this.player;
    }
}
