# iso-game-engine
A simple isometric game engine.

## Dependencies

This application requires Java 8 u40 or later.

## Building and running

Install Apache buildr and type buildr run.

The first time you run the map editor, you will be asked to browse to the data
directory.  This is the directory in which you intend to store the game files
as you edit them.

If the data directory is missing any necessary files or directories, the map
editor should create them.  At present it just crashes.

## Keys
* Q: Rotate camera left
* E: Rotate camera right
* Arrow keys: scroll

## Notes

Every texture, sprite, and cliff texture has a name, which is set when you add
a new texture, sprite, or cliff texture.  By default, a random name is
generated, but you have the option of choosing something friendly.  The only
condition is that no two objects of the same class (i.e. no two textures,
sprites, or cliff textures) can have the same name.

