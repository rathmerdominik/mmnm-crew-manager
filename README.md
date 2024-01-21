
<h1 align="center">Discord Role Assigner</h1>  

This [Mine Mine no Mi](https://modrinth.com/mod/mine-mine-no-mi) Addon manages the sizes of your crews and can **optionally** use [Discord Integration](https://modrinth.com/plugin/dcintegration) to synchronize your crews with Discord.

It can:
- Limit your crew sizes
- Dissolve existing crews that go over the crew limit

If Discord Integration it will additionally:
- List Crews neatly organized in a forum channel
- Show crew creation date
- Display captain
- Display members
- Display the crews banner

# Important

In order for the addon to work several changes to the main Mine Mine no Mi mod had to be made.  
Those changes have already been committed by me to the main mod and will be available with the next Mine Mine no Mi update.  
Specifics can be found [here](https://github.com/rathmerdominik/mine-mine-no-mi-modded).  

It is imperative that for now this custom version is used. Changes in there are purely server sided. So only you need to install this version on your server.  
Players can still use the normal Mine Mine no Mi Version.

I HAVE EXPLICIT PERMISSION BY THE CREATOR OF THE MINE MINE NO MI MOD TO REDISTRIBUTE THIS.  
YOU DO NOT! THEREFORE EVERY FURTHER REDISTRIBUTION HAS TO HAPPEN BY REFERENCING TO THIS PAGE!

!!! A bit of competency is required to use this mod !!!  
Do not be illiterate.  
Read the instructions!


## Compile it yourself

```
git clone https://github.com/rathmerdominik/mmnm-crew-manager
cd mmnm-crew-manager
cd libs
curl -L -O https://github.com/rathmerdominik/mine-mine-no-mi-modded/raw/main/release/mine-mine-no-mi-1.16.5-0.9.5-HAMMER.jar
cd ..
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
