# Information
This is a **Spigot** plugin for minecraft servers, the purpose of which is to protect the buildings of other players **automatically**

# Version
Currently the supported version is **_1.12.2_**. If you are an advanced user who understands what he is doing, you can change the version of the api in **_[plugin.yml](src/main/resources/plugin.yml)_** and in **_[pom.xml](pom.xml)_**


# Commands
The latest version of the plugin so far adds 3 commands, the use of which you can read in the game itself or below

### Description of commands
- `/give-block-picker` - issues an item that checks whether any block belongs to which you clicked
- `/disable-block-adding <enable|disable>` - turns off the automatic addition of the blocks you have set to the database (this is useful when you are digging in a mine)
- `/give-guard-remover` - issues an item that removes protection with one click

# Config.yml
When you first start the server with the plugin, a configuration file will appear that can be configured, the purpose of all parameters will be described from the bottom.

### Description of the config parameters
- `databaseFileName: name.db` - determines the name of the database. **_IMPORTANT!_** _At the end of the name there must be an extension of the **.db**_