### PCNEssentials
Custom utility and gameplay commands developed by the PeacefulCraft network.

This plugin is avaiable to download in the releases tab and you're welcome to use it on your sever, or fork this repository and make your own changes.

### Configuration Structure
RTP ranges can be named whatever you wish, so long as they do not contain spaces. We recomend using quotes around the names of the RTP range to avoid ambiguity and confusing YAMLerrors. The range minimiums and maximiums can overlap.
```YAML
nv:
  enabled: true     # Enable NV Command

rtp:
  enabled: true     # Enable RTP Command
  resistance_duration: 10 # seconds for resistence effect to last after teleport
  allowed_worlds:  # List of worlds users are allowed to /rtp to
  - world
  - peaceful
  ranges:
    small:          # Define RTP range called 'small'
      min: 1000     # Set the minimium teleport radius for this range
      max: 10000    # Set the maximium teleport radius for this range
    medium:         # Define another RTP range called 'medium'
      min: 10000
      max: 20000
    large:
      min: 20000
      max: 30000

random:
  enabled: false

# Enables / Disables the /crusade <player> command: Bombards the foe with a powerful crusade.
crusade:
  enabled: true

# Don't worry about this one
competition:
  enable: false
  name: "Turkeys Killed"

# /hug command
hug:
  enabled: true
  heart_count: 15
  cooldown: 15 # seconds
```

### Permission Nodes
- `pcn.staff` : Access to `/pcn-reload` Players with this permission will also receive update notifications. The permission is automatically given to `/op`d players.

### Commands
- `/nv` : Toggles Night Vision
- `/rtp [rtp range name]` : Random teleport within the bounds of that teleport range
- `/pcn-reload` : Reload random teleport ranges from config file
