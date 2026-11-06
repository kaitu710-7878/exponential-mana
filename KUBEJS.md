# KubeJS Integration Guide / KubeJS 連携ガイド

Exponential Mana provides full integration with KubeJS 6 on Minecraft 1.20.1 Forge.
Exponential Mana は Minecraft 1.20.1 Forge 環境における KubeJS 6 との完全な連携機能を提供します。

---

## 1. Extended Crafting Recipes (拡張作業台レシピ)

Location: `kubejs/server_scripts/`

### Direct Tier Builders (各Tier専用ビルダー)
You can define recipes for Tier 1 (4x4) through Tier 10 (13x13) extended crafting tables using intuitive shaped syntax identical to vanilla shaped crafting:
バニラの作業台（shaped）レシピと全く同じ直感的な記法で、Tier 1（4×4）から Tier 10（13×13）までの拡張作業台レシピを定義できます：

```javascript
ServerEvents.recipes(event => {
    // Tier 1 Extended Crafting Table (4x4 grid)
    event.recipes.exponential_mana.tier1('minecraft:nether_star', [
        'DDDD',
        'DSSD',
        'DSSD',
        'DDDD'
    ], {
        D: 'minecraft:diamond',
        S: 'exponential_mana:tier1_mana'
    })

    // Tier 2 Extended Crafting Table (5x5 grid)
    event.recipes.exponential_mana.tier2('minecraft:dragon_egg', [
        '.....',
        '.OOO.',
        '.OEO.',
        '.OOO.',
        '.....'
    ], {
        O: 'minecraft:obsidian',
        E: 'minecraft:ender_pearl'
    })

    // Up to Tier 10 Extended Crafting Table (13x13 grid)
    // event.recipes.exponential_mana.tier10('output_item', [ 13 pattern rows ], { keys... })
})
```

### Full Grid Compression (全面圧縮レシピ)
```javascript
ServerEvents.recipes(event => {
    // Fill the entire Tier 4 (7x7, 49 slots) grid with raw iron to forge a massive block
    // Tier 4（7×7、49スロット）の全グリッドを鉄インゴットで埋め尽くして圧縮作成
    event.recipes.exponential_mana.full_grid('minecraft:iron_block', 4, 'minecraft:iron_ingot')
})
```

### Shapeless Crafting (不定形レシピ)
```javascript
ServerEvents.recipes(event => {
    // Tier 3 Table (6x6) shapeless recipe
    event.recipes.exponential_mana.shapeless('minecraft:beacon', 3, [
        'minecraft:nether_star',
        '3x minecraft:obsidian',
        '5x minecraft:glass'
    ])
})
```

### Removing & Modifying Recipes (レシピの削除・素材置換)
```javascript
ServerEvents.recipes(event => {
    // Remove recipe by ID or Output
    event.remove({ type: 'exponential_mana:extended_crafting', output: 'exponential_mana:pure_mana' })

    // Replace inputs across all extended crafting recipes
    event.replaceInput({ type: 'exponential_mana:extended_crafting' }, 'minecraft:coal', 'minecraft:charcoal')
})
```

---

## 2. Custom Fairy Catalysts & Custom Ores (カスタム触媒・鉱石登録)

Location: `kubejs/server_scripts/` or `kubejs/startup_scripts/`

```javascript
// Register custom fairy catalyst: When placed in Fairy Block, produces target mana
// カスタム妖精触媒の登録: 妖精ブロックに設置すると指定のマナを生産します
ExponentialMana.registerCatalyst('minecraft:dragon_breath', 'exponential_mana:tier10_mana')

// Register custom ore for Batch Ore Breaker (Active Ore, Inactive Ore, Mana Tier 0-11)
// 一括破壊機へのカスタム鉱石登録 (活性鉱石, 非活性鉱石, 対応するマナのTier 0〜11)
ExponentialMana.registerOre('minecraft:ancient_debris', 'minecraft:netherrack', 10)
```

---

## 3. Global Constants (グローバル定数)

You can reference Exponential Mana items and blocks directly using `EMItems` and `EMBlocks`:
`EMItems` および `EMBlocks` を通じて、MOD内のアイテムやブロックの参照にアクセスできます：

```javascript
ServerEvents.recipes(event => {
    event.shaped(EMItems.TIER1_CORE.get(), [
        'MMM',
        'MCM',
        'MMM'
    ], {
        M: EMItems.TIER0_MANA.get(),
        C: 'minecraft:coal'
    })
})
```
