# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトのすべての注目すべき変更はこのファイルに記録されます。

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.5.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented Extended Crafting Table blocks from Tier 1 (4×4) up to Tier 10 (13×13, 169 slots).
  - Implemented `ExtendedCraftingTableBlockEntity` supporting energy-free automatic crafting with AE2 Pattern Provider compatibility (`IItemHandler`).
  - Added Tier 1 crafting recipes (Tier 1 Mana, Core, Ore, Catalyst, and 4×4 Crafting Table).
  - Implemented Even-Tier deconstruction recipes (breaking down Tier 1, 3, 5, 7, 9 ores, catalysts, and tables into 4 shards).
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - Tier 1（4×4）から Tier 10（13×13、169スロット）に至る拡張作業台ブロックを実装。
  - AE2のパターンプロバイダー等の自動クラフトにデフォルト対応（`IItemHandler`公開・エネルギー消費なし）する `ExtendedCraftingTableBlockEntity` を実装。
  - Tier 1の各種進化クラフトレシピ（Tier1マナ、コア、鉱石、触媒、4×4拡張作業台）を追加。
  - 偶数Tier移行用の設備分解レシピ（Tier 1, 3, 5, 7, 9の鉱石・触媒・作業台を単体クラフトで欠片×4に分解）を実装。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.4.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented Batch Ore Breaker (`batch_ore_breaker`) with 10×10 internal slots (`Int_MAX` capacity per slot) and virtual timer processing.
  - Implemented safe unscattered drop mechanism: drops NBT-encoded Batch Compressed Mana (`batch_compressed_mana`) on breaker dismantle.
  - Implemented Thawer (`thawer`) to safely extract and output real item-form mana from Batch Compressed Mana.
  - Added recipe for Batch Ore Breaker (8 Mana Ores) and Thawer.
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - 10×10（100スロット、各スロット`Int_MAX`容量）の内部インベントリと仮想タイマー処理を備えた鉱石一括破壊機（`batch_ore_breaker`）を実装。
  - ワールド撤去時にアイテムを散乱させず、内部データを焼き込んだ一括圧縮状態のマナ（`batch_compressed_mana`）として安全にドロップする機構を実装。
  - 一括圧縮状態のマナから通常アイテム状態のマナを安全に解凍・抽出する解凍機（`thawer`）を実装。
  - 鉱石一括破壊機（マナ鉱石×8）および解凍機のクラフトレシピを追加。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.3.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Added Inactive Tier 0 Mana Ore (`inactive_tier0_mana_ore`).
  - Updated Tier 0 Mana Ore to drop both Tier 0 Mana and Inactive Tier 0 Mana Ore upon mining.
  - Implemented Fairy Block, Compressed Fairy Blocks (Tier 1 to 10: $9^1$ to $9^{10}$), and Fairy Attractor.
  - Implemented `FairyBlockEntity` with 1-minute production timer, non-consumed catalyst slot with `Int_MAX` capacity.
  - Allowed external tick acceleration on Fairy Blocks.
  - Implemented Fairy Residue crafting logic (returns Fairy Block unconsumed).
  - Implemented Fairy 9x compression crafting recipes.
  - Added GUI compact stack formatter (`1.5K`, `20M`, `2.1B`) with exact count tooltips.
  - Synchronized bilingual translations in `en_us.json` and `ja_jp.json`.
- **JA**:
  - 非活性Tier0 マナ鉱石（`inactive_tier0_mana_ore`）を追加。
  - Tier0 マナ鉱石の採掘時に「Tier0 マナ」と「非活性Tier0 マナ鉱石」の双方がドロップするよう更新。
  - 妖精ブロック、圧縮妖精ブロック（Tier 1〜10: $9^1$〜$9^{10}$）、および妖精誘引機を実装。
  - 1分間の自動生産タイマー、消費なしの触媒スロット（`Int_MAX` 容量対応）を備えた `FairyBlockEntity` を実装。
  - 妖精ブロックに対する外部MODのTick加速を意図的に許容する設計を適用。
  - 妖精ブロックを消費せず「妖精の残滓」を抽出する単体クラフトロジックを実装。
  - 妖精ブロックの9圧縮クラフトレシピを実装。
  - GUIスロットの大容量スタック表示フォーマッタ（`1.5K`, `20M`, `2.1B`）および正確な数値ツールチップを実装。
  - 日英言語ファイル（`en_us.json`, `ja_jp.json`）を完全同期更新。

## [0.2.0-alpha] - 2026-11-03

### Added / 追加
- **EN**:
  - Implemented core registry systems (`ModCreativeModeTabs`, `ModItems`, `ModBlocks`).
  - Added Tier 0 to 10 Mana items and Core items.
  - Added Fairy Residue, Tier 1 to 10 Fairy Catalysts, and Pure Fairy Catalyst.
  - Added Shard items for even-tier transitions (Ore Shards, Catalyst Shards, Mini Crafting Tables for Tiers 1, 3, 5, 7, 9).
  - Added Tier 0 to 10 Mana Ore blocks, Pure Mana Ore, and Inactive Mana Ore blocks (Tier 1 to 10).
  - Implemented custom `InactiveManaOreBlock` with 5-minute activation timer and obsidian-grade resistance.
  - Full bilingual localization sync for all items and blocks in `en_us.json` and `ja_jp.json`.
- **JA**:
  - コアレジストリシステム（`ModCreativeModeTabs`, `ModItems`, `ModBlocks`）を実装。
  - Tier 0〜10のマナアイテムおよびコアアイテムを追加。
  - 妖精の残滓、Tier 1〜10の妖精活発化触媒、無垢の妖精活発化触媒を追加。
  - 偶数Tier移行用の各種欠片アイテム（Tier 1, 3, 5, 7, 9の鉱石の欠片、触媒の欠片、ミニ拡張作業台）を追加。
  - Tier 0〜10のマナ鉱石ブロック、無垢のマナ鉱石、およびTier 1〜10の非活性マナ鉱石ブロックを追加。
  - 5分間の活性化タイマーと黒曜石同等の耐久度・耐爆性を持つカスタムブロック `InactiveManaOreBlock` を実装。
  - すべてのアイテム・ブロックに対応する日英言語ファイル（`en_us.json`, `ja_jp.json`）を同期更新。

## [0.1.0-alpha] - 2026-11-03

### Added / 追加
- **EN**: Initial project foundation for Minecraft Forge 1.20.1.
  - Setup ForgeGradle 6.x build configuration targeting Java 17 with UTF-8 encoding.
  - Project configuration: ModID `exponential_mana`, GPL-3.0 license.
  - Bilingual documentation (README.md, CHANGELOG.md, CONFIG.md).
- **JA**: Minecraft Forge 1.20.1 向け初期プロジェクト基盤を構築。
  - Java 17ターゲットおよびUTF-8エンコーディングを明示したForgeGradle 6.xビルド構成を整備。
  - プロジェクト構成設定: ModID `exponential_mana`、GPL-3.0ライセンス。
  - 日英バイリンガルドキュメント (README.md, CHANGELOG.md, CONFIG.md) を整備。
