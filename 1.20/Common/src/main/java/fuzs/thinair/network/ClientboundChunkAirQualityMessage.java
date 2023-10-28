package fuzs.thinair.network;

import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.thinair.api.AirQualityLevel;
import fuzs.thinair.init.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;

public record ClientboundChunkAirQualityMessage(ChunkPos chunkPos, Map<BlockPos, AirQualityLevel> airBubblePositions, Mode mode) implements ClientboundMessage<ClientboundChunkAirQualityMessage> {

    @Override
    public ClientMessageListener<ClientboundChunkAirQualityMessage> getHandler() {
        return new ClientMessageListener<>() {

            @Override
            public void handle(ClientboundChunkAirQualityMessage message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                if (level.getChunkSource().hasChunk(message.chunkPos.x, message.chunkPos.z)) {
                    LevelChunk chunk = level.getChunkSource().getChunkNow(message.chunkPos.x, message.chunkPos.z);
                    ModRegistry.AIR_BUBBLE_POSITIONS_CAPABILITY.maybeGet(chunk).ifPresent(capability -> {
                        final Map<BlockPos, AirQualityLevel> airBubblePositions = capability.getAirBubblePositions();
                        switch (message.mode) {
                            case REPLACE_ALL -> {
                                airBubblePositions.clear();
                                airBubblePositions.putAll(message.airBubblePositions);
                            }
                            case ADD -> {
                                airBubblePositions.putAll(message.airBubblePositions);
                            }
                            case REMOVE -> {
                                message.airBubblePositions.keySet().forEach(airBubblePositions::remove);
                            }
                        }
                    });
                }
            }
        };
    }

    public enum Mode {
        REPLACE_ALL, REMOVE, ADD
    }
}
