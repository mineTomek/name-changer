# Name Changer

[![build](https://github.com/mineTomek/name-changer/actions/workflows/build.yml/badge.svg)](https://github.com/mineTomek/name-changer/actions/workflows/build.yml)

A mod for changing a player’s name. Designed to replace the player’s name consistently across the game.

Useful for roleplay servers, content creation, screenshots, or simply using a different display name without changing your account username.

Needs to be installed on both the client and the server for full functionality.

Available on [Modrinth](https://modrinth.com/mod/namechanger) for NeoForge and Fabric.

## Features

The primary feature of this mod is obviously changing the player's name. Here’s every place this affects:

### UI & Display
- Chat name labels
- Nameplates above players
- Player list (under `Tab`)
- Social interactions screen
- Scoreboard sidebar display
- MOTD for LAN worlds

### Commands & System Integration
- Entity selection in commands
  - Works with both suggestions and execution
  - Original names still work
- Various system messages
  - Death
  - Advancements
  - Join/Leave

On top of that there's the `/name` command for changing the name, which requires operator permissions. It has two subcommands:

- `/name set <target> <name>` sets the name of the target player
- `/name reset [<target>]` resets the name of the target player (or self if target is omitted) to what it was originally

Custom names are stored per-world and persist between reloads.

## Names

The custom name entered in the `/name set` command is a text component. You can read more about them on the [Text Component](https://minecraft.wiki/w/Text_component_format#Java_Edition) page on the Minecraft Wiki.

Here are examples of using the `/name set` command:

**Plain text:**

`/name set @s mineTomek`

**Plain text with spaces or other special characters:**

`/name set @s "mineTomek :3"`

**Formatted text:**

`/name set @s {text: "mineTomek", color: "#6dc53b", bold: true}`

Formatting only affects chat messages and the player list.\
In most other places, Minecraft strips formatting and displays plain text instead.
