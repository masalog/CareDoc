# CareDoc

CareDoc は、介護保険の「要介護認定・要支援認定 申請書」をプルダウンから作成するための
デスクトップアプリです。  
Compose Desktop + Kotlin + Apache POI を使用し、PDF テンプレートに自動入力します。

## ✨ 主な機能
- 必要な入力項目をプルダウンから選択
- 「リスト」シートに保存されたデータからプルダウンを生成
- デスクトップアプリとして配布が可能
- UI 入力に対応（カレンダーから日時選択など）
- Windows / macOS 対応

## 📂 プロジェクト構成
```
desktopApp
├── build
├── src
│   └── main
│       ├── kotlin
│       │   └── org
│       │       └── example
│       │           └── caredoc
│       │               ├── model
│       │               ├── pdf
│       │               ├── ui
│       │               ├── utility
│       │               └── main.kt
│       └── resources
│           ├── fonts
│           └── templates
├── build.gradle.kts
└── output.pdf

```

## 🛠 使用技術
- Kotlin / Compose Desktop
- Gradle（Kotlin DSL）
- Apache POI（Excel 操作）

## 🚀 使い方
1. CareDoc を起動すると、申請書作成画面が表示
2. 入力項目を編集
3. 「申請書を保存」ボタンを押して PDF を出力

## 📄 テンプレート
- 入力ファイル名：`template.pdf`
- リストファイル名：`data_list.xlsx`
- 出力ファイル名：`介護認定申請書.pdf`


## 🔮 今後の予定
- プルダウンメニューの追加

## 🧑‍💻 留意点
本アプリは、東京都中央区が公開している
「介護保険 要介護・要支援認定申請書」を基に作成しています。
本アプリは中央区の公式アプリではありません。

