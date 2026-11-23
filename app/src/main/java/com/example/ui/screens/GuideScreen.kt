package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.CalmGreen
import com.example.ui.theme.CautionAmber

@Composable
fun GuideScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 720.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.img_hospital_quiet_hero),
                contentDescription = stringResource(R.string.cd_hero_banner),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )
        }

        item {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "အကောင့်ဝင်ရန်မလိုဘဲ တိုက်ရိုက်အသုံးပြုနိုင်သည်",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ဆူညံသံမှတ်တမ်းများနှင့် တိုင်ကြားစာများကို ဖုန်းစက်ထဲတွင်သာ လုံခြုံစွာ သိမ်းဆည်းပေးပါသည်။",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HealthAndSafety,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "WHO ဆေးရုံ ဆူညံသံ စံနှုန်းများ",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    ThresholdGuideRow(
                        level = "30 – 40 dB",
                        desc = "ညပိုင်းနှင့် ICU လူနာဆောင် — အေးချမ်းစွာ အိပ်စက်အနားယူနိုင်သော အဆင့်",
                        color = CalmGreen
                    )
                    ThresholdGuideRow(
                        level = "45 – 55 dB",
                        desc = "နေ့ပိုင်းဆေးရုံနှင့် ကျန်းမာရေးဌာန — သတိပြုထိန်းသိမ်းသင့်သော အဆင့်",
                        color = CautionAmber
                    )
                    ThresholdGuideRow(
                        level = "70+ dB",
                        desc = "အန္တရာယ်အဆင့် — သွေးပေါင်တက်ခြင်း၊ နှလုံးခုန်နှုန်းမြန်ခြင်းနှင့် အိပ်မပျော်ခြင်းတို့ ဖြစ်စေနိုင်သည်",
                        color = AlertCrimson
                    )
                }
            }
        }

        item {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "နည်းပညာဆိုင်ရာ အသိပေးချက်",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "• စပီကာထိန်းချုပ်မှု — ဖုန်းနှင့် ချိတ်ဆက်ခွင့်ပြုထားသော Bluetooth စပီကာများနှင့် စက်တွင်းအသံစနစ်များကိုသာ တိုက်ရိုက်အတိုးအချဲ့/အသံပိတ် ထိန်းချုပ်နိုင်ပါသည်။ ခွင့်ပြုချက်မရှိသော ပြင်ပသူစိမ်းဆောင်းဘောက်များကို အဝေးမှ ဝင်ရောက်ပိတ်ပစ်ရန် Android စနစ်တွင် မဖြစ်နိုင်ပါ။",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• APK ရယူရန် — AI Studio မီနူးရှိ Export / Generate APK မှတစ်ဆင့် APK ဖိုင်ကို ဖုန်းသို့ တိုက်ရိုက်ဒေါင်းလော့ဆွဲယူနိုင်ပါသည်။",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ThresholdGuideRow(
    level: String,
    desc: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = level,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
