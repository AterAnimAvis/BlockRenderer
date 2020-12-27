declare function print(...args);

declare var API: {

    /**
     * Returns the Identifier of the Entity's EntityType
     * @param entity
     * @return Identifier
     */
    getIdentifier(entity: Entity): string

    /**
     * Serializes the entity to nbt then transforms the nbt into a JSON String
     * @param entity
     * @return string
     */
    asJsonString(entity: Entity): string

    /**
     * @param namespace
     * @param path
     * @return Identifier
     */
    location(namespace: string, path: string): Identifier

};

declare class Bounds {

    static of(x: number, y: number, z: number): Bounds

    static of(x: number, y: number, z: number, ox: number, oy: number, oz: number): Bounds

}

declare class Entity {

}

declare class Identifier {

    constructor(resource: string)

    constructor(namespace: string, path: string)

    equals(other: Identifier): boolean

}