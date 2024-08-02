package com.utar.loancalculator

import com.utar.loancalculator.ui.viewModel.SavedCalculationsViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.utar.loancalculator.internal.dataclass.BottomNavigationItem
import com.utar.loancalculator.internal.datastore.DataStoreRepository
import com.utar.loancalculator.ui.nav.SetupNavGraph
import com.utar.loancalculator.ui.screen.Screens
import com.utar.loancalculator.ui.theme.LoanCalculatorTheme

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var savedCalculationsViewModel: SavedCalculationsViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        savedCalculationsViewModel = ViewModelProvider(this)[SavedCalculationsViewModel::class.java]

        setContent {
            val items = listOf(

                BottomNavigationItem(
                    title = "Home",
                    selectedIcon = ImageVector.vectorResource(id = R.drawable.ic_home_filled),
                    unselectedIcon = ImageVector.vectorResource(id = R.drawable.ic_home_outlined)
                ),
                BottomNavigationItem(
                    title = "Saved",
                    selectedIcon = ImageVector.vectorResource(id = R.drawable.ic_description_filled),
                    unselectedIcon = ImageVector.vectorResource(id = R.drawable.ic_description_outlined)
                ),
                BottomNavigationItem(
                    title = "Setting",
                    selectedIcon = ImageVector.vectorResource(id = R.drawable.ic_settings_filled),
                    unselectedIcon = ImageVector.vectorResource(id = R.drawable.ic_settings_outlined)
                ),
            )

            var selectedItemIndex by rememberSaveable {
                mutableIntStateOf(0)
            }
            val dataStore = DataStoreRepository(LocalContext.current)
            val isBirthYearSet = dataStore.getIsBirthYearSet.collectAsState(initial = false)

            LoanCalculatorTheme {
                navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination
                val currentRoute = currentDestination?.route

                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    if (currentRoute == Screens.Home.route || currentRoute == Screens.Saved.route || currentRoute == Screens.Setting.route) {
                        TopAppBar(
                            title = { Text(text = stringResource(id = R.string.app_name)) },
                        )
                    } else if (currentRoute != Screens.Initial.route && currentRoute != Screens.Home.route && currentRoute != Screens.Saved.route && currentRoute != Screens.Setting.route) {
                        TopAppBar(title = {}, navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Navigation Icon"
                                )
                            }
                        })
                    }
                }, bottomBar = {
                    if (isBirthYearSet.value && currentRoute == Screens.Home.route || currentRoute == Screens.Saved.route || currentRoute == Screens.Setting.route) {
                        NavigationBar {
                            items.forEachIndexed { index, bottomNavigationItem ->
                                NavigationBarItem(selected = selectedItemIndex == index, onClick = {
                                    selectedItemIndex = items.indexOf(bottomNavigationItem)
                                    navController.navigate(bottomNavigationItem.title) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }, icon = {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            bottomNavigationItem.selectedIcon
                                        } else bottomNavigationItem.unselectedIcon,
                                        contentDescription = bottomNavigationItem.title
                                    )
                                })
                            }
                        }
                    }
                }) { innerPadding ->
                    SetupNavGraph(
                        navHostController = navController,
                        savedCalculationsViewModel = savedCalculationsViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}