# Changelog / 変更履歴

All notable changes to this project will be documented in this file.
本プロジェクトのすべての注目すべき変更はこのファイルに記録されます。

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

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
