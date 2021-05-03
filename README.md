BlockRenderer
---

See [meta](https://github.com/AterAnimavis/BlockRenderer/tree/meta) branch for more information.

### Installation

We make use of custom yarn mappings to reduce patch size.

You can generate these mappings via [mcp2yarn](https://github.com/AterAnimAvis/mcp2yarn) with the following `gradle.properties`

```properties
####################################################### Intellij #######################################################
# suppress inspection "UnusedProperty" for whole file

###################################################### MCP-Config ######################################################
mcpType        =release
mcpVersion     =1.16.5

####################################################### Mappings #######################################################
mappingsChannel=official
mappingsVersion=1.16.5

######################################################### Yarn #########################################################
yarnVersion    =1.16.5
yarnBuild      =8
```

Clone mcp2yarn, modify the `gradle.properties` to match and then run `gradle install` to generate and install the mappings to your local
maven repository