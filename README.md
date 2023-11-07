# Thin Air

A Minecraft mod. Downloads can be found on [CurseForge](https://www.curseforge.com/members/fuzs_/projects) and [Modrinth](https://modrinth.com/user/Fuzs).

![](https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/thinair/banner.png)

## Configuration
Many aspects of the Thin Air mod are fully configurable using a data pack.

All entities that are to be affected by the breathing mechanics, meaning they need to be supplied with air in certain parts of the world where air quality reaches yellow or red levels, 
are controlled by the `thinair:air_quality_sensitive` entity type tag.

Blocks that create a certain air quality around themselves are controlled via the following block tags:
- `thinair:blue_air_providers`
- `thinair:green_air_providers`
- `thinair:yellow_air_providers`
- `thinair:red_air_providers`

Lastly equipment that supplies air while surrounded by yellow air quality like the respirator is controlled via the `thinair:breathing_equipment` item tag. 
This tag can be applied to any item that can be equipped, both all four armor slots and slots registered via Trinkets / Curios are supported. 
If the item has a durability bar it will occasionally receive damage.

Additional options like defining air quality levels for dimensions and certain y-levels in those dimensions and changing block ranges of the air quality providers are supported via the `thinair-server.toml` configuration file.
