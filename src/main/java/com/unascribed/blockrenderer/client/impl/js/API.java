package com.unascribed.blockrenderer.client.impl.js;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.Objects;

@SuppressWarnings("unused")
public class API {

    public static String getIdentifier(Entity entity) {
        return EntityType.getKey(entity.getType()).toString();
    }

    public static String asJsonString(Entity entity) {
        CompoundNBT nbt = entity.serializeNBT();
        if (nbt == null) return "{}";
        return asString(nbt);
    }

    private static String asString(INBT nbt) {
        StringBuilder buffer = new StringBuilder();
        switch (nbt.getId()) {
            case NBT.TAG_END:        // 0
                break;
            case NBT.TAG_BYTE:       // 1
                buffer.append(((NumberNBT) nbt).getByte());
                break;
            case NBT.TAG_SHORT:      // 2
                buffer.append(((NumberNBT) nbt).getShort());
                break;
            case NBT.TAG_INT:        // 3
                buffer.append(((NumberNBT) nbt).getInt());
                break;
            case NBT.TAG_LONG:       // 4
                buffer.append(((NumberNBT) nbt).getLong());
                break;
            case NBT.TAG_FLOAT:      // 5
                buffer.append(((NumberNBT) nbt).getFloat());
                break;
            case NBT.TAG_DOUBLE:     // 6
                buffer.append(((NumberNBT) nbt).getDouble());
                break;
            case NBT.TAG_BYTE_ARRAY: // 7
                ByteArrayNBT byteNBTs = (ByteArrayNBT) nbt;
                byte[] bytes = byteNBTs.getByteArray();
                buffer.append("[");
                for (int i = 0; i < bytes.length; ++i) {
                    if (i != 0) buffer.append(',');
                    buffer.append(bytes[i]);
                }
                buffer.append("]");
                break;
            case NBT.TAG_STRING:     // 8
                // buffer.append('"').append(nbt.getString()).append('"');
                buffer.append(nbt.toString());
                break;
            case NBT.TAG_LIST:       // 9
                ListNBT listNBT = (ListNBT) nbt;
                buffer.append("[");
                for (int i = 0; i < listNBT.size(); ++i) {
                    if (i != 0) buffer.append(',');
                    buffer.append(asString(listNBT.get(i)));
                }
                buffer.append("]");
                break;
            case NBT.TAG_COMPOUND:   // 10
                CompoundNBT compoundNBT = (CompoundNBT) nbt;
                boolean addComma = false;
                buffer.append("{");
                for (String key : compoundNBT.keySet()) {
                    if (addComma) buffer.append(',');

                    buffer.append(StringNBT.quoteAndEscape(key)).append(':').append(asString(Objects.requireNonNull(compoundNBT.get(key))));

                    addComma = true;
                }
                buffer.append("}");
                break;
            case NBT.TAG_INT_ARRAY:  // 11
                IntArrayNBT intNBTs = (IntArrayNBT) nbt;
                int[] ints = intNBTs.getIntArray();
                buffer.append("[");
                for (int i = 0; i < ints.length; ++i) {
                    if (i != 0) buffer.append(',');
                    buffer.append(ints[i]);
                }
                buffer.append("]");
                break;
            case NBT.TAG_LONG_ARRAY: // 12
                LongArrayNBT longNBTs = (LongArrayNBT) nbt;
                long[] longs = longNBTs.getAsLongArray();
                buffer.append("[");
                for (int i = 0; i < longs.length; ++i) {
                    if (i != 0) buffer.append(',');
                    buffer.append(longs[i]);
                }
                buffer.append("]");
                break;

        }

        return buffer.toString();
    }

}
