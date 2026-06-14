# CareDoc

CareDoc は、介護保険の「要介護認定・要支援認定 申請書」をプルダウンメニューから作成するための<br>
デスクトップアプリです。  
Kotlin + JavaFX + PDFBox を使用し、PDF テンプレートに自動入力します。

## ✨ 主な機能
- 必要な入力項目をプルダウンから選択
- Excel シートに保存されたデータを画面に表示
- デスクトップアプリとして配布が可能
- PDF を編集してダウンロードできる
- Windows / macOS 対応

## 📂 プロジェクト構成図
```text
src/
└── main/
    ├── kotlin/
    │   └── org/
    │       └── example/
    │           └── pdfconverter/
    │               ├── PdfViewer.kt
    │               ├── YamlLoader.kt
    │               ├── ExcelLoader.kt
    │               ├── Launcher.kt
    │               └── utility/
    │                   ├── PdfPositionConverter.kt
    │                   └── PdfSizeChecker.kt
    └── resources/
        ├── fonts/
        ├── positions/
        │   ├── converted_positions.yaml
        │   └── raw_positions.yaml
        └── templates/
```

## 🛠 使用技術
- Kotlin / JavaFX
- Maven
- PDFBox

## 🚀 使い方
1. CareDoc を起動すると、申請書作成画面が表示
2. 「名前を選択してください」ボタンを押して、画面に印刷文字を表示
3. 「出力」ボタンを押して PDF を出力

## 🔮 今後の予定
- プルダウンメニューの追加

## 📄 テンプレート
- 入力用PDF:`template.pdf`
- 編集用PDF:`edited.pdf`
- 出力用PDF:`output.pdf`
- 画像座標YAML:`raw_positions.yaml`
- PDF座標YAML:`converted_positions.yaml`
- データストアExcel:`members.xlsx`

## 🧑‍💻 留意点
本テンプレートは、東京都中央区が公開している介護認定申請書の様式を参考に、学習目的で作成したものです。
正式な手続きの際には、中央区が提供する最新の書式をご使用くださいますようお願いいたします。
なお、本書類の利用により生じた損害等について、作成者は一切の責任を負いません。
