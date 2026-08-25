package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.NavDestination
import com.example.ui.components.RoomMateBottomBar
import com.example.ui.screens.details.PropertyDetailScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.onboarding.RoleSelectionScreen
import com.example.ui.screens.onboarding.UsernameScreen
import com.example.ui.screens.onboarding.WelcomeScreen
import com.example.ui.screens.post.PostPropertyScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.saved.CompareRoomsScreen
import com.example.ui.screens.saved.SavedScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RoomMateApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RoomMateApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val filteredProperties by viewModel.filteredProperties.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()
    val recentlyViewedIds by viewModel.recentlyViewedIds.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val compareIds by viewModel.comparePropertyIds.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            viewModel.clearUserMessage()
        }
    }

    var onboardingUsername by remember { mutableStateOf("") }

    val showBottomBar = currentRoute in listOf(
        NavDestination.Home.route,
        NavDestination.Search.route,
        NavDestination.Saved.route,
        NavDestination.Profile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                RoomMateBottomBar(
                    currentRoute = currentRoute ?: NavDestination.Home.route,
                    savedCount = savedIds.size,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val startDestination = if (currentUser?.isInitialOnboardingCompleted == true) {
            NavDestination.Home.route
        } else {
            "welcome"
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ONBOARDING SCREEN 1: Welcome
            composable("welcome") {
                WelcomeScreen(
                    onContinue = {
                        navController.navigate("username_input")
                    }
                )
            }

            // ONBOARDING SCREEN 2: Username Input
            composable("username_input") {
                UsernameScreen(
                    onContinue = { username ->
                        onboardingUsername = username
                        navController.navigate("role_selection")
                    }
                )
            }

            // ONBOARDING SCREEN 3: Role Selection
            composable("role_selection") {
                RoleSelectionScreen(
                    username = onboardingUsername.ifBlank { "Student" },
                    onRoleSelected = { role ->
                        viewModel.completeOnboarding(
                            username = onboardingUsername.ifBlank { "Student" },
                            role = role
                        )
                        if (role == "OWNER") {
                            navController.navigate(NavDestination.Post.route) {
                                popUpTo("welcome") { inclusive = true }
                            }
                        } else {
                            navController.navigate(NavDestination.Home.route) {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    }
                )
            }

            // TAB 1: HOME SCREEN
            composable(NavDestination.Home.route) {
                HomeScreen(
                    currentUser = currentUser,
                    properties = filteredProperties,
                    savedPropertyIds = savedIds,
                    filterState = filterState,
                    onSearchClick = {
                        navController.navigate(NavDestination.Search.route)
                    },
                    onPropertyClick = { propId ->
                        viewModel.selectProperty(propId)
                        navController.navigate("property_detail/$propId")
                    },
                    onSaveToggle = { propId ->
                        viewModel.toggleSaveProperty(propId)
                    },
                    onQuickFilterSelected = { filter ->
                        viewModel.setQuickFilter(filter)
                    },
                    onLocationUpdate = { city, area, college ->
                        viewModel.updateLocation(city, area, college)
                    },
                    onSwitchRole = { nextRole ->
                        viewModel.switchRole(nextRole)
                    },
                    onPostPropertyClick = {
                        navController.navigate(NavDestination.Post.route)
                    }
                )
            }

            // TAB 2: SEARCH SCREEN
            composable(NavDestination.Search.route) {
                SearchScreen(
                    properties = filteredProperties,
                    savedPropertyIds = savedIds,
                    filterState = filterState,
                    onSearchQueryChange = { query ->
                        viewModel.updateSearchQuery(query)
                    },
                    onApplyFilters = { minRent, maxRent, types, furnishings, facilities, gender, avail, sort, area, college ->
                        viewModel.applyDetailedFilters(
                            minRent, maxRent, types, furnishings, facilities, gender, avail, sort, area, college
                        )
                    },
                    onResetFilters = {
                        viewModel.resetFilters()
                    },
                    onPropertyClick = { propId ->
                        viewModel.selectProperty(propId)
                        navController.navigate("property_detail/$propId")
                    },
                    onSaveToggle = { propId ->
                        viewModel.toggleSaveProperty(propId)
                    }
                )
            }

            // TAB 3: POST PROPERTY (Owner 8-step flow)
            composable(NavDestination.Post.route) {
                PostPropertyScreen(
                    currentUser = currentUser,
                    viewModel = viewModel,
                    onFinish = { createdProp ->
                        if (createdProp != null) {
                            viewModel.selectProperty(createdProp.id)
                            navController.navigate("property_detail/${createdProp.id}") {
                                popUpTo(NavDestination.Home.route)
                            }
                        } else {
                            navController.navigate(NavDestination.Home.route) {
                                popUpTo(NavDestination.Home.route) { inclusive = true }
                            }
                        }
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }

            // TAB 4: SAVED PROPERTIES SCREEN
            composable(NavDestination.Saved.route) {
                val savedList = allProperties.filter { savedIds.contains(it.id) }
                val recentList = allProperties.filter { recentlyViewedIds.contains(it.id) }

                SavedScreen(
                    savedProperties = savedList,
                    recentlyViewedProperties = recentList,
                    compareCount = compareIds.size,
                    onPropertyClick = { propId ->
                        viewModel.selectProperty(propId)
                        navController.navigate("property_detail/$propId")
                    },
                    onSaveToggle = { propId ->
                        viewModel.toggleSaveProperty(propId)
                    },
                    onCompareClick = {
                        navController.navigate("compare_rooms")
                    },
                    onExploreClick = {
                        navController.navigate(NavDestination.Search.route)
                    }
                )
            }

            // TAB 5: PROFILE & DASHBOARD SCREEN
            composable(NavDestination.Profile.route) {
                ProfileScreen(
                    currentUser = currentUser,
                    viewModel = viewModel,
                    onNavigateToPost = {
                        navController.navigate(NavDestination.Post.route)
                    },
                    onNavigateToProperty = { propId ->
                        viewModel.selectProperty(propId)
                        navController.navigate("property_detail/$propId")
                    }
                )
            }

            // PROPERTY DETAIL SCREEN
            composable("property_detail/{propertyId}") { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId")
                val property = allProperties.find { it.id == propertyId }

                if (property != null) {
                    PropertyDetailScreen(
                        property = property,
                        currentUser = currentUser,
                        isSaved = savedIds.contains(property.id),
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToCompare = {
                            navController.navigate("compare_rooms")
                        }
                    )
                }
            }

            // COMPARE ROOMS SCREEN
            composable("compare_rooms") {
                val compareList = allProperties.filter {
                    compareIds.contains(it.id) || savedIds.contains(it.id)
                }.take(3)

                CompareRoomsScreen(
                    properties = compareList,
                    onPropertyClick = { propId ->
                        viewModel.selectProperty(propId)
                        navController.navigate("property_detail/$propId")
                    },
                    onRemoveFromCompare = { propId ->
                        viewModel.toggleCompare(propId)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
