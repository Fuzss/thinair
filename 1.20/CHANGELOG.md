# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.1.0-1.20.1] - 2023-10-18
### Changed
- Only selected entities are now affected by the new air mechanics, by default this includes the player and humanoid mobs such as villagers and pillagers
- Additional entities can be added in the config in a whitelist (used to be a blacklist)
- Changed air provider radius in config to apply to the whole air quality level instead of per block
- Air quality providers such as soul fire blocks and portal blocks are now controlled via dedicated block tags:
  - `thinair:blue_air_providers`
  - `thinair:green_air_providers`
  - `thinair:yellow_air_providers`
  - `thinair:red_air_providers`
### Fixed
- Fixed start-up crash on Forge
- Fixed conduit power no longer allowing to breath underwater
- Fixed respirator not working
- Fixed occasional crash when saving level

## [v8.0.0-1.20.1] - 2023-06-27
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
