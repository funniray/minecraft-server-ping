package br.com.azalim.mcserverping.serializers;

import br.com.azalim.mcserverping.MCPingResponse;
import br.com.azalim.mcserverping.utils.FriendlyByteBuf;
import com.google.gson.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.var;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ForgeDataSerializer implements JsonDeserializer<MCPingResponse.ModInfo> {
    public static final String IGNORESERVERONLY = "OHNOES\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31";
    private static final int VERSION_FLAG_IGNORESERVERONLY = 0b1;

    private static MCPingResponse.ModInfo deserializeOptimized(String data)
    {
        var buf = new FriendlyByteBuf(decodeOptimized(data));

        boolean truncated;
        Set<MCPingResponse.Mod> mods;
        Set<MCPingResponse.Channel> channels;

        try
        {
            truncated = buf.readBoolean();
            var modsSize = buf.readUnsignedShort();
            mods = new HashSet<>();
            channels = new HashSet<>();
            for (var i = 0; i < modsSize; i++) {
                var channelSizeAndVersionFlag = buf.readVarInt();
                var channelSize = channelSizeAndVersionFlag >>> 1;
                var isIgnoreServerOnly = (channelSizeAndVersionFlag & VERSION_FLAG_IGNORESERVERONLY) != 0;
                var modId = buf.readUtf();
                var modVersion = isIgnoreServerOnly ? IGNORESERVERONLY : buf.readUtf();
                for (var i1 = 0; i1 < channelSize; i1++) {
                    var channelName = buf.readUtf();
                    var channelVersion = buf.readUtf();
                    var requiredOnClient = buf.readBoolean();
                    channels.add(new MCPingResponse.Channel(channelName, channelVersion, requiredOnClient));
                }

                mods.add(new MCPingResponse.Mod(modId, modVersion));
            }

            var nonModChannelCount = buf.readVarInt();
            for (var i = 0; i < nonModChannelCount; i++) {
                var channelName = buf.readResourceLocation();
                var channelVersion = buf.readUtf();
                var requiredOnClient = buf.readBoolean();
                channels.add(new MCPingResponse.Channel(channelName, channelVersion, requiredOnClient));
            }
        }
        finally
        {
            buf.release();
        }

        return new MCPingResponse.ModInfo(null, 3, channels, mods, truncated, null);
    }

    /**
     * Decode binary data encoded
     */
    private static ByteBuf decodeOptimized(String s)
    {
        var size0 = ((int) s.charAt(0));
        var size1 = ((int) s.charAt(1));
        var size = size0 | (size1 << 15);

        var buf = Unpooled.buffer(size);

        int stringIndex = 2;
        int buffer = 0; // we will need at most 8 + 14 = 22 bits of buffer, so an int is enough
        int bitsInBuf = 0;
        while (stringIndex < s.length())
        {
            while (bitsInBuf >= 8)
            {
                buf.writeByte(buffer);
                buffer >>>= 8;
                bitsInBuf -= 8;
            }

            var c = s.charAt(stringIndex);
            buffer |= (((int) c) & 0x7FFF) << bitsInBuf;
            bitsInBuf += 15;
            stringIndex++;
        }

        // write any leftovers
        while (buf.readableBytes() < size)
        {
            buf.writeByte(buffer);
            buffer >>>= 8;
            bitsInBuf -= 8;
        }
        return buf;
    }

    @Override
    public MCPingResponse.ModInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String d = jsonElement.getAsString();

        if (d != null) {
            return deserializeOptimized(d);
        }

        return null;
    }
}
