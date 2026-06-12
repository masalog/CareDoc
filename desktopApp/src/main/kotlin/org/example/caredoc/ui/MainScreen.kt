package org.example.caredoc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.caredoc.pdf.exportPdf
import org.example.caredoc.pdf.loadPdfPageAsImage
import org.example.caredoc.model.FieldSelection

@Composable
fun MainScreen() {

    // ★ 入力すべき座標リスト（必要に応じて増やせる）
    val fields = listOf(
        FieldSelection(100f, 700f, ""),  // プルダウン1の座標
        FieldSelection(100f, 650f, ""),  // プルダウン2の座標
        FieldSelection(100f, 600f, "")   // プルダウン3の座標
    )

    // ★ 今どのプルダウンを表示しているか
    var currentIndex by remember { mutableStateOf(0) }

    // ★ 選択結果を保存するリスト
    var selections by remember { mutableStateOf(listOf<FieldSelection>()) }

    // PDF 読み込み
    val pdfImage = remember {
        loadPdfPageAsImage("/templates/template.pdf", 0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // PDF 表示
        PdfViewer(
            image = pdfImage,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(16.dp))

        // ★ 現在のプルダウンを表示
        if (currentIndex < fields.size) {

            Text("項目 ${currentIndex + 1} を選択してください")

            PdfDropdown { value ->

                // ★ 選択内容を座標とセットで保存
                val field = fields[currentIndex]
                selections = selections + FieldSelection(
                    x = field.x,
                    y = field.y,
                    value = value
                )

                // 次のプルダウンへ
                currentIndex++
            }

        } else {
            // ★ 全部選び終わったら PDF 出力ボタンを表示
            Button(
                onClick = { exportPdf(selections) },
                enabled = selections.isNotEmpty()
            ) {
                Text("PDF を出力")
            }
        }
    }
}
