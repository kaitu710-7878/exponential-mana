package com.exponentialmana.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureGenerator {

    // Theme colors per tier: [Highlight, Base, Shadow, Outline]
    public static final Color[][] TIER_COLORS = new Color[][]{
            // Tier 0: Cyan / Sky Blue
            { new Color(240, 255, 255), new Color(0, 229, 255), new Color(0, 145, 234), new Color(0, 34, 68) },
            // Tier 1: Coal Charcoal
            { new Color(180, 190, 195), new Color(66, 66, 66), new Color(33, 33, 33), new Color(15, 15, 15) },
            // Tier 2: Copper Orange
            { new Color(255, 204, 188), new Color(255, 112, 67), new Color(216, 67, 21), new Color(78, 52, 46) },
            // Tier 3: Redstone Crimson
            { new Color(255, 205, 210), new Color(255, 23, 68), new Color(213, 0, 0), new Color(62, 39, 35) },
            // Tier 4: Glowstone Amber
            { new Color(255, 253, 231), new Color(255, 238, 88), new Color(255, 171, 0), new Color(78, 52, 46) },
            // Tier 5: Iron Silver
            { new Color(255, 255, 255), new Color(207, 216, 220), new Color(120, 144, 156), new Color(38, 50, 56) },
            // Tier 6: Gold Yellow
            { new Color(255, 249, 196), new Color(255, 215, 0), new Color(255, 160, 0), new Color(62, 39, 35) },
            // Tier 7: Diamond Aqua
            { new Color(255, 255, 255), new Color(128, 216, 255), new Color(0, 176, 255), new Color(1, 87, 155) },
            // Tier 8: Obsidian Purple
            { new Color(243, 229, 245), new Color(171, 71, 188), new Color(74, 20, 140), new Color(18, 0, 94) },
            // Tier 9: Emerald Green
            { new Color(232, 248, 245), new Color(0, 230, 118), new Color(0, 200, 83), new Color(27, 94, 32) },
            // Tier 10: Netherite Dark Violet
            { new Color(237, 231, 246), new Color(124, 77, 255), new Color(55, 42, 61), new Color(26, 26, 36) },
    };

    // Pure Mana colors: Brilliant pure white with gentle silver/pearl shading
    public static final Color[] PURE_COLORS = new Color[]{
            new Color(255, 255, 255), new Color(255, 255, 255), new Color(235, 240, 245), new Color(195, 205, 215)
    };

    // Origin palette: Sacred cosmic blending star gold (#FFD700), quantum cyan (#00E5FF), abyssal dark purple (#7C4DFF)
    public static final Color[] ORIGIN_PALETTE = new Color[]{
            new Color(255, 255, 255), // Highlight
            new Color(255, 215, 0),   // Star Gold
            new Color(0, 229, 255),   // Quantum Cyan
            new Color(124, 77, 255),  // Abyssal Violet
            new Color(25, 15, 45)     // Deep Core
    };

    // Vanilla Stone Matrix (16x16 brightness indices from standard stone texture)
    public static final int[][] STONE_NOISE = {
            {2, 1, 2, 2, 3, 2, 1, 2, 3, 3, 2, 1, 2, 2, 2, 1},
            {1, 2, 3, 2, 2, 1, 0, 1, 2, 2, 3, 2, 1, 3, 2, 2},
            {2, 3, 3, 1, 1, 2, 2, 2, 1, 1, 2, 3, 2, 2, 1, 2},
            {2, 2, 1, 0, 1, 3, 3, 1, 0, 1, 2, 2, 1, 0, 1, 2},
            {3, 2, 1, 2, 2, 3, 2, 2, 1, 2, 3, 1, 0, 1, 2, 3},
            {2, 3, 2, 3, 1, 2, 1, 3, 2, 3, 2, 2, 1, 2, 3, 2},
            {1, 2, 3, 2, 2, 1, 2, 2, 3, 2, 1, 2, 2, 3, 2, 1},
            {2, 1, 2, 1, 0, 1, 3, 2, 2, 1, 0, 1, 3, 2, 1, 2},
            {3, 2, 1, 1, 2, 2, 3, 1, 1, 2, 2, 3, 2, 1, 2, 3},
            {2, 3, 2, 2, 3, 1, 2, 2, 2, 3, 1, 2, 2, 2, 3, 2},
            {1, 2, 2, 3, 2, 2, 1, 0, 1, 2, 2, 1, 0, 1, 2, 2},
            {2, 1, 3, 2, 1, 2, 2, 1, 2, 3, 2, 2, 1, 2, 3, 1},
            {2, 2, 2, 1, 2, 3, 2, 2, 3, 2, 1, 2, 2, 3, 2, 2},
            {3, 2, 1, 0, 1, 2, 3, 1, 2, 1, 0, 1, 3, 2, 1, 2},
            {2, 3, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 3},
            {1, 2, 3, 2, 1, 2, 1, 3, 2, 1, 2, 1, 3, 2, 2, 1}
    };

    public static final Color[] STONE_PAL = {
            new Color(95, 95, 95), new Color(115, 115, 115), new Color(128, 128, 128), new Color(145, 145, 145)
    };

    public static final Color[] OBSIDIAN_PAL = {
            new Color(16, 12, 28), new Color(27, 18, 44), new Color(38, 23, 60), new Color(59, 39, 84)
    };

    public static void main(String[] args) throws IOException {
        String baseDir = args.length > 0 ? args[0] : "src/main/resources/assets/exponential_mana/textures";
        File itemDir = new File(baseDir, "item");
        File blockDir = new File(baseDir, "block");
        itemDir.mkdirs();
        blockDir.mkdirs();

        System.out.println("Updating textures in: " + baseDir);

        // 1. Mana Spheres (Tier 0 to 10)
        for (int t = 0; t <= 10; t++) {
            save(createManaSphere(TIER_COLORS[t]), new File(itemDir, "tier" + t + "_mana.png"));
        }
        // Pure Mana: Pure white
        save(createPureManaSphere(), new File(itemDir, "pure_mana.png"));
        // Infinite Mana: Multicolored cosmic nexus with singularity core
        save(createInfiniteMana(), new File(itemDir, "infinite_mana.png"));
        // Mana Root: 4 roots converging swirl
        save(createRootOfMana(), new File(itemDir, "mana_root.png"));

        // 2. Cores (Tier 1 to 10): Mystical ring & casing with large theme sphere peeking through
        for (int t = 1; t <= 10; t++) {
            save(createCoreWithPeekingSphere(TIER_COLORS[t]), new File(itemDir, "tier" + t + "_core.png"));
        }

        // 3. 4 Root Manas based on origin items
        save(createStarMana(false), new File(itemDir, "star_mana.png"));
        save(createStarMana(true), new File(itemDir, "uncompressed_star_mana.png"));
        save(createQuantumMana(false), new File(itemDir, "quantum_mana.png"));
        save(createQuantumMana(true), new File(itemDir, "uncompressed_quantum_mana.png"));
        save(createAbyssalMana(false), new File(itemDir, "abyssal_mana.png"));
        save(createAbyssalMana(true), new File(itemDir, "uncompressed_abyssal_mana.png"));
        save(createMarineMana(false), new File(itemDir, "marine_mana.png"));
        save(createMarineMana(true), new File(itemDir, "uncompressed_marine_mana.png"));

        // 4. Batch Compressed Mana: 1/4 size Tier 0 mana in 4 corners + enchantment shimmer
        save(createBatchCompressedMana(), new File(itemDir, "batch_compressed_mana.png"));

        // 5. Fairy Catalysts & Residue
        for (int t = 1; t <= 10; t++) {
            save(createFairyCatalyst(TIER_COLORS[t]), new File(itemDir, "tier" + t + "_fairy_catalyst.png"));
        }
        save(createFairyCatalyst(PURE_COLORS), new File(itemDir, "pure_fairy_catalyst.png"));
        save(createFairyResidue(), new File(itemDir, "fairy_residue.png"));

        // 6. Shards & Mini Tables
        for (int t : new int[]{1, 3, 5, 7, 9}) {
            save(createShard(TIER_COLORS[t]), new File(itemDir, "tier" + t + "_mana_ore_shard.png"));
            save(createCatalystShard(TIER_COLORS[t]), new File(itemDir, "tier" + t + "_fairy_catalyst_shard.png"));
            save(createMiniTable(t), new File(itemDir, "tier" + t + "_mini_crafting_table.png"));
        }

        // 7. Mana Materials & Compressed Materials
        for (int t = 1; t <= 10; t++) {
            save(createMaterialIngot(TIER_COLORS[t], false), new File(itemDir, "tier" + t + "_mana_material.png"));
            save(createMaterialIngot(TIER_COLORS[t], true), new File(itemDir, "compressed_tier" + t + "_mana_material.png"));
        }
        save(createCompressedMineral(new Color(25, 25, 112), new Color(65, 105, 225)), new File(itemDir, "compressed_lapis.png"));
        save(createCompressedMineral(new Color(75, 0, 130), new Color(186, 85, 211)), new File(itemDir, "compressed_amethyst.png"));
        save(createCompressedMineral(new Color(180, 180, 180), new Color(245, 245, 245)), new File(itemDir, "compressed_quartz.png"));

        // 8. Mana Symbols
        for (int t = 0; t <= 10; t++) {
            save(createManaSphere(TIER_COLORS[t]), new File(itemDir, "mana_symbol_tier" + t + "_mana.png"));
            save(createManaSphere(TIER_COLORS[t]), new File(itemDir, "mana_symbol_tier" + t + ".png"));
        }

        save(createOriginAlloy(false), new File(itemDir, "origin_alloy.png"));
        save(createOriginAlloy(true), new File(itemDir, "compressed_origin_alloy.png"));
        save(createKnowledgeBook(), new File(itemDir, "mana_knowledge.png"));
        save(createKnowledgeBook(), new File(itemDir, "mana_wisdom.png"));

        // 9. Origin Armor & Sword based on vanilla armor/sword shapes with Origin colors
        save(createVanillaStyledArmor(0), new File(itemDir, "origin_helmet.png"));
        save(createVanillaStyledArmor(1), new File(itemDir, "origin_chestplate.png"));
        save(createVanillaStyledArmor(2), new File(itemDir, "origin_leggings.png"));
        save(createVanillaStyledArmor(3), new File(itemDir, "origin_boots.png"));
        save(createVanillaStyledSword(), new File(itemDir, "origin_sword.png"));

        // 10. Blocks: Ores using Vanilla Stone / Obsidian base
        for (int t = 0; t <= 10; t++) {
            save(createVanillaOre(TIER_COLORS[t], false), new File(blockDir, "tier" + t + "_mana_ore.png"));
            save(createVanillaOre(TIER_COLORS[t], true), new File(blockDir, "inactive_tier" + t + "_mana_ore.png"));
        }
        save(createVanillaOre(PURE_COLORS, false), new File(blockDir, "pure_mana_ore.png"));

        // 11. Fairy Blocks (0 to 10) with progressive brightness & compression rings
        save(createFairyBlock(0), new File(blockDir, "fairy_block.png"));
        for (int c = 1; c <= 10; c++) {
            save(createFairyBlock(c), new File(blockDir, "compressed_fairy_block_" + c + ".png"));
        }
        save(createFairyAttractorBlock(), new File(blockDir, "fairy_attractor.png"));

        // 12. Distinct Batch Breaker (Crusher teeth) & Thawer (Frost / Steam chamber)
        save(createBatchBreakerBlock(), new File(blockDir, "batch_ore_breaker.png"));
        save(createThawerBlock(), new File(blockDir, "thawer.png"));

        // 13. Extended Crafting Tables
        for (int t = 1; t <= 10; t++) {
            save(createTableBlock(t), new File(blockDir, "tier" + t + "_extended_crafting_table.png"));
        }

        System.out.println("Textures regenerated successfully!");
    }

    public static BufferedImage createManaSphere(Color[] pal) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        int cx = 8, cy = 8;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 4.8) {
                    if (dist <= 1.6 && x <= 7 && y <= 7) {
                        img.setRGB(x, y, pal[0].getRGB());
                    } else if (x < 7 && y < 7) {
                        img.setRGB(x, y, pal[1].getRGB());
                    } else if (dist <= 3.8) {
                        img.setRGB(x, y, pal[2].getRGB());
                    } else {
                        img.setRGB(x, y, pal[3].getRGB());
                    }
                } else if (dist <= 5.8) {
                    Color aura = new Color(pal[1].getRed(), pal[1].getGreen(), pal[1].getBlue(), 80);
                    img.setRGB(x, y, aura.getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createPureManaSphere() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        int cx = 8, cy = 8;
        Color pureWhite = Color.WHITE;
        Color lightSilver = new Color(245, 248, 250);
        Color rimWhite = new Color(225, 235, 245);
        Color auraWhite = new Color(255, 255, 255, 120);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 4.8) {
                    if (dist <= 2.2 && x <= 8 && y <= 8) {
                        img.setRGB(x, y, pureWhite.getRGB());
                    } else if (dist <= 3.8) {
                        img.setRGB(x, y, lightSilver.getRGB());
                    } else {
                        img.setRGB(x, y, rimWhite.getRGB());
                    }
                } else if (dist <= 5.8) {
                    img.setRGB(x, y, auraWhite.getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createCoreWithPeekingSphere(Color[] pal) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        int cx = 8, cy = 8;

        // 1. Draw large theme sphere in center (radius 5.6)
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 5.6) {
                    if (dist <= 2.2 && x <= 7 && y <= 7) img.setRGB(x, y, pal[0].getRGB());
                    else if (dist <= 4.0) img.setRGB(x, y, pal[1].getRGB());
                    else img.setRGB(x, y, pal[2].getRGB());
                }
            }
        }

        // 2. Overlaid Mystic/Relic Casing (not industrial, but magical rune casing leaving generous openings)
        Color casingLight = new Color(85, 75, 95);
        Color casingDark = new Color(38, 30, 48);
        Color casingGem = new Color(220, 210, 230);

        for (int y = 2; y <= 13; y++) {
            for (int x = 2; x <= 13; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                // Leave a large circular aperture in the center (dist <= 3.8) and wide 2-pixel cross slits
                boolean isOpen = (dist <= 3.8) || (x >= 7 && x <= 8 && y >= 3 && y <= 12) || (y >= 7 && y <= 8 && x >= 3 && x <= 12);

                if (!isOpen) {
                    if (x == 2 || y == 2 || (x == 3 && y == 3)) {
                        img.setRGB(x, y, casingLight.getRGB());
                    } else if (x == 13 || y == 13 || (x == 12 && y == 12)) {
                        img.setRGB(x, y, casingDark.getRGB());
                    } else if ((x == 3 && y == 3) || (x == 12 && y == 3) || (x == 3 && y == 12) || (x == 12 && y == 12)) {
                        img.setRGB(x, y, casingGem.getRGB());
                    } else {
                        img.setRGB(x, y, new Color(55, 45, 68).getRGB());
                    }
                }
            }
        }
        return img;
    }

    public static BufferedImage createRootOfMana() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        int cx = 8, cy = 8;
        // 4 colors spiral: Gold (Star), Cyan (Quantum), Dark Purple (Abyssal), Marine Teal (Marine)
        Color[] roots = {
                new Color(255, 215, 0), new Color(0, 229, 255), new Color(156, 39, 176), new Color(0, 188, 212)
        };

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 5.0) {
                    double angle = Math.atan2(y - cy, x - cx) + Math.PI; // 0 to 2PI
                    int quadrant = (int) ((angle / (Math.PI * 2)) * 4) % 4;
                    if (dist <= 1.5) {
                        img.setRGB(x, y, Color.WHITE.getRGB()); // Converged radiant core
                    } else {
                        img.setRGB(x, y, roots[quadrant].getRGB());
                    }
                } else if (dist <= 6.2) {
                    img.setRGB(x, y, new Color(255, 255, 255, 60).getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createInfiniteMana() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        int cx = 8, cy = 8;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 5.2) {
                    if (dist <= 1.8) {
                        img.setRGB(x, y, new Color(10, 5, 20).getRGB()); // Singularity Black Hole center
                    } else if (dist <= 3.2) {
                        img.setRGB(x, y, Color.WHITE.getRGB()); // Event horizon bright ring
                    } else if (dist <= 4.4) {
                        img.setRGB(x, y, new Color(124, 77, 255).getRGB()); // Nebula Purple
                    } else {
                        img.setRGB(x, y, new Color(0, 229, 255).getRGB()); // Outer Quantum Cyan
                    }
                } else if (dist <= 6.5) {
                    img.setRGB(x, y, new Color(255, 215, 0, 70).getRGB()); // Golden halo
                }
            }
        }
        return img;
    }

    public static BufferedImage createStarMana(boolean uncompressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color core = Color.WHITE;
        Color starGold = uncompressed ? new Color(255, 245, 150) : new Color(255, 215, 0);
        Color darkEdge = new Color(200, 140, 0);

        // Nether Star 4-pointed cross shape
        int cx = 8, cy = 8;
        for (int y = 2; y <= 13; y++) {
            for (int x = 2; x <= 13; x++) {
                int dx = Math.abs(x - cx);
                int dy = Math.abs(y - cy);
                if (dx + dy <= 5 || (dx <= 1 && dy <= 6) || (dy <= 1 && dx <= 6)) {
                    if (dx + dy <= 2) img.setRGB(x, y, core.getRGB());
                    else if (dx + dy <= 4) img.setRGB(x, y, starGold.getRGB());
                    else img.setRGB(x, y, darkEdge.getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createQuantumMana(boolean uncompressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color glass = new Color(200, 240, 255, 160);
        Color magentaCore = uncompressed ? new Color(225, 140, 255) : new Color(213, 0, 249);
        Color cyanCore = new Color(0, 229, 255);

        // End Crystal diamond glass shell
        for (int y = 2; y <= 13; y++) {
            for (int x = 2; x <= 13; x++) {
                int dx = Math.abs(x - 8);
                int dy = Math.abs(y - 8);
                if (dx + dy <= 6) {
                    if (dx + dy == 6 || dx == dy) {
                        img.setRGB(x, y, glass.getRGB()); // Glass frame
                    } else if (dx + dy <= 2) {
                        img.setRGB(x, y, cyanCore.getRGB()); // Inner core
                    } else {
                        img.setRGB(x, y, magentaCore.getRGB());
                    }
                }
            }
        }
        return img;
    }

    public static BufferedImage createAbyssalMana(boolean uncompressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color darkSlate = new Color(11, 25, 33);
        Color sculkCyan = uncompressed ? new Color(100, 245, 230) : new Color(0, 240, 200);
        Color deepBlue = new Color(15, 60, 75);

        // Echo shard / Sculk shape
        for (int y = 2; y <= 13; y++) {
            for (int x = 4; x <= 11; x++) {
                if (Math.abs(x - 7.5) <= (y < 8 ? y * 0.4 : (15 - y) * 0.5)) {
                    if (x == 7 || x == 8) img.setRGB(x, y, sculkCyan.getRGB());
                    else if ((x + y) % 2 == 0) img.setRGB(x, y, deepBlue.getRGB());
                    else img.setRGB(x, y, darkSlate.getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createMarineMana(boolean uncompressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color prismarine = new Color(75, 145, 130);
        Color heartOrange = uncompressed ? new Color(255, 170, 90) : new Color(245, 110, 40);
        Color aqua = new Color(100, 230, 210);

        // Conduit core
        int cx = 8, cy = 8;
        for (int y = 2; y <= 13; y++) {
            for (int x = 2; x <= 13; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist <= 5.0) {
                    if (dist <= 2.2) img.setRGB(x, y, heartOrange.getRGB());
                    else if (dist <= 3.8) img.setRGB(x, y, aqua.getRGB());
                    else img.setRGB(x, y, prismarine.getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createBatchCompressedMana() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // 1/4 size Tier 0 mana in the 4 corners
        Color[] t0 = TIER_COLORS[0];
        int[][] centers = { {4, 4}, {11, 4}, {4, 11}, {11, 11} };

        for (int[] c : centers) {
            int cx = c[0], cy = c[1];
            for (int dy = -3; dy <= 3; dy++) {
                for (int dx = -3; dx <= 3; dx++) {
                    double d = Math.sqrt(dx * dx + dy * dy);
                    if (d <= 2.8) {
                        int px = cx + dx;
                        int py = cy + dy;
                        if (px >= 0 && px < 16 && py >= 0 && py < 16) {
                            if (d <= 1.0 && dx <= 0 && dy <= 0) img.setRGB(px, py, t0[0].getRGB());
                            else if (d <= 2.0) img.setRGB(px, py, t0[1].getRGB());
                            else img.setRGB(px, py, t0[2].getRGB());
                        }
                    }
                }
            }
        }

        // Enchantment glint / shimmer overlays
        Color shimmer = new Color(213, 0, 249, 110);
        Color shimmerLight = new Color(255, 128, 255, 160);
        for (int i = 0; i < 16; i++) {
            if (i < 16) {
                img.setRGB(i, i, shimmer.getRGB());
                if (i + 1 < 16) img.setRGB(i, i + 1, shimmerLight.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createVanillaOre(Color[] pal, boolean inactive) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color[] basePal = inactive ? OBSIDIAN_PAL : STONE_PAL;

        // 1. Render realistic Vanilla Stone / Obsidian background from noise matrix
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int noiseIdx = STONE_NOISE[y][x];
                img.setRGB(x, y, basePal[noiseIdx].getRGB());
            }
        }

        // 2. Add organic mineral veins matching vanilla coal/iron/diamond style
        int[][] veinCoords = {
                {4, 3}, {5, 3}, {4, 4}, {5, 4}, {6, 4},
                {10, 8}, {11, 8}, {10, 9}, {11, 9}, {12, 9}, {11, 10},
                {3, 11}, {4, 11}, {4, 12}, {5, 12}
        };

        for (int[] pt : veinCoords) {
            int px = pt[0], py = pt[1];
            img.setRGB(px, py, pal[1].getRGB());
        }
        // Highlights on top-left of clusters
        img.setRGB(4, 3, pal[0].getRGB());
        img.setRGB(10, 8, pal[0].getRGB());
        img.setRGB(3, 11, pal[0].getRGB());

        // Shadow outlines
        img.setRGB(6, 5, pal[2].getRGB());
        img.setRGB(12, 10, pal[2].getRGB());
        img.setRGB(5, 13, pal[2].getRGB());

        if (inactive) {
            // Steel restraint bands across the inactive ore
            Color band = new Color(75, 75, 90);
            Color highlight = new Color(110, 110, 130);
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, 6, highlight.getRGB());
                img.setRGB(x, 7, band.getRGB());
                img.setRGB(x, 8, band.darker().getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createFairyBlock(int compression) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color mintBase = new Color(0, 200, 110, 180);
        Color mintGlow = new Color(160, 255, 200);
        Color outerFrame = new Color(0, 140, 75);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (x == 0 || x == 15 || y == 0 || y == 15) {
                    img.setRGB(x, y, outerFrame.getRGB());
                } else {
                    double d = Math.sqrt((x - 7.5) * (x - 7.5) + (y - 7.5) * (y - 7.5));
                    if (d <= (2.5 + compression * 0.3)) {
                        img.setRGB(x, y, mintGlow.getRGB()); // Core grows with compression
                    } else {
                        img.setRGB(x, y, mintBase.getRGB());
                    }
                }
            }
        }

        // Distinct rings for higher compression
        if (compression > 0) {
            Color ringColor = (compression >= 7) ? new Color(255, 215, 0) : Color.WHITE;
            for (int i = 3; i <= 12; i++) {
                if (compression % 2 == 0) {
                    img.setRGB(i, 3, ringColor.getRGB());
                    img.setRGB(i, 12, ringColor.getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createBatchBreakerBlock() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color casing = new Color(38, 38, 48);
        Color plate = new Color(55, 55, 68);
        Color teeth = new Color(220, 220, 230);
        Color warning = new Color(255, 170, 0);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, y, plate.getRGB());
                if (x == 0 || x == 15 || y == 0 || y == 15) img.setRGB(x, y, casing.getRGB());
            }
        }

        // Heavy industrial crusher teeth in the center
        for (int x = 3; x <= 12; x++) {
            if (x % 2 == 0) {
                img.setRGB(x, 6, teeth.getRGB());
                img.setRGB(x, 7, teeth.getRGB());
                img.setRGB(x, 8, teeth.darker().getRGB());
                img.setRGB(x, 9, teeth.darker().getRGB());
            }
        }
        // Warning hazard stripes top & bottom
        img.setRGB(4, 2, warning.getRGB());
        img.setRGB(7, 2, warning.getRGB());
        img.setRGB(10, 2, warning.getRGB());
        return img;
    }

    public static BufferedImage createThawerBlock() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color casing = new Color(45, 60, 75);
        Color frostIce = new Color(160, 230, 255);
        Color steamGlow = new Color(255, 240, 200);

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, y, casing.getRGB());
                if (x == 0 || x == 15 || y == 0 || y == 15) img.setRGB(x, y, casing.darker().getRGB());
            }
        }

        // Circular thawing chamber with frost border and glowing heat vent
        int cx = 8, cy = 8;
        for (int y = 3; y <= 12; y++) {
            for (int x = 3; x <= 12; x++) {
                double d = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (d <= 4.0) {
                    if (d <= 2.0) img.setRGB(x, y, steamGlow.getRGB()); // Heat element
                    else img.setRGB(x, y, frostIce.getRGB()); // Ice chamber
                }
            }
        }
        return img;
    }

    public static BufferedImage createVanillaStyledSword() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Canonical Vanilla Sword silhouette (45-degree diagonal)
        // Origin Colors: Blade = Deep Purple -> Star Gold -> Quantum Cyan tip
        Color tip = new Color(0, 240, 255);
        Color blade = new Color(255, 215, 0);
        Color bladeDark = new Color(124, 77, 255);
        Color guard = new Color(230, 180, 0);
        Color grip = new Color(75, 45, 25);
        Color pommel = new Color(255, 215, 0);

        // Blade diagonal from (13, 2) down to (6, 9)
        int[][] bladePts = {
                {13, 2}, {12, 3}, {13, 3},
                {11, 4}, {12, 4}, {10, 5}, {11, 5},
                {9, 6}, {10, 6}, {8, 7}, {9, 7},
                {7, 8}, {8, 8}, {6, 9}, {7, 9}
        };

        for (int[] pt : bladePts) {
            int x = pt[0], y = pt[1];
            if (x >= 12 && y <= 3) img.setRGB(x, y, tip.getRGB());
            else if (x >= 9) img.setRGB(x, y, blade.getRGB());
            else img.setRGB(x, y, bladeDark.getRGB());
        }

        // Crossguard
        img.setRGB(5, 8, guard.getRGB());
        img.setRGB(6, 8, guard.getRGB());
        img.setRGB(7, 10, guard.getRGB());
        img.setRGB(8, 10, guard.getRGB());

        // Grip & Pommel
        img.setRGB(4, 11, grip.getRGB());
        img.setRGB(3, 12, grip.getRGB());
        img.setRGB(2, 13, pommel.getRGB());

        return img;
    }

    public static BufferedImage createVanillaStyledArmor(int type) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color gold = new Color(255, 215, 0);
        Color cosmicViolet = new Color(124, 77, 255);
        Color deepCore = new Color(25, 15, 45);
        Color cyanTrim = new Color(0, 229, 255);

        // Standard vanilla armor icon silhouettes:
        // 0: Helmet, 1: Chestplate, 2: Leggings, 3: Boots
        switch (type) {
            case 0 -> { // Helmet
                for (int y = 2; y <= 9; y++) {
                    for (int x = 4; x <= 11; x++) {
                        if (y <= 7 || x == 4 || x == 11) {
                            if (y == 2 || (x >= 7 && x <= 8 && y == 3)) img.setRGB(x, y, cyanTrim.getRGB());
                            else if (x == 4 || x == 11) img.setRGB(x, y, gold.getRGB());
                            else img.setRGB(x, y, cosmicViolet.getRGB());
                        }
                    }
                }
            }
            case 1 -> { // Chestplate
                for (int y = 2; y <= 13; y++) {
                    for (int x = 2; x <= 13; x++) {
                        // Notch neck (x in 6..9, y==2..3)
                        if (y <= 3 && x >= 6 && x <= 9) continue;
                        if (y <= 7 || (x >= 4 && x <= 11)) {
                            if (x == 7 || x == 8) img.setRGB(x, y, cyanTrim.getRGB());
                            else if (x == 2 || x == 13 || y == 2) img.setRGB(x, y, gold.getRGB());
                            else if (y >= 11) img.setRGB(x, y, deepCore.getRGB());
                            else img.setRGB(x, y, cosmicViolet.getRGB());
                        }
                    }
                }
            }
            case 2 -> { // Leggings
                for (int y = 2; y <= 13; y++) {
                    for (int x = 3; x <= 12; x++) {
                        // Crotch split
                        if (y >= 6 && x >= 7 && x <= 8) continue;
                        if (y == 2) img.setRGB(x, y, gold.getRGB());
                        else if (x == 3 || x == 12) img.setRGB(x, y, cyanTrim.getRGB());
                        else img.setRGB(x, y, cosmicViolet.getRGB());
                    }
                }
            }
            case 3 -> { // Boots
                for (int y = 6; y <= 13; y++) {
                    for (int x = 3; x <= 12; x++) {
                        if (x <= 6 || x >= 9) {
                            if (y == 12 || y == 13) img.setRGB(x, y, gold.getRGB());
                            else if (x == 3 || x == 12) img.setRGB(x, y, cyanTrim.getRGB());
                            else img.setRGB(x, y, cosmicViolet.getRGB());
                        }
                    }
                }
            }
        }
        return img;
    }

    public static BufferedImage createFairyCatalyst(Color[] pal) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color glass = new Color(200, 240, 255, 120);
        Color cap = new Color(230, 200, 120);

        for (int x = 6; x <= 9; x++) {
            img.setRGB(x, 2, cap.getRGB());
            img.setRGB(x, 3, cap.darker().getRGB());
        }
        for (int y = 4; y <= 13; y++) {
            for (int x = 5; x <= 10; x++) {
                if (x == 5 || x == 10 || y == 13) {
                    img.setRGB(x, y, glass.getRGB());
                } else {
                    double d = Math.sqrt((x - 7.5) * (x - 7.5) + (y - 8.5) * (y - 8.5));
                    if (d <= 2.2) {
                        img.setRGB(x, y, pal[1].getRGB());
                    }
                }
            }
        }
        return img;
    }

    public static BufferedImage createFairyResidue() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color c1 = new Color(0, 230, 118);
        Color c2 = new Color(185, 246, 202);
        int[][] pts = { {7, 5}, {8, 6}, {6, 8}, {8, 8}, {9, 9}, {7, 10}, {5, 11}, {10, 11}, {8, 12} };
        for (int[] p : pts) {
            img.setRGB(p[0], p[1], c1.getRGB());
            if (p[0] + 1 < 16) img.setRGB(p[0] + 1, p[1], c2.getRGB());
        }
        return img;
    }

    public static BufferedImage createShard(Color[] pal) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                if (x + y >= 11 && x + y <= 17 && Math.abs(x - y) <= 3) {
                    img.setRGB(x, y, pal[1].getRGB());
                }
            }
        }
        return img;
    }

    public static BufferedImage createCatalystShard(Color[] pal) {
        return createShard(pal);
    }

    public static BufferedImage createMiniTable(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color wood = new Color(160, 110, 60);
        Color top = new Color(190, 140, 80);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, (y == 4 || y == 5) ? top.getRGB() : wood.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createMaterialIngot(Color[] pal, boolean compressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 5; y <= 10; y++) {
            for (int x = 3; x <= 12; x++) {
                if (y == 5) img.setRGB(x, y, pal[0].getRGB());
                else if (y == 10 || x == 3 || x == 12) img.setRGB(x, y, pal[3].getRGB());
                else img.setRGB(x, y, compressed ? pal[0].getRGB() : pal[1].getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createCompressedMineral(Color dark, Color light) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, ((x + y) % 2 == 0) ? light.getRGB() : dark.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createOriginAlloy(boolean compressed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color c = compressed ? new Color(255, 215, 0) : new Color(124, 77, 255);
        for (int y = 4; y <= 11; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, c.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createKnowledgeBook() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color cover = new Color(124, 77, 255);
        Color page = new Color(245, 240, 220);
        for (int y = 3; y <= 12; y++) {
            for (int x = 4; x <= 11; x++) {
                img.setRGB(x, y, (x == 4 || x == 11 || y == 3 || y == 12) ? cover.getRGB() : page.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createFairyAttractorBlock() {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color iron = new Color(140, 145, 150);
        Color core = new Color(0, 230, 118);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                img.setRGB(x, y, (x >= 5 && x <= 10 && y >= 5 && y <= 10) ? core.getRGB() : iron.getRGB());
            }
        }
        return img;
    }

    public static BufferedImage createTableBlock(int tier) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Color wood = new Color(140, 95, 55);
        Color rune = (tier <= 10) ? TIER_COLORS[Math.min(10, tier)][1] : Color.WHITE;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if ((x == 7 || x == 8 || y == 7 || y == 8) && (x >= 3 && x <= 12 && y >= 3 && y <= 12)) {
                    img.setRGB(x, y, rune.getRGB());
                } else {
                    img.setRGB(x, y, wood.getRGB());
                }
            }
        }
        return img;
    }

    private static void save(BufferedImage img, File file) throws IOException {
        ImageIO.write(img, "PNG", file);
    }
}
