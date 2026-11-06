# Exponential Mana

[English](#english) | [日本語](#japanese)

---

<a name="english"></a>
## English

### Overview
**Exponential Mana** is an endgame inflation Minecraft Forge mod for 1.20.1 featuring exponential resource demands, extreme automation, and compression technology. Face astronomical material requirements, establish passive production lines using Fairies, deploy lag-free Batch Ore Breakers, and craft across massive 13×13 crafting tables (169 slots) to push Minecraft mechanics to their limit (`Int_MAX`).

### Key Features
- **Tiered Mana Progression (Tier 0 - 10)**: 
  - Natural generation of Tier 0 Mana Ore.
  - Odd Tiers upgrade directly, while even Tiers require decomposing previous tier equipment into 4 Shards.
- **Fairies & Passive Automation**:
  - Insert Fairy Catalyst (non-consumed) into Fairy Blocks for automated mana production every 60 seconds.
  - 9x compression up to $9^{10}$ (~3.48 billion times) without increasing tick lag.
  - Catalyst slot holds up to `Integer.MAX_VALUE` (2,147,483,647) items.
- **Inactive Ore & Batch Processing**:
  - Inactive Mana Ores drop with obsidian-level hardness. Automatically activates after 5 minutes in the world.
  - Mining restricted to players (immune to automated block breakers).
  - **Batch Ore Breaker**: 10×10 inventory (each slot `Int_MAX`) handling virtual ore activation and cooldowns. Drops as compressed NBT on block break.
  - **Thawer**: Safely uncompresses and extracts batch mana.
- **Endgame Crafting (13×13)**:
  - Extended Crafting Tables from 4×4 up to 13×13 (169 slots), compatible with AE2 pattern providers without energy cost.
  - Mana Alloys, Origin Alloy, Wisdom of Mana, and 4 Root Manas (Star, Quantum, Abyssal, Marine).
  - **Origin Equipment**: Creative flight, absolute invulnerability, and instant-kill attacks.
  - **Infinite Mana**: Duplicates any single non-NBT item stack into 64 items.
- **KubeJS Integration**:
  - Full support for adding, removing, and modifying Tier 1-10 Extended Crafting recipes via JavaScript.
  - Custom Fairy Catalyst and Custom Ore registration APIs. See [KUBEJS.md](KUBEJS.md) for detailed syntax.

### Installation
1. Install **Minecraft Forge 1.20.1** (Recommended 47.3.0+).
2. Place the `exponential_mana-<version>.jar` into your Minecraft `.minecraft/mods` directory.
3. (Optional) Install **Applied Energistics 2** for automated crafting integration.

### License
This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

---

<a name="japanese"></a>
## 日本語

### 概要
『**Exponential Mana**』は、指数関数的（Exponential）に膨れ上がる要求素材を、極限の自動化と圧縮技術で解決していくインフレ系エンドコンテンツMODです（Minecraft 1.20.1 Forge対応）。手作業では到達不可能な天文学的素材量を前に、「妖精」による不労所得ラインや「鉱石一括破壊機」によるサーバー負荷を抑えた大規模処理システムを構築し、最終的には13×13（169マス）の巨大作業台を駆使してマイクラのシステム上限（`Int_MAX`）に挑みます。

### 主な特徴
- **マナの階層化 (Tier 0 〜 10)**:
  - 自然生成される「Tier0マナ鉱石」から採掘スタート。
  - 奇数Tierは前段階を直接強化、偶数Tier移行時は前Tier設備を「欠片×4」へ単体分解するパズル要素。
- **妖精ブロックと無限の生産力**:
  - 妖精ブロックに「妖精活発化触媒（消費なし）」をセットし、1分ごとにマナを自動生産。
  - 9個圧縮で上位の「圧縮妖精」へ（最大$9^{10}$、約34億倍まで圧縮可能）。クールダウン1分のまま生産量9倍。
  - 触媒スロットは上限撤廃（1スロット`Int_MAX`個まで投入可能）。
- **非活性鉱石と一括処理**:
  - 高Tier採掘時に確定ドロップする非常に硬い「非活性マナ鉱石」。ワールド設置後5分で活性化。
  - プレイヤー採掘縛り（自動採掘機無効、誤設置撤去時はタイマーリセット）。
  - **鉱石一括破壊機**: 内部10×10スロット（各スロット`Int_MAX`）の仮想処理マシン。撤去時は中身を保持した圧縮NBTアイテムとして安全にドロップ。
  - **解凍機**: 圧縮マナから通常アイテム状態へ安全に抽出・搬出。
- **エンドゲーム (13×13 拡張作業台)**:
  - 4×4から13×13（169マス）に至る拡張作業台。AE2自動クラフト完全対応・エネルギー消費なし。
  - マナ素材圧縮、無垢のマナ、オリジン合金、マナの叡智、4つの根源マナ（星・量子・深淵・海洋）。
  - **根源装備**: クリエイティブ飛行、完全無敵（`/kill` 無効）、即死攻撃。
  - **無限のマナ**: NBT無しの任意アイテムを1スタック（64個）に複製。
- **KubeJS 連携**:
  - JavaScriptからTier 1〜10拡張作業台レシピの追加・削除・素材置換に完全対応。
  - 任意のカスタム妖精触媒やカスタム鉱石を登録できるAPIを提供。詳細は [KUBEJS.md](KUBEJS.md) を参照。

### 導入手順
1. **Minecraft Forge 1.20.1** (47.3.0以上推奨) を導入します。
2. ビルドされた `exponential_mana-<version>.jar` を `.minecraft/mods` フォルダに配置します。
3. （任意）自動クラフト連携のため **Applied Energistics 2** などの導入を推奨します。

### ライセンス
本プロジェクトは **GNU General Public License v3.0 (GPL-3.0)** のもとで公開されています。
