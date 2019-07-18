# hsrails
### high speed rails

A spigot plugin to make rails great again.

Place a powered rail on a redstone block to build high speed rail.
Default speed is 4x regular powered rails, ie. 32 m/s, or 115 km/h. This is as fast as rocket powered elytra flight.

The high speed rail multiplier can be changed with the `/hsrails` command.

![scrot](https://github.com/ergor/hsrails/blob/master/scrots/scrot.png)

## Usage
Place a powered rail on a redstone block to make it a high speed rail. Place on any other block to get regular powered rail. When traveling at high speeds, a regular powered rail will quickly slow the cart down towards the stock 8 m/s speed. I recommend building your tracks with regular power rails before corners to prevent derailing, and then accelerate out of the turn with high speed rails.

Use `/hsrails <multiplier>` to tweak how fast high speed rails are. Multiplier must be between 0 and 50.

## Compiling
`mvn package`
