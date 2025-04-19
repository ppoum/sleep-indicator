# Sleep Indicator
This mod displays a boss bar whenever players are trying to sleep to skip the night. This boss bar takes into account the `playersSleepingPercentage` gamerule to properly display the required number of players needed to skip the night.
The bar is only displayed while players are sleeping and is hidden whenever 0 players are sleeping.

## AfkPlus integration
This mod integrates with [AfkPlus](https://modrinth.com/mod/afkplus) to take into account AFK players and the `bypassSleepCount` configuration. If a player is AFK, then this player will not be considered when calculating the required number of players to skip the night.
