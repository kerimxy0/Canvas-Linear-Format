package io.canvasmc.canvas.path;

import net.minecraft.world.level.pathfinder.NodeEvaluator;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NodeEvaluatorCache {
    private static final Map<NodeEvaluatorFeatures, ConcurrentLinkedQueue<NodeEvaluator>> threadLocalNodeEvaluators = new ConcurrentHashMap<>();
    private static final Map<NodeEvaluator, NodeEvaluatorGenerator> nodeEvaluatorToGenerator = new ConcurrentHashMap<>();

    private static @NotNull Queue<NodeEvaluator> getQueueForFeatures(@NotNull NodeEvaluatorFeatures features) {
        return threadLocalNodeEvaluators.computeIfAbsent(features, k -> new ConcurrentLinkedQueue<>());
    }

    public static @NotNull NodeEvaluator takeNodeEvaluator(@NotNull NodeEvaluatorGenerator generator,
            @NotNull NodeEvaluator localNodeEvaluator) {
        final NodeEvaluatorFeatures features = NodeEvaluatorFeatures.fromNodeEvaluator(localNodeEvaluator);
        NodeEvaluator nodeEvaluator = getQueueForFeatures(features).poll();
        if (nodeEvaluator == null)
            nodeEvaluator = generator.generate(features);
        nodeEvaluatorToGenerator.put(nodeEvaluator, generator);
        return nodeEvaluator;
    }

    public static void returnNodeEvaluator(@NotNull NodeEvaluator nodeEvaluator) {
        final NodeEvaluatorGenerator generator = nodeEvaluatorToGenerator.remove(nodeEvaluator);
        Validate.notNull(generator, "NodeEvaluator already returned");
        getQueueForFeatures(NodeEvaluatorFeatures.fromNodeEvaluator(nodeEvaluator)).offer(nodeEvaluator);
    }

    public static void removeNodeEvaluator(@NotNull NodeEvaluator nodeEvaluator) {
        nodeEvaluatorToGenerator.remove(nodeEvaluator);
    }
}
