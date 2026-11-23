package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.audio.DecibelMonitorEngine
import com.example.audio.SpeakerControlManager
import com.example.data.preferences.QuietSettingsStore
import com.example.data.repository.NoiseRepository
import com.example.ui.screens.ComplaintsScreen
import com.example.ui.screens.DraftComplaintPrefill
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.MonitorScreen
import com.example.ui.screens.SpeakersScreen
import com.example.ui.theme.QuietCareTheme
import com.example.ui.viewmodel.QuietCareViewModel

object Routes {
    const val MONITOR = "monitor"
    const val SPEAKERS = "speakers"
    const val COMPLAINTS = "complaints"
    const val GUIDE = "guide"
}

private data class NavDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuietCareTheme {
                MainShell()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainShell() {
    val viewModel: QuietCareViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                QuietCareViewModel(
                    repository = NoiseRepository(app),
                    settingsStore = QuietSettingsStore(app),
                    speakerControlManager = SpeakerControlManager(app),
                    decibelEngine = DecibelMonitorEngine()
                )
            }
        }
    )

    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val monitorState by viewModel.monitorState.collectAsStateWithLifecycle()
    val speakerSnapshot by viewModel.speakerSnapshot.collectAsStateWithLifecycle()
    val complaintsState by viewModel.complaintsState.collectAsStateWithLifecycle()
    val noiseLogsState by viewModel.noiseLogsState.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(bannerMessage) {
        val msg = bannerMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBannerMessage()
        }
    }

    var complaintPrefill by remember { mutableStateOf<DraftComplaintPrefill?>(null) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.MONITOR

    val destinations = remember {
        listOf(
            NavDestination(Routes.MONITOR, R.string.nav_monitor, Icons.Filled.GraphicEq, "nav_monitor"),
            NavDestination(Routes.SPEAKERS, R.string.nav_speakers, Icons.Filled.Speaker, "nav_speakers"),
            NavDestination(Routes.COMPLAINTS, R.string.nav_complaints, Icons.Filled.Report, "nav_complaints"),
            NavDestination(Routes.GUIDE, R.string.nav_guide, Icons.AutoMirrored.Filled.HelpOutline, "nav_guide")
        )
    }

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar {
                        destinations.forEach { dest ->
                            NavigationBarItem(
                                selected = currentRoute == dest.route,
                                onClick = { navigateTo(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail {
                        destinations.forEach { dest ->
                            NavigationRailItem(
                                selected = currentRoute == dest.route,
                                onClick = { navigateTo(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.MONITOR
                    ) {
                        composable(Routes.MONITOR) {
                            MonitorScreen(
                                monitorState = monitorState,
                                preferences = preferences,
                                noiseLogsState = noiseLogsState,
                                onStartMonitoring = viewModel::startMonitoring,
                                onStopMonitoring = viewModel::stopMonitoring,
                                onSelectPreset = viewModel::selectZonePreset,
                                onThresholdChange = viewModel::updateCustomThreshold,
                                onToggleAutoGuard = viewModel::setAutoGuardEnabled,
                                onCaptureSnapshot = viewModel::captureCurrentNoiseSnapshot,
                                onDeleteLog = viewModel::deleteNoiseLog,
                                onReportNoiseClick = { measured, peak ->
                                    complaintPrefill = DraftComplaintPrefill(
                                        measuredDb = measured.coerceAtLeast(35),
                                        peakDb = peak.coerceAtLeast(45)
                                    )
                                    navigateTo(Routes.COMPLAINTS)
                                }
                            )
                        }
                        composable(Routes.SPEAKERS) {
                            SpeakersScreen(
                                snapshot = speakerSnapshot,
                                onRefresh = viewModel::refreshSpeakers,
                                onSetVolume = viewModel::setStreamVolume,
                                onToggleMediaMute = viewModel::toggleMediaMute,
                                onRouteToPhoneSpeaker = viewModel::routeToBuiltInSpeaker,
                                onOpenBluetoothSettings = viewModel::openBluetoothSettings
                            )
                        }
                        composable(Routes.COMPLAINTS) {
                            ComplaintsScreen(
                                complaintsState = complaintsState,
                                defaultReporterName = "Healthcare Staff / Citizen",
                                defaultFacilityName = preferences.defaultFacilityName,
                                prefill = complaintPrefill,
                                onClearPrefill = { complaintPrefill = null },
                                onCreateComplaint = viewModel::submitComplaint,
                                onUpdateStatus = viewModel::updateComplaintStatus,
                                onDeleteComplaint = viewModel::deleteComplaint
                            )
                        }
                        composable(Routes.GUIDE) {
                            GuideScreen()
                        }
                    }
                }
            }
        }
    }
}
