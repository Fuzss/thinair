# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.1.4-1.20.1] - 2023-01-24
### Added
- Add `thinair:heavy_breathing_utility` item tag for equipment that can protect from red air quality, the tag is empty by default
### Fixed
- Fix breathing equipment not breaking and getting negative durability values

## [v8.1.3-1.20.1] - 2023-11-07
### Fixed
- Third time is a charm: fix crash when checking air quality

## [v8.1.2-1.20.1] - 2023-11-06
### Fixed
- Once again try to fix crash when checking air quality

## [v8.1.1-1.20.1] - 2023-11-06
### Fixed
- Fixed crash when checking air quality

## [v8.1.0-1.20.1] - 2023-11-05
### Changed
- Only selected entities are now affected by the new air mechanics, by default this includes the player and humanoid mobs such as villagers and pillagers
- Affected entities are controlled via an entity type tag: `thinair:air_quality_sensitive`
- Changed air provider radius in config to apply to the whole air quality level instead of per block to allow for tag support
- Air quality providers such as soul fire blocks and portal blocks are now controlled via dedicated block tags:
  - `thinair:blue_air_providers`
  - `thinair:green_air_providers`
  - `thinair:yellow_air_providers`
  - `thinair:red_air_providers`
- Equipment keeping you safe from yellow air quality like the respirator is now controlled via a new item tag: `thinair:breathing_utility`, so you can e.g. configure certain pieces of armor to protect you as well
- Reworked air bladder use animations and mechanics
- Rewrote local air quality handling: only the server calculates values and syncs those to clients, clients no longer do any scanning on their own
- Moved some basic classes to a dedicated `api` package
### Fixed
- Fixed start-up crash on newer versions of Forge
- Fixed conduit power no longer allowing to breath underwater
- Fixed respirator not working
- Fixed occasional crash when saving level
- Fixed safety lantern not always showing correct air quality when in inventory

## [v8.0.0-1.20.1] - 2023-06-27
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
