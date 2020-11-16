# BlockShufflePlugin
Modified version of the BlockShufflePlugin by SulphurousCerebrum.

Inspired by [Dream's](https://www.youtube.com/user/DreamTraps) YouTube [video](https://www.youtube.com/watch?v=p34C7fNFgTA). When the game starts, the inventory of each player is cleared, and they are given a fixed amount of food to start off (Can be set to 0 in the config file). A random block is assigned to each player, if they dont find the block before the round ends, they lose. This continues until only 1 player is left.

## Available Commands
Every command starts with `/blockshuffle` with different parameters.

-   `/blockshuffle start` - Starts the game
-   `/blockshuffle stop` - Stops the game
-   `/blockshuffle info` - Shows the current game settings like total rounds, round time etc.
-   `/blockshuffle add [playerName]` - Adds a player to the player list
-   `/blockshuffle remove [playerName]` - Removes a player from the player list
-   `/blockshuffle list` - Returns the number of players currently in the list
-   `/blockshuffle set roundTime [round_time]` - Sets the round time in **ticks** (20 ticks is 1 second)
-   `/blockshuffle set foodAmount [amount_of_food_to_be_given]` - Sets the initial amount of food to be given to each player. (Set to 0 to start with a clear inventory)

## How it works
Plugin reads the available blocks from [config.yml](https://github.com/xorinzor/BlockShufflePlugin/blob/main/config.yml) and assigns a random block to each player. A check is done using BukkitScheduler which does a check every 10 ticks (half a second) 

