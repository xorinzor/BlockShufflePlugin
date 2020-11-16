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
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_blockToFind", new BlockShufflePlaceholderReplacer(params) {        		
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
        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_foundBlock", new BlockShufflePlaceholderReplacer(params) {
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			if(event.isOnline()) {
        				return "offline";
        			}
    			
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
        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_score", new BlockShufflePlaceholderReplacer(params) {
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			if(event.isOnline()) {
        				return "offline";
        			}
    			
    				Player p = event.getPlayer();
    				
    				if(p == null) {
    					return "invalid player";
    				}
    				
    				BlockShufflePlayer bp = this.params.getPlayer(p.getName());
    				
    				if(bp == null) {
    					return "unknown user";
    				}
    				
    				return "" + bp.getScore();
        		}
        	});
        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_currentRound", new BlockShufflePlaceholderReplacer(params) {
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			return "" + this.params.getCurrentRound();
        		}
        	});
        	        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_totalRoundTime", new BlockShufflePlaceholderReplacer(params) {
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			return "" + this.params.getRoundTime();
        		}
        	});
        	
        	PlaceholderAPI.registerPlaceholder(this, "blockshuffle_currentRoundTime", new BlockShufflePlaceholderReplacer(params) {        		
        		@Override
        		public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        			return "" + this.params.getCurrentRoundTime();
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
