# Tanks
Spin-off of classic _Wii Play Tanks!_ game

## Description
This game is based on Nintendo's _Wii Play Tanks!_ game. The player controls a blue tank and must traverse a small maze from the start to the end, while avoiding or defeating a number of enemy tanks along the way. The player can control their tank by using the WASD keys and control their barrel and shoot bullets with the mouse. Each tank (player and enemy tanks) has a certain number of "lives" above their tank, as shown below:

![alt text](https://github.com/dipeshmanandhar/Tanks/raw/master/pics/Sample%20Player.png)

If a tank loses all their lives (by getting hit by a bullet), they die and are removed from the game (for the player, this results in a game over). The game starts at the main menu, which includes a "mini game" of the full game, where the player is not restricted to a maze, as shown below:

![alt text](https://github.com/dipeshmanandhar/Tanks/raw/master/pics/Main%20Menu.png "Main Menu")

After clicking "Play", the game starts and the player is thrown into a maze, as shown below:

![alt text](https://github.com/dipeshmanandhar/Tanks/raw/master/pics/Gameplay.png "Gameplay")

## Maze Generation Algorithm
The maze was created using a randomized version of Prim's Algorithm, the same one as used in my Empty Life Project (https://github.com/dipeshmanandhar/Empty_Life).

## Pathfinding AI
The pathfinding algorithm used by the enemy AI's was the A* (A-star) Algorithm, which is a modification of Dijkstra's Shortest-Path Algorithm using a greedy heuristic to guide the search for a shortest path from a start node to a destination node. This algorithm was chosen over Dijkstra's because the worst-case runtime of A* is _&Theta; (|E|)_, which is better than that of Dijkstra's, _&Theta; (|E| log |V|)_. For more information on the A* Algorithm, see the [Wikipedia page](https://en.wikipedia.org/wiki/A*_search_algorithm "A* Wikipedia page").

## MIDI Music Player
All sound effects and background music was created using a MIDI sound player. The background music plays a certain scale of piano notes at a speed based on the proximity of the player to the nearest enemy. That is, the closer the player is to an enemy, the faster the tempo of the music gets, to create a feeling of anxiety in the player.  

This project was completed by May of 2017.
