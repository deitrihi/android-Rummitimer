// 콘텐츠가 가용 영역보다 크면 비율을 유지한 채 자동으로 축소해 화면에 맞추는 래퍼
package com.deitrihi.rummitimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

@Composable
fun AutoFitContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier) { constraints ->
        val placeable = subcompose(Unit, content).first().measure(Constraints())

        val scale = if (placeable.width > 0 && placeable.height > 0) {
            minOf(
                1f,
                constraints.maxWidth.toFloat() / placeable.width,
                constraints.maxHeight.toFloat() / placeable.height
            )
        } else 1f

        val scaledWidth = (placeable.width * scale).toInt().coerceIn(0, constraints.maxWidth)
        val scaledHeight = (placeable.height * scale).toInt().coerceIn(0, constraints.maxHeight)

        layout(scaledWidth, scaledHeight) {
            // 축소된 배치 영역 중심에 원본 크기 기준으로 배치한 뒤 레이어 스케일 적용
            val x = (scaledWidth - placeable.width) / 2
            val y = (scaledHeight - placeable.height) / 2
            placeable.placeRelativeWithLayer(x, y) {
                scaleX = scale
                scaleY = scale
            }
        }
    }
}
