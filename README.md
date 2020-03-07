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
  ranges:
    "small":          # Define RTP range called 'small'
      min: 1000     # Set the minimium teleport radius for this range
      max: 10000    # Set the maximium teleport radius for this range
    "medium":         # Define another RTP range called 'medium'
      min: 10000
      max: 20000
    "super.special&R@nge@me"
      min: 20000
      max: 30000
```

### Permission Nodes
- `pcn.staff` : Access to `/pcn-reload`

### Commands
- `/nv` : Toggles Night Vision
- `/rtp [rtp range name]` : Random teleport within the bounds of that teleport range
- `/pcn-reload` : Reload random teleport ranges from config file
