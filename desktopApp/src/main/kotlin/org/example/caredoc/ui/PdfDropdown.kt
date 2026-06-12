package org.example.caredoc.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.*

@Composable
fun PdfDropdown(onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("選択してください") }

    val options = listOf("A", "B", "C")

    Box {
        Button(onClick = { expanded = true }) {
            Text(selected)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selected = option
                        expanded = false
                        onSelected(option)
                    }
                ) {
                    Text(option)
                }
            }
        }
    }
}
