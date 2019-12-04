# hsrails
## High Speed Rails

A spigot/bukkit plugin to make minecarts viable again.

Place a powered rail on a redstone block to build high-speed rail.
High-speed rails are by default 4x faster than regular powered rails, ie. 32 m/s, or 115 km/h. This is as fast as rocket powered elytra flight.

The high-speed rail multiplier can be temporarily changed with the `/hsrails` command, or permanently changed in the config.

**Note**: please read the _Design_ sections on how to use this plugin effectively.

![scrot](https://github.com/ergor/hsrails/blob/master/img/scrot.png)

### Usage
Place a powered rail on a _redstone block_ to make it a _high-speed rail_. Place on any other block to get a regular powered rail.

Use `/hsrails <multiplier>` to tweak how fast high-speed rails are.
For permanent change, edit the config file `config.yml` in `plugins/HsRails/`.
Multiplier must be between 0 and 50.

#### Design considerations

You must be aware of a couple of things while building high-speed tracks:

- Acceleration is _not_ instantaneous.
- Entering turns at high speed _will_ derail you.
- Entering/exiting slopes at high speed _will_ derail you.
- While traveling at high speeds, a regular powered rail _will_ slow you down.

Derailing at high speeds is a limitation of the game itself, and is probably the reason why rails are so slow in vanilla Minecraft. Thus, when designing your high-speed tracks, you will have to design them like real high-speed train tracks: long stretches with smooth turns.

#### Design guidelines

These are my recommendations for building efficient high-speed tracks:

- Allow room for acceleration: it takes a while to reach top speed. Place several high-speed rail sections close together at the start of your track.
- Minimize number of turns: you only need at most one turn to get to any destination. An optimal track looks like an L from above.
- Minimize number of slopes: build tunnels, bridges, etc. to stay on the same level.
- Before turns and slopes, put one (or sometimes a couple of) regular powered rails to slow down and avoid derailment.
- After turns and slopes, allow room for acceleration again.

To maintain high speeds you must of course build your tracks out of high-speed rails, because regular powered rails will slow you down. Only mix in regular powered rails in turns and slopes as mentioned above.


### Compiling
This is a maven project made in intellij idea.
To compile, simply run `mvn package`
