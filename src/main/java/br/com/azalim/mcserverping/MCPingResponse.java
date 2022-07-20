/*
 * Copyright 2014 jamietech. All rights reserved.
 * https://github.com/jamietech/MinecraftServerPing
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
package br.com.azalim.mcserverping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.azalim.mcserverping.serializers.ForgeDataSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * References: http://wiki.vg/Server_List_Ping
 * https://gist.github.com/thinkofdeath/6927216
 */
@Getter
@ToString
public class MCPingResponse {

    /**
     * @return the MOTD
     */
    private Description description;

    /**
     * @return @{link Players}
     */
    private Players players;

    /**
     * @return @{link Version}
     */
    private Version version;

    /**
     * @return Base64 encoded favicon image
     */
    private String favicon;

    /**
     * @return Ping in ms.
     */
    @Setter
    private long ping;

    @Setter
    private String software;

    @Setter
    private List<Plugin> pluginlist;

    @SerializedName(value="modinfo", alternate = "forgeData")
    @Setter
    private ModInfo modinfo;

    @Getter
    @ToString
    @AllArgsConstructor
    public static class ModInfo {
        private String type;

        private int fmlNetworkVersion;

        private Set<Channel> channels;

        @SerializedName(value = "modList", alternate = "mods")
        private Set<Mod> modList;

        private boolean truncated;

        @JsonAdapter(ForgeDataSerializer.class)
        private ModInfo d;
    }

    @Getter @ToString @AllArgsConstructor
    public static class Channel {
        private String res;
        private String version;
        private boolean required;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Mod {
        @SerializedName(value = "modid", alternate = "modId")
        private String modid;

        @SerializedName(value = "version", alternate = "modmarker")
        private String version;
    }

    @Getter
    @ToString
    public class Description {

        /**
         * @return Server description text
         */
        private String text;

        public String getStrippedText() {
            return MCPingUtil.stripColors(this.text);
        }

    }

    @Getter
    @ToString
    public class Players {

        /**
         * @return Maximum player count
         */
        private int max;

        /**
         * @return Online player count
         */
        private int online;

        /**
         * @return List of some players (if any) specified by server
         */
        @Setter
        private List<Player> sample;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Player {

        /**
         * @return Name of player
         */
        private String name;

        /**
         * @return Player's UUID
         */
        private String id;

    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Plugin {

        private String name;
        private String version;

    }

    @Getter
    @ToString
    public class Version {

        /**
         * @return Version name (ex: 13w41a)
         */
        private String name;
        /**
         * @return Protocol version
         */
        private int protocol;

    }

}
