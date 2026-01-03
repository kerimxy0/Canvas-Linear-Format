package io.canvasmc.canvas.path;

import net.minecraft.world.level.pathfinder.NodeEvaluator;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface NodeEvaluatorGenerator {
    @NotNull
    NodeEvaluator generate(NodeEvaluatorFeatures nodeEvaluatorFeatures);
}
