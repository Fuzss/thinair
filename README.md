# Thin Air

A Minecraft mod. Downloads can be found on [CurseForge](https://www.curseforge.com/members/fuzs_/projects) and [Modrinth](https://modrinth.com/user/Fuzs).

![](https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/thinair/banner.png)

## Configuration
Many aspects of the Thin Air mod are fully configurable using a data pack.

### Affected Entities
All entities that are to be affected by the breathing mechanics, meaning they need to be supplied with air in certain parts of the world where air quality reaches yellow or red levels,
are controlled by the `thinair:air_quality_sensitive` entity type tag.

### Air Quality Levels
Thin Air introduces four different levels of air quality you will encounter in your world.

| Air Quality | Allows Breathing | Allows Refilling Air | Air Loss    | Dimensions                                          | Source Blocks                                               |
|-------------|------------------|----------------------|-------------|-----------------------------------------------------|-------------------------------------------------------------|
| Green       | ✅                | ✅                    | None        | Overworld (Y=0 to Y=128)                            | Nether Portal<br/>End Portal<br/>End Gateway                |
| Yellow      | ❌                | ❌                    | 0.25 / tick | Overworld (Y<0 & Y>128)<br/>The Nether (everywhere) | None                                                        |
| Red         | ❌                | ❌                    | 1 / tick    | The End (everywhere)                                | Lava<br/>Water<br/>Fire                                     |
| Blue        | ✅                | ❌                    | None        | None                                                | Soul Campfire<br/>Soul Torch<br/>Soul Fire<br/>Soul Lantern |

### Air Providers
Blocks that create a certain air quality around themselves independently of the player's location in the world are controlled via the following block tags:
- `thinair:blue_air_providers`
- `thinair:green_air_providers`
- `thinair:yellow_air_providers`
- `thinair:red_air_providers`

### Breathing Equipment
Lastly equipment that supplies air while surrounded by yellow air quality like the respirator is controlled via the `thinair:breathing_equipment` item tag. 
There is an additional `thinair:heavy_breathing_equipment` item tag offering protection from red air, which is empty by default though.
Both tags can be applied to any item that can be equipped with all four armor slots and slots registered via Trinkets / Curios being supported. 
If the item has a durability bar it will occasionally receive damage.

### Further Customization
Additional options like defining air quality levels for dimensions and certain y-levels in those dimensions and changing block ranges of the air quality providers are supported via the `thinair-server.toml` configuration file.
