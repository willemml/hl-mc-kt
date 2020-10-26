[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/willemml/hl-mc-kt/Java%20CI%20with%20Gradle?logo=github)](https://github.com/wnuke/hl-mc-kt/actions?query=workflow%3A%22Java%20CI%20with%20Gradle%22)
# Headless Minecraft Kotlin (hl-mc-kt)

This is a headless client for vanilla Minecraft servers, it is written in Kotlin and has support for movement and chat. You can also use this to create Discord bots, this project can be use both as a library and a standalone application.

My other project, [kt-cmd](https://github.com/willemml/kt-cmd) is used managing and parsing commands.

### Building:
Clone the repo and enter its directory:
 - `git clone https://github.com/willemml/hl-kt-mc`
 - `cd hl-mc-kt`

Update the submodules:
 - `git submodule update --init --recursive`
 
Build:
 - `./gradlew build`
 
Running with gradle:
 - `./gradlew run`

#### Credits
 - Steveice10's [MCProtocolLib](https://github.com/Steveice10/MCProtocolLib) for saving me lots of time.
 - Tea-Ayataka's [Kordis](https://github.com/Tea-Ayataka/Kordis) for making it easy to interact with the Discord API.
 - PorkStudios' [PorkLib](https://github.com/PorkStudios/PorkLib) for Minecraft text parsing.
 - PrismarineJS' [minecraft-data](https://github.com/PrismarineJS/minecraft-data) for easy to access block and item definitions.
