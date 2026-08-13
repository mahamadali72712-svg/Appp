package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class AppThemeOption(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentGlow: Color,
    val gradientBrush: Brush,
    val cardBackground: Color = Color(0x2BFFFFFF),
    val cardBorder: Color = Color(0x26FFFFFF)
)

val ThemePresets = listOf(
    AppThemeOption(
        id = "ocean",
        name = "أزرق سيان (افتراضي)",
        primaryColor = Color(0xFF00C9FF),
        secondaryColor = Color(0xFF92FE9D),
        accentGlow = Color(0x6600C9FF),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF1E3C72),
            0.4f to Color(0xFF2A5298),
            0.7f to Color(0xFF2980B9),
            1.0f to Color(0xFF1E3C72)
        )
    ),
    AppThemeOption(
        id = "emerald",
        name = "زمردي فاخر",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF34D399),
        accentGlow = Color(0x6610B981),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF064E3B),
            0.5f to Color(0xFF047857),
            1.0f to Color(0xFF065F46)
        )
    ),
    AppThemeOption(
        id = "gold",
        name = "ذهبي ملكي",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFFBBF24),
        accentGlow = Color(0x66F59E0B),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF451A03),
            0.5f to Color(0xFF78350F),
            1.0f to Color(0xFF9A3412)
        )
    ),
    AppThemeOption(
        id = "purple",
        name = "بنفسجي ملوكي",
        primaryColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFFEC4899),
        accentGlow = Color(0x66A855F7),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF3B0764),
            0.5f to Color(0xFF581C87),
            1.0f to Color(0xFF6B21A8)
        )
    ),
    AppThemeOption(
        id = "crimson",
        name = "ياقوتي فاخر",
        primaryColor = Color(0xFFEF4444),
        secondaryColor = Color(0xFFF87171),
        accentGlow = Color(0x66EF4444),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF450A0A),
            0.5f to Color(0xFF7F1D1D),
            1.0f to Color(0xFF991B1B)
        )
    ),
    AppThemeOption(
        id = "dark_glass",
        name = "زجاجي داكن",
        primaryColor = Color(0xFF6366F1),
        secondaryColor = Color(0xFF38BDF8),
        accentGlow = Color(0x666366F1),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF0F172A),
            0.5f to Color(0xFF1E293B),
            1.0f to Color(0xFF0F172A)
        )
    ),
    AppThemeOption(
        id = "sunset",
        name = "غروب دافئ برتقالي",
        primaryColor = Color(0xFFFF6B6B),
        secondaryColor = Color(0xFFFF8E53),
        accentGlow = Color(0x66FF6B6B),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF4A0E17),
            0.5f to Color(0xFF7A1C28),
            1.0f to Color(0xFF9E2A2B)
        )
    ),
    AppThemeOption(
        id = "mint",
        name = "نعناعي فيروزي",
        primaryColor = Color(0xFF00F2FE),
        secondaryColor = Color(0xFF4FACFE),
        accentGlow = Color(0x6600F2FE),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF003B46),
            0.5f to Color(0xFF07575B),
            1.0f to Color(0xFF003B46)
        )
    ),
    AppThemeOption(
        id = "sapphire",
        name = "ياقوت كحلي ليلي",
        primaryColor = Color(0xFF3B82F6),
        secondaryColor = Color(0xFF60A5FA),
        accentGlow = Color(0x663B82F6),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF0B192C),
            0.5f to Color(0xFF1E3E62),
            1.0f to Color(0xFF0B192C)
        )
    ),
    AppThemeOption(
        id = "rose",
        name = "وردي ملوكي",
        primaryColor = Color(0xFFEC4899),
        secondaryColor = Color(0xFFF472B6),
        accentGlow = Color(0x66EC4899),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF500724),
            0.5f to Color(0xFF831843),
            1.0f to Color(0xFF9F1239)
        )
    ),
    AppThemeOption(
        id = "coffee",
        name = "قهوة ملكية دافئة",
        primaryColor = Color(0xFFD97706),
        secondaryColor = Color(0xFFF59E0B),
        accentGlow = Color(0x66D97706),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF2E1C0C),
            0.5f to Color(0xFF4A3018),
            1.0f to Color(0xFF3B2312)
        )
    ),
    AppThemeOption(
        id = "cyber_neon",
        name = "نيون سايبر حديث",
        primaryColor = Color(0xFF00FFC6),
        secondaryColor = Color(0xFF7B2CBF),
        accentGlow = Color(0x6600FFC6),
        gradientBrush = Brush.verticalGradient(
            0.0f to Color(0xFF10002B),
            0.5f to Color(0xFF240046),
            1.0f to Color(0xFF3C096C)
        )
    )
)

fun getThemeOption(id: String): AppThemeOption {
    return ThemePresets.find { it.id == id } ?: ThemePresets.first()
}
