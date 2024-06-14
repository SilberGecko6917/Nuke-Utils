# Nuke-Utils Plugin for Minecraft

**Nuke-Utils** is a powerful Minecraft plugin that allows players to create controlled explosions using placed blocks.

## Features

- Generates a nuclear explosion based on configured blocks.
- Configurable explosion power and warnings for players.
- Supports all Minecraft 1.20.* versions.

## Configuration

In the configuration file (`config.yml`), various settings can be adjusted:

| Setting           | Description                                                          |
|-------------------|----------------------------------------------------------------------|
| `material`        | The triggering material for the nuke (must be a block).              |
| `power`           | The strength of the explosion (maximum 150, longer time to explode). |
| `warning`         | Whether to warn players before triggering the explosion.             |
| `cooldown`        | Cooldown in seconds between consecutive explosions.                  |
| `destroy_bedrock` | Whether nukes can destroy bedrock.                                   |

## Installation

1. Download the latest version of the plugin from the [releases page](https://github.com/SilberGecko6917/Nuke-Utils/releases).
2. Place the JAR file into your server's plugins folder.
3. Restart your server or reload the plugin using `/reload`.

## Usage

1. Place the configured block on the map.
2. Interact with the block to trigger the nuke (based on settings in `config.yml`).

## Version History

| Minecraft Version | Plugin Version | Release Date  | Changes           | Tested |
|-------------------|----------------|---------------|-------------------|--------|
| 1.20              | v1.0-ALPHA     | June 14, 2024 | - Initial release | Yes    |
| 1.20.1            | v1.0-ALPHA     | June 14, 2024 | - Initial release | Yes    |
| 1.20.2            | v1.0-ALPHA     | June 14, 2024 | - Initial release | Yes    |
| 1.20.3            | v1.0-ALPHA     | June 14, 2024 | - Initial release | Yes    |
| 1.20.4            | v1.0-ALPHA     | June 14, 2024 | - Initial release | Yes    |
| 1.20.5            | v1.0-ALPHA     | June 14, 2024 | - Initial release | Yes    |

## Support

For support, bug reports, or feature requests, please create an issue on [GitHub](https://github.com/SilberGecko6917/Nuke-Utils/issues).

## License

This plugin is released under the [MIT License](LICENSE).
