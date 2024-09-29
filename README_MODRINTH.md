#### Download the mod file directly [here](https://github.com/rathmerdominik/mine-mine-no-mi-modded/raw/main/release/mine-mine-no-mi-1.16.5-0.9.5-HAMMER.jar) 

It is imperative that for now this custom version is used. Changes in there are purely server sided. So only you need to install this version on your server.  
Players can still use the normal Mine Mine no Mi Version.

---

<iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/JYq6bmUbr4A" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

#  Crew Manager

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