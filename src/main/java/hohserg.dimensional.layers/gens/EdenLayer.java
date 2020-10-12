package hohserg.dimensional.layers.gens;

import divinerpg.registry.BlockRegistry;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.minecraft.init.Blocks.AIR;

public class EdenLayer implements LayerFiller {
    private final Random rand;
    private final int startY;
    private final NoiseGeneratorOctaves noiseGen1;
    private final NoiseGeneratorOctaves perlinNoise1;

    public EdenLayer(World world, int startY) {
        rand = new Random(world.getSeed() + world.provider.getDimension());
        noiseGen1 = new NoiseGeneratorOctaves(rand, 16);
        perlinNoise1 = new NoiseGeneratorOctaves(rand, 8);
        this.startY = startY;
    }

    private double buffer[], pnr[], ar[], br[];

    @Override
    public IBlockState generateBlockState(BlockPos absolutePos) {
        BlockPos localPos = absolutePos.add(0, -startY, 0);
        if (!chunkCache.containsKey(localPos))
            generateAll(localPos);
        IBlockState r = chunkCache.get(localPos);
        chunkCache.remove(localPos);
        return r;
    }

    private Map<BlockPos, IBlockState> chunkCache = new HashMap<>();

    private void generateAll(BlockPos pos) {
        int x = pos.getX() >> 4;
        int z = pos.getZ() >> 4;
        buffer = setupNoiseGenerators(buffer, x * 2, z * 2);

        for (int i1 = 0; i1 < 2; i1++) {
            for (int j1 = 0; j1 < 2; j1++) {
                for (int k1 = 0; k1 < 32; k1++) {
                    double d1 = buffer[(i1 * 3 + j1) * 33 + k1];
                    double d2 = buffer[(i1 * 3 + (j1 + 1)) * 33 + k1];
                    double d3 = buffer[((i1 + 1) * 3 + j1) * 33 + k1];
                    double d4 = buffer[((i1 + 1) * 3 + (j1 + 1)) * 33 + k1];

                    double d5 = (buffer[(i1 * 3 + j1) * 33 + (k1 + 1)] - d1) * 0.25D;
                    double d6 = (buffer[(i1 * 3 + (j1 + 1)) * 33 + (k1 + 1)] - d2) * 0.25D;
                    double d7 = (buffer[((i1 + 1) * 3 + j1) * 33 + (k1 + 1)] - d3) * 0.25D;
                    double d8 = (buffer[((i1 + 1) * 3 + (j1 + 1)) * 33 + (k1 + 1)] - d4) * 0.25D;

                    for (int l1 = 0; l1 < 4; l1++) {
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.125D;
                        double d13 = (d4 - d2) * 0.125D;

                        for (int i2 = 0; i2 < 8; i2++) {
                            double d15 = d10;
                            double d16 = (d11 - d10) * 0.125D;

                            for (int k2 = 0; k2 < 8; k2++) {
                                int x1 = i2 + i1 * 8;
                                int y = l1 + k1 * 4;
                                int z1 = k2 + j1 * 8;

                                IBlockState filler = AIR.getDefaultState();
//                                if (d15 < -38D) {
//                                }
//                                if (d15 < -39D && d15 > -43D) {
//                                    if (d15 < -41D) {
//                                    }
//                                }
//                                if (d15 < -44D && d15 > -46D) {
//                                    if (d15 < -44.25D) {
//                                    }
//                                }
                                if (d15 > 0.0D) {
                                    filler = BlockRegistry.twilightStone.getDefaultState();
                                }

                                chunkCache.put(new BlockPos(x1, y, z1), filler);

                                d15 += d16;
                            }
                            d10 += d12;
                            d11 += d13;
                        }
                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    protected double[] setupNoiseGenerators(double buffer[], int x, int z) {
        if (buffer == null) {
            buffer = new double[3366];
        }

        double d = 1368.824D;
        double d1 = 684.41200000000003D;

        pnr = perlinNoise1.generateNoiseOctaves(pnr, x, 0, z, 3, 33, 3, d / 80D, d1 / 160D, d / 80D);
        ar = noiseGen1.generateNoiseOctaves(ar, x, 0, z, 3, 33, 3, d, d1, d);
        br = noiseGen1.generateNoiseOctaves(br, x, 0, z, 3, 33, 3, d, d1, d);

        int id = 0;

        for (int j2 = 0; j2 < 3; j2++) {
            for (int l2 = 0; l2 < 3; l2++) {
                for (int j3 = 0; j3 < 33; j3++) {
                    double d8;

                    double d10 = ar[id] / 512D;
                    double d11 = br[id] / 512D;
                    double d12 = (pnr[id] / 10D + 1.0D) / 2D;

                    if (d12 < 0.0D) {
                        d8 = d10;
                    } else if (d12 > 1.0D) {
                        d8 = d11;
                    } else {
                        d8 = d10 + (d11 - d10) * d12;
                    }
                    d8 -= 8D;
                    if (j3 > 33 - 32) {
                        double d13 = (float) (j3 - (33 - 32)) / ((float) 32 - 1.0F);
                        d8 = d8 * (1.0D - d13) + -30D * d13;
                    }
                    if (j3 < 8) {
                        double d14 = (float) (8 - j3) / ((float) 8 - 1.0F);
                        d8 = d8 * (1.0D - d14) + -30D * d14;
                    }
                    buffer[id] = d8;
                    id++;
                }
            }
        }
        return buffer;
    }

    @Override
    public void poputale(ICube cube) {

    }
}
