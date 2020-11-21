package com.xorinzor.blockshuffle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {

    BlockShuffleParams params;

    public void onEnable(){
    	super.onEnable();
    	
        Objects.requireNonNull(getCommand("blockshuffle")).setExecutor(new BlockShuffleCommands(this));
        Objects.requireNonNull(getCommand("blockshuffle")).setTabCompleter(new BlockShuffleTabCompleter(this));

        saveDefaultConfig();
        params = new BlockShuffleParams();

        loadConfigFile();
        
        if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_blocktofind", new BlockShufflePlaceholderReplacer(params) {        		
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			if(this.params.getIsGameRunning() == false) {
        				return "Wait for game to start";
        			}
        			
    				Player p = event.getPlayer();
    				
    				if(p == null) {
    					return "invalid player";
    				}
    				
    				BlockShufflePlayer bp = this.params.getPlayer(p.getName());
    				
    				if(bp == null) {
    					return "unknown user";
    				}
    				
    				if(bp.hasLost()) {
    					return "game over";
    				}
    				
    				if(bp.getBlockToBeFound() == null) {
    					return "not assigned";
    				} else {
    					return bp.getBlockToBeFound().name();
        			}
        		}
        	});
        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_foundblock", new BlockShufflePlaceholderReplacer(params) {
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			Player p = event.getPlayer();
    				
    				if(p == null) {
    					return "invalid player";
    				}
    				
    				BlockShufflePlayer bp = this.params.getPlayer(p.getName());
    				
    				if(bp == null) {
    					return "unknown user";
    				}
    				
    				if(bp.getHasFoundBlock()) {
    					return "true";
    				}
    				
					return "false";
        		}
        	});
        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_currentround", new BlockShufflePlaceholderReplacer(params) {
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			if(this.params.getIsGameRunning() == false) {
        				return "Waiting";
        			}
        			
        			return "Round " + this.params.getCurrentRound();
        		}
        	});
        	        	        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_timeremaining", new BlockShufflePlaceholderReplacer(params) {        		
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			if(this.params.getIsGameRunning() == false) {
        				return "0:00";
        			}
        			
        			int ticksLeft = this.params.getRoundTime() - this.params.getCurrentRoundTime();
        			
        			if(ticksLeft < 20) {
        				return "0:00";
        			}
        			
        			int time = (int) Math.round(Math.ceil(ticksLeft / 20));
        			
        			int minutes = time / 60;
        			int seconds = time % 60;

        			return String.format("%02d:%02d", minutes, seconds);
        		}
        	});
        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_gamerunning", new BlockShufflePlaceholderReplacer(params) {        		
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			return (this.params.getIsGameRunning()) ? "true" : "false";
        		}
        	});
        }
    }

    public void onDisable(){
    }

    public void loadConfigFile() {
        List<String> configBlocks;
        List<Material> validBlocks = new ArrayList<>();

        configBlocks = getConfig().getStringList("block-list");

        for(String block : configBlocks) {
            if(checkMaterialValidity(block)) {
                validBlocks.add(Material.getMaterial(block));
                Bukkit.getLogger().info("Loaded " + block);
            }
            else {
                Bukkit.getLogger().info("Material " + block + " is not valid. Skipping");
            }
        }

        if(validBlocks.isEmpty())
            Bukkit.getLogger().info("No blocks were added from the config.yml file. Game cannot start");

        this.params.setAvailableBlocks(validBlocks);
        Bukkit.getLogger().info("Total of " + validBlocks.size() + " blocks were added");

        int roundTime = getConfig().getInt("parameters.roundTime");

        if(roundTime < 200) {
            Bukkit.getLogger().info("Round time cannot be less than 10 seconds. Defaulting to 1 minute");
            roundTime = 1200;
        }

        this.params.setRoundTime(roundTime);
        Bukkit.getLogger().info("Round time : " + roundTime);

        int foodToBeGiven = getConfig().getInt("parameters.giveFood");
        if(foodToBeGiven < 0) {
            Bukkit.getLogger().info("Invalid food amount. Defaulting to 16");
            foodToBeGiven = 16;
        }

        if(foodToBeGiven > 2304) foodToBeGiven = 2304;
        this.params.setInitialFoodAmount(foodToBeGiven);
        Bukkit.getLogger().info("Amount of food to be given : " + foodToBeGiven);
    }

    public boolean checkMaterialValidity(String material) {
        Material m = Material.getMaterial(material);
        return m != null;
    }
}
