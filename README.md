
<h1 align="center">Discord Role Assigner</h1>  

This [Mine Mine no Mi](https://modrinth.com/mod/mine-mine-no-mi) Addon manages the sizes of your crews and can **optionally** use [Discord Integration](https://modrinth.com/plugin/dcintegration) to synchronize your crews with Discord.

It can:
- Limit your crew sizes
- Dissolve existing crews that go over the crew limit

If Discord Integration is present it will additionally:
- List Crews neatly organized in a forum channel
- Show crew creation date
- Display captain
- Display members
- Display the crews banner

## Compile it yourself

```
git clone https://github.com/rathmerdominik/mmnm-crew-manager
cd mmnm-crew-manager
./gradlew build
cd build/libs
```

You can now just take the jar file out of this directory and place it under the `mods/` folder on your server.

# Config Options

```toml
["Crew Display"]
        "Sync Crew Members" = true
        "Show Crew Creation Date" = true
        #Has to be a Forum channel! Therefore your server MUST be a community server if you want to use this feature!
        #Range: 0 ~ 9223372036854775807
        "Crew Forum Channel Id" = 0
        "Show Captain of the crew" = true
        "Sync Crew Banner" = true

["Crew Manager"]
        #Leave it as -1 if you do not want any crew size limits. Crew size includes the captain! So 4 Members and 1 Captain would need a crew size of 5 to exist.
        # Setting this to 0 will disable crews on your server.
        #Range: > -1
        "Limit crew size" = -1
        #If set to true existing crews that are over the crew limit will be dissolved and removed
        "Dissolve existing crews" = false
```
