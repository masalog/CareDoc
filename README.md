# CareDoc

CareDoc は、介護保険の「要介護認定・要支援認定 申請書」を Excel 上のデータから<br>
PDF に転記して作成するためのデスクトップアプリです。  
技術は、Kotlin + JavaFX + PDFBox を使用しています。

## ✨ 主な機能
- プルダウンから名前を選択すると、申請書を作成
- Excel シートに保存されたデータを PDF 画面に転記
- Excel シートをデータベースの代わりとして使用可
- デスクトップアプリとしてZip配布が可能
- Windows 11 対応

## 📂 プロジェクト構成図
```text
src/
└── main/
    ├── kotlin/
    │   └── org/
    │       └── example/
    │           └── pdfConverter/
    │               ├── Launcher.kt
    │               ├── controller/ 
    │               │   ├── PdfViewerController.kt
    │               │   ├── PdfViewerControllerFactory.kt    
    │               │   └── PdfViewerEventBinder.kt
    │               ├── factory/ 
    │               │   └── PdfViewerFactory.kt        
    │               ├── model/
    │               │   ├── CommonData.kt
    │               │   ├── Member.kt
    │               │   ├── InitialData.kt
    │               │   ├── PdfLayout.kt
    │               │   └── FieldPosition.kt
    │               ├── render/
    │               │   ├── PdfDisplayController.kt
    │               │   ├── PdfRenderManager.kt
    │               │   ├── RenderExecutor.kt
    │               │   └── RenderJobManager.kt
    │               ├── repository/
    │               │   ├── PdfRepository.kt
    │               │   ├── ExcelLoader.kt
    │               │   └── YamlLoader.kt
    │               ├── service/
    │               │   ├── ErrorHandler.kt
    │               │   ├── ErrorHandlerImpl.kt
    │               │   ├── PdfEditor.kt
    │               │   ├── PdfLoader.kt
    │               │   └── PdfViewerInitializer.kt
    │               ├── util/
    │               │   ├── PdfPositionConverter.kt
    │               │   └── PdfSizeChecker.kt
    │               ├── view/
    │               │   ├── PdfViewer.kt
    │               │   ├── PdfViewerUI.kt
    │               │   ├── PdfViewerView.kt
    │               │   └── PdfViewerViewFactory.kt
    │               └── viewModel/
    │                   ├── PdfUpdateViewModel.kt
    │                   └── DateInputViewModel.kt
    ├── resources/
    │   ├── fonts/
    │   │   └── NotoSansJP-Regular.ttf
    │   ├── positions/
    │   │   ├── converted_positions.yaml
    │   │   └── raw_positions.yaml
    │   └── templates/
    │       └── template.pdf
    test/
    └── kotlin/
        └── org/
            └── example/
                └── pdfConverter/
                    ├── controller/ 
                    │   ├── PdfViewerControllerTest.kt
                    │   └── PdfViewerEventBinderTest.kt
                    ├── testUtil/   
                    │   └── TestData.kt
                    └── util     
```

## 🛠 主要技術
- Kotlin
- Java 21
- JavaFX
- Apache PDFBox 2.0.30
- Apache Poi 5.5.1
- IntelliJ IDEA Communitiy Edition 2026.1.3
- Maven 4.0.0

## 🚀 使い方
1. リリースページから、本アプリのベータ版をダウンロード 
2. 展開して、CareDoc.exe をダブルクリックすると、申請書作成画面が表示
3.「名前を選択してください」ボタンを押して、印刷文字を表示
4.「申請年月日」ボタンから指定
5. 必要があれば、「変更更新理由」を入力
6.「保存」ボタンを押して PDF を取得
7. members.xlsx の「個別」シートを通じて、利用者のデータを編集可能
8. members.xlsx の「共通」シートを通じて、担当者のデータを編集可能

## 🔮 今後の予定
- PDF 読み込みの高速化
- 入力バリデーション
- テストコードの追加

## 📄 テンプレート
- 入力PDF:`template.pdf`
- 編集PDF:`edited.pdf`
- 出力PDF:`output.pdf`
- 変換前座標YAML:`raw_positions.yaml`
- 変換後座標YAML:`converted_positions.yaml`
- データストアExcel:`members.xlsx`

## 🏗 ビルド方法
※ 以下のコマンドはプロジェクトのルートディレクトリ（プロジェクト直下フォルダ）で実行  
※ JAVA_HOME には、Java21 JDK のパス設定が必要です
※ JavaFXのHPより、Java21 の Windows x86_64 に対応した jmods をインストールして配置

```powershell
mvn clean package

jlink `
  --module-path "$env:JAVA_HOME\jmods;C:\javafx-jmods-21" `
  --add-modules java.base,java.desktop,java.logging,java.xml,javafx.base,javafx.graphics,javafx.controls,javafx.fxml,javafx.swing `
  --strip-debug `
  --compress=2 `
  --no-header-files `
  --no-man-pages `
  --output runtime

jpackage `
  --type app-image `
  --input target `
  --main-jar PdfConverter-1.0.0.jar `
  --main-class org.example.pdfConverter.Launcher `
  --name CareDoc `
  --runtime-image runtime
```

### 実行
CareDoc フォルダに members.xlsx を配置し、プロジェクトルートで以下を実行します：

```powershell
cd CareDoc
.\CareDoc.exe
```

### 配布
※ プロジェクトのルートディレクトリに戻って実行

```powershell
cd ..
Compress-Archive CareDoc CareDoc.zip -Force
```

## 🧑‍💻 留意点
本テンプレートは、東京都中央区が公開している介護認定申請書の様式を参考に作成したものです。
正式な手続きの際には、中央区が提供する最新の書式をご使用くださいますようお願いいたします。
なお、本書類の利用により生じた損害等について、作成者は一切の責任を負いません。
