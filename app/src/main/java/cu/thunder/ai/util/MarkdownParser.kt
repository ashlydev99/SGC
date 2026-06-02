package cu.thunder.ai.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cu.thunder.ai.ui.theme.*

@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val blocks = parseMarkdown(content)

    Column(modifier = modifier) {
        for (block in blocks) {
            when (block) {
                is MarkdownBlock.Code -> {
                    CodeBlock(block) {
                        clipboardManager.setText(AnnotatedString(block.code))
                    }
                }
                is MarkdownBlock.Bold -> {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(block.text)
                            }
                        },
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is MarkdownBlock.Italic -> {
                    Text(
                        text = block.text,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
                is MarkdownBlock.ListBlock -> {
                    for (item in block.items) {
                        Text(
                            text = "• $item",
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                is MarkdownBlock.Text -> {
                    Text(
                        text = block.text,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeBlock(
    block: MarkdownBlock.Code,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBlockBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = block.language ?: "codigo",
                color = ElectricBlue,
                style = MaterialTheme.typography.labelSmall
            )
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copiar",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = block.code,
            color = TextPrimary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private sealed class MarkdownBlock {
    data class Text(val text: String) : MarkdownBlock()
    data class Bold(val text: String) : MarkdownBlock()
    data class Italic(val text: String) : MarkdownBlock()
    data class Code(val code: String, val language: String?) : MarkdownBlock()
    data class ListBlock(val items: kotlin.collections.List<String>) : MarkdownBlock()
}

private fun parseMarkdown(content: String): kotlin.collections.List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = content.split("\n")
    var i = 0
    val listItems = mutableListOf<String>()

    while (i < lines.size) {
        val line = lines[i]

        when {
            line.startsWith("```") -> {
                if (listItems.isNotEmpty()) {
                    blocks.add(MarkdownBlock.ListBlock(listItems.toList()))
                    listItems.clear()
                }
                
                val language = line.removePrefix("```").trim()
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                blocks.add(MarkdownBlock.Code(codeLines.joinToString("\n"), language.takeIf { it.isNotEmpty() }))
            }
            line.startsWith("**") && line.endsWith("**") -> {
                if (listItems.isNotEmpty()) {
                    blocks.add(MarkdownBlock.ListBlock(listItems.toList()))
                    listItems.clear()
                }
                blocks.add(MarkdownBlock.Bold(line.removeSurrounding("**")))
            }
            line.startsWith("*") && !line.startsWith("**") -> {
                if (listItems.isNotEmpty()) {
                    blocks.add(MarkdownBlock.ListBlock(listItems.toList()))
                    listItems.clear()
                }
                blocks.add(MarkdownBlock.Italic(line.removeSurrounding("*")))
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                listItems.add(line.removePrefix("- ").removePrefix("* "))
            }
            else -> {
                if (listItems.isNotEmpty() && line.isBlank()) {
                    blocks.add(MarkdownBlock.ListBlock(listItems.toList()))
                    listItems.clear()
                } else if (line.isNotBlank()) {
                    blocks.add(MarkdownBlock.Text(line))
                }
            }
        }
        i++
    }

    if (listItems.isNotEmpty()) {
        blocks.add(MarkdownBlock.ListBlock(listItems.toList()))
    }

    return blocks
}