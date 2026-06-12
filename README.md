# CareDoc

CareDoc は、介護保険の「要介護認定・要支援認定 申請書」をプルダウンメニューから作成するためのデスクトップアプリです。  
Compose Desktop + Kotlin + Apache POI を使用し、Excel テンプレートに自動入力します。

## ✨ 主な機能
- 必要な入力項目をプルダウンから選択
- 「リスト」シートに保存されたデータからプルダウンを生成
- デスクトップアプリとして配布が可能
- UI 入力に対応（カレンダーから日時選択など）
- Windows / macOS 対応

## 📂 プロジェクト構成
```
desktopApp/
 ├─ build.gradle.kts
 └─ src/
      └─ main/
           ├─ kotlin/
           │    └─ org/
           │         └─ example/
           │              └─ caredoc/
           │                   └─ main.kt
           └─ resources/
```

## 🛠 使用技術
- Kotlin / Compose Desktop
- Gradle（Kotlin DSL）
- Apache POI（Excel 操作）

## 🚀 使い方
1. CareDoc を起動すると、申請書作成画面が表示
2. 入力項目を編集
3. 「申請書を保存」ボタンを押して Excel を出力

## 📄 Excel テンプレート
- ファイル：`介護認定申請書.xlsx`
- メインシート名：介護認定申請書
- リストシート名：リスト

## 🔮 今後の予定
- プルダウンメニューの追加

## 🧑‍💻 開発者
masalog
