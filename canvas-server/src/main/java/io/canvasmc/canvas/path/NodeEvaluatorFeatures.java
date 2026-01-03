package io.canvasmc.canvas.path;

import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

public record NodeEvaluatorFeatures(NodeEvaluatorType type, boolean canPassDoors, boolean canFloat,
        boolean canWalkOverFences, boolean canOpenDoors, boolean allowBreaching) {
    public static NodeEvaluatorFeatures fromNodeEvaluator(NodeEvaluator nodeEvaluator) {
        return new NodeEvaluatorFeatures(
                NodeEvaluatorType.fromNodeEvaluator(nodeEvaluator),
                nodeEvaluator.canPassDoors(), nodeEvaluator.canFloat(),
                nodeEvaluator.canWalkOverFences(), nodeEvaluator.canOpenDoors(),
                nodeEvaluator instanceof SwimNodeEvaluator s && s.allowBreaching);
    }
}
