package com.example.devexercise.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.LicenseInfo
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.R
import com.example.devexercise.databinding.ActivityMainBinding
import com.example.devexercise.repository.LoginRepository
import com.example.devexercise.viewmodel.LoginViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: LoginViewModel

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (applicationContext as DevExerciseApp).appComp().inject(this)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        val navheader = binding.navView.getHeaderView(0)

        val displayName = navheader.findViewById<TextView>(R.id.display_name_text)
        val dataStored = viewModel.getDataStored()
        if(dataStored?.userId != null){
            displayName.text = dataStored.userId
        }else{
            val userInfo = viewModel.getUserInfo()
            userInfo.observe(this, Observer { displayName.text = it.userId })
        }

        val buttonLogout = binding.navView.menu.getItem(0)
        buttonLogout.setOnMenuItemClickListener { item ->
            when(item!!.itemId){
                R.id.loginActivity -> {
                    viewModel.logout()
                    val loginIntent = Intent(this, LoginActivity::class.java)
                    startActivity(loginIntent)
                    finish()
                }
            }
            true
        }

        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if(nd.id == nc.graph.startDestination){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onStop() {
        viewModel.rememberAction()
        super.onStop()
    }
}