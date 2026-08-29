package com.example.f2shhh

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lightweight custom Vector Icons for Flip to Shhh.
 * Replaces the 40MB+ material-icons-extended dependency with lightweight vector paths.
 */
object AppIcons {

    val DoNotDisturbOn: ImageVector by lazy {
        ImageVector.Builder(
            name = "DoNotDisturbOn",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
            close()
            moveTo(17f, 13f)
            lineTo(7f, 13f)
            lineTo(7f, 11f)
            lineTo(17f, 11f)
            lineTo(17f, 13f)
            close()
        }.build()
    }

    val Bedtime: ImageVector by lazy {
        ImageVector.Builder(
            name = "Bedtime",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(12.3f, 2f)
            curveTo(6.58f, 2f, 2f, 6.58f, 2f, 12.3f)
            curveTo(2f, 17.75f, 6.17f, 22.19f, 11.5f, 22.5f)
            curveTo(10.53f, 20.91f, 10f, 19.04f, 10f, 17f)
            curveTo(10f, 11.48f, 14.48f, 7f, 20f, 7f)
            curveTo(20.73f, 7f, 21.43f, 7.08f, 22.12f, 7.23f)
            curveTo(20.7f, 4.14f, 16.82f, 2f, 12.3f, 2f)
            close()
        }.build()
    }

    val PowerSettingsNew: ImageVector by lazy {
        ImageVector.Builder(
            name = "PowerSettingsNew",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 3f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(10f)
            horizontalLineToRelative(2f)
            lineTo(13f, 3f)
            close()
            moveTo(17.83f, 5.17f)
            lineToRelative(-1.42f, 1.42f)
            curveTo(17.99f, 7.86f, 19f, 9.81f, 19f, 12f)
            curveTo(19f, 15.87f, 15.87f, 19f, 12f, 19f)
            curveTo(8.13f, 19f, 5f, 15.87f, 5f, 12f)
            curveTo(5f, 9.81f, 6.01f, 7.86f, 7.58f, 6.59f)
            lineTo(6.17f, 5.17f)
            curveTo(4.23f, 6.82f, 3f, 9.26f, 3f, 12f)
            curveTo(3f, 16.97f, 7.03f, 21f, 12f, 21f)
            curveTo(16.97f, 21f, 21f, 16.97f, 21f, 12f)
            curveTo(21f, 9.26f, 19.77f, 6.82f, 17.83f, 5.17f)
            close()
        }.build()
    }

    val NotificationsActive: ImageVector by lazy {
        ImageVector.Builder(
            name = "NotificationsActive",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(7.58f, 4.08f)
            lineTo(6.15f, 2.65f)
            curveTo(3.75f, 4.48f, 2.17f, 7.3f, 2.03f, 10.5f)
            horizontalLineToRelative(2f)
            curveTo(4.16f, 7.86f, 5.48f, 5.54f, 7.58f, 4.08f)
            close()
            moveTo(16.42f, 4.08f)
            curveTo(18.52f, 5.54f, 19.84f, 7.86f, 19.97f, 10.5f)
            horizontalLineToRelative(2f)
            curveTo(21.83f, 7.3f, 20.25f, 4.48f, 17.85f, 2.65f)
            lineToRelative(-1.43f, 1.43f)
            close()
            moveTo(12f, 22f)
            curveTo(13.1f, 22f, 14f, 21.1f, 14f, 20f)
            horizontalLineToRelative(-4f)
            curveTo(10f, 21.1f, 10.89f, 22f, 12f, 22f)
            close()
            moveTo(18f, 16f)
            verticalLineToRelative(-5f)
            curveTo(18f, 7.93f, 15.36f, 5.36f, 12f, 4.5f)
            curveTo(8.64f, 5.36f, 6f, 7.92f, 6f, 11f)
            verticalLineToRelative(5f)
            lineToRelative(-2f, 2f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(-1f)
            lineToRelative(-2f, -2f)
            close()
        }.build()
    }

    val BatterySaver: ImageVector by lazy {
        ImageVector.Builder(
            name = "BatterySaver",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(16f, 4f)
            horizontalLineToRelative(-2f)
            lineTo(14f, 2f)
            horizontalLineToRelative(-4f)
            verticalLineToRelative(2f)
            lineTo(8f, 4f)
            curveTo(6.9f, 4f, 6f, 4.9f, 6f, 6f)
            verticalLineToRelative(14f)
            curveTo(6f, 21.1f, 6.9f, 22f, 8f, 22f)
            horizontalLineToRelative(8f)
            curveTo(17.1f, 22f, 18f, 21.1f, 18f, 20f)
            lineTo(18f, 6f)
            curveTo(18f, 4.9f, 17.1f, 4f, 16f, 4f)
            close()
            moveTo(15f, 14f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            lineTo(9f, 14f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            close()
        }.build()
    }

    val ChevronRight: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChevronRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(10f, 6f)
            lineTo(8.59f, 7.41f)
            lineTo(13.17f, 12f)
            lineToRelative(-4.58f, 4.59f)
            lineTo(10f, 18f)
            lineToRelative(6f, -6f)
            close()
        }.build()
    }

    val Shield: ImageVector by lazy {
        ImageVector.Builder(
            name = "Shield",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 1f)
            lineTo(3f, 5f)
            verticalLineToRelative(6f)
            curveTo(3f, 16.55f, 6.84f, 21.74f, 12f, 23f)
            curveTo(17.16f, 21.74f, 21f, 16.55f, 21f, 11f)
            lineTo(21f, 5f)
            lineTo(12f, 1f)
            close()
        }.build()
    }

    val Code: ImageVector by lazy {
        ImageVector.Builder(
            name = "Code",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(9.4f, 16.6f)
            lineTo(4.8f, 12f)
            lineToRelative(4.6f, -4.6f)
            lineTo(8f, 6f)
            lineToRelative(-6f, 6f)
            lineToRelative(6f, 6f)
            lineToRelative(1.4f, -1.4f)
            close()
            moveTo(14.6f, 16.6f)
            lineTo(19.2f, 12f)
            lineToRelative(-4.6f, -4.6f)
            lineTo(16f, 6f)
            lineToRelative(6f, 6f)
            lineToRelative(-6f, 6f)
            lineToRelative(-1.4f, -1.4f)
            close()
        }.build()
    }

    val CheckCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "CheckCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
            close()
            moveTo(10f, 17f)
            lineTo(5f, 12f)
            lineToRelative(1.41f, -1.41f)
            lineTo(10f, 14.17f)
            lineToRelative(7.59f, -7.59f)
            lineTo(19f, 8f)
            lineToRelative(-9f, 9f)
            close()
        }.build()
    }

    val Lock: ImageVector by lazy {
        ImageVector.Builder(
            name = "Lock",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(18f, 8f)
            horizontalLineToRelative(-1f)
            lineTo(17f, 6f)
            curveTo(17f, 3.24f, 14.76f, 1f, 12f, 1f)
            curveTo(9.24f, 1f, 7f, 3.24f, 7f, 6f)
            verticalLineToRelative(2f)
            lineTo(6f, 8f)
            curveTo(4.9f, 8f, 4f, 8.9f, 4f, 10f)
            verticalLineToRelative(10f)
            curveTo(4f, 21.1f, 4.9f, 22f, 6f, 22f)
            horizontalLineToRelative(12f)
            curveTo(19.1f, 22f, 20f, 21.1f, 20f, 20f)
            lineTo(20f, 10f)
            curveTo(20f, 8.9f, 19.1f, 8f, 18f, 8f)
            close()
            moveTo(12f, 17f)
            curveTo(10.9f, 17f, 10f, 16.1f, 10f, 15f)
            curveTo(10f, 13.9f, 10.9f, 13f, 12f, 13f)
            curveTo(13.1f, 13f, 14f, 13.9f, 14f, 15f)
            curveTo(14f, 16.1f, 13.1f, 17f, 12f, 17f)
            close()
            moveTo(15.1f, 8f)
            lineTo(8.9f, 8f)
            lineTo(8.9f, 6f)
            curveTo(8.9f, 4.29f, 10.29f, 2.9f, 12f, 2.9f)
            curveTo(13.71f, 2.9f, 15.1f, 4.29f, 15.1f, 6f)
            verticalLineToRelative(2f)
            close()
        }.build()
    }

    val Warning: ImageVector by lazy {
        ImageVector.Builder(
            name = "Warning",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(1f, 21f)
            horizontalLineToRelative(22f)
            lineTo(12f, 2f)
            lineTo(1f, 21f)
            close()
            moveTo(13f, 18f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            close()
            moveTo(13f, 14f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(4f)
            close()
        }.build()
    }

    val Info: ImageVector by lazy {
        ImageVector.Builder(
            name = "Info",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
            curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
            curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
            close()
            moveTo(13f, 17f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-6f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(6f)
            close()
            moveTo(13f, 9f)
            horizontalLineToRelative(-2f)
            lineTo(11f, 7f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            close()
        }.build()
    }
}
