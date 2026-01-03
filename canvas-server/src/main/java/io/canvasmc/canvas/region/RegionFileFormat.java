package io.canvasmc.canvas.region;

/**
 * Enum representing the available region file formats.
 * LINEAR format uses ZSTD compression for ~50% disk space savings.
 */
public enum RegionFileFormat {
    ANVIL, // Standard Minecraft format (ZLIB compression)
    LINEAR // Kaiiju Linear format (ZSTD compression, ~50% smaller)
}
