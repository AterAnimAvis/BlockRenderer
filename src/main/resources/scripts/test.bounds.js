/**
 * Nashorn Scripting
 * - ES6ish
 */

/* Zombie Type */
const ZOMBIE_TYPE = "minecraft:zombie"

// noinspection JSUnusedGlobalSymbols
/**
 * Returns the bounds for the provided entity
 * @param entity : Entity
 * @param bounds : Bounds The current provided bounds for the entity
 * @returns Bounds
 */
function bounds(entity, bounds) {
  print("Hello World from JS: ") // > Hello World JS:

  print(bounds)                       // > Bounds.Instance[size=(0.6 1.95 0.6),offset=(0.3 0 0.3)]

  // N.B. I would recommend only calculating this once.
  let entityJson = JSON.parse(API.asJsonString(entity))
  print(JSON.stringify(entityJson))   // > {"Brain":{"memories":{}},"HurtByTimestamp":0,"Attributes":[{"Base":20, ...
                                      // "CanPickUpLoot":0,"HurtTime":0,"DrownedConversionTime":-1}

  return Bounds.of(1.2, 2.10, 1.2, 0.6, 0, 0.6)
}

// noinspection JSUnusedGlobalSymbols
/**
 * Returns true if the bounds function should be ran for the provided entity
 * @param entity : Entity
 * @returns boolean
 */
function validFor(entity) {
  return API.getIdentifier(entity) === ZOMBIE_TYPE
}
