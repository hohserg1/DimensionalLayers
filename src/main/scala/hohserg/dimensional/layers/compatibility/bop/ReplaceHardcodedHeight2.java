package hohserg.dimensional.layers.compatibility.bop;

import biomesoplenty.common.world.generator.tree.*;
import gloomyfolken.hooklib.api.Hook;
import gloomyfolken.hooklib.api.HookContainer;
import gloomyfolken.hooklib.api.OnExpression;
import gloomyfolken.hooklib.api.Shift;
import hohserg.dimensional.layers.data.LayerManagerServer;
import hohserg.dimensional.layers.data.WorldData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import scala.Option;

import java.util.Random;

@HookContainer
public class ReplaceHardcodedHeight2 {
    public static int _256() {
        return 256;
    }

    public static int _255() {
        return 255;
    }

    public static int worldMaxY(World world) {
        Option<WorldData> maybeWorldData = LayerManagerServer.getWorldData(world);
        if (maybeWorldData.isDefined())
            return maybeWorldData.get().maxBlockY();
        else
            return 255;
    }

    @Hook(targetMethod = "generate")
    @OnExpression(expressionPattern = "_256", shift = Shift.INSTEAD)
    public static int replaceMaxHeightBasicTree(GeneratorBasicTree self, World world, Random random, BlockPos pos) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightBayouTree(GeneratorBayouTree self, World world, BlockPos pos, int rootHeight, int middleHeight, int height) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightBulbTree(GeneratorBulbTree self, World world, BlockPos pos, int baseHeight, int height) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkEnoughSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightHugeTree(GeneratorHugeTree self, World world, BlockPos pos, int height) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightMangroveTree(GeneratorMangroveTree self, World world, BlockPos pos, int rootHeight, int middleHeight, int height) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightPalmTree(GeneratorPalmTree self, World world, BlockPos pos, int height, int radius) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightPineTree(GeneratorPineTree self, World world, BlockPos pos, int height, int radius) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightProfileTree(GeneratorProfileTree self, World world, BlockPos pos, int height, int radius) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightRedwoodTree(GeneratorRedwoodTree self, World world, BlockPos pos, int height, int radius) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "canPlaceHere")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightRedwoodTreeThin(GeneratorRedwoodTreeThin self, World world, BlockPos pos, int height, int radius) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "generate")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightRedwoodTreeThin(GeneratorRedwoodTreeThin self, World world, Random random, BlockPos pos) {
        return worldMaxY(world);
    }

    @Hook(targetMethod = "checkSpace")
    @OnExpression(expressionPattern = "_255", shift = Shift.INSTEAD)
    public static int replaceMaxHeightTaigaTree(GeneratorTaigaTree self, World world, BlockPos pos, int baseHeight, int height) {
        return worldMaxY(world);
    }
}
