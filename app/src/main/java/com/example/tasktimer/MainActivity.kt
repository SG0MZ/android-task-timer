package com.example.tasktimer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.LocusId
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import com.example.tasktimer.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val DIALOG_ID_CANCEL_EDIT = 1
class MainActivity : AppCompatActivity(),
    AddEditFragment.OnSaveClicked,
    MainActivityFragment.OnTaskEdit {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var mTwoPane = false

    private var aboutDialog: AlertDialog? = null
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate: starts")
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        mTwoPane = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        Log.d(TAG,"onCreate: twoPane is $mTwoPane")

        var fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if (fragment != null) {
            showEditPane()
        } else {
            task_details_container.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
            mainFragment.view?.visibility = View.VISIBLE
        }
        Log.d(TAG,"onCreate: finished")
    }

    private fun showEditPane() {
        task_details_container.visibility = View.VISIBLE
        mainFragment.view?.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
    }

    private fun removeEditPane(fragment: Fragment? = null) {
        Log.d(TAG,"removeEditPane called")
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
        task_details_container.visibility = if (mTwoPane) View.INVISIBLE else View.GONE
        mainFragment.view?.visibility = View.VISIBLE

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menumain_addTask -> taskEditRequest(null)
            R.id.menumain_showAbout -> showAboutDialog()
            android.R.id.home -> {
                Log.d(TAG,"onOptionsItemSelected: home button pressed")
                val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
                removeEditPane(fragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun showAboutDialog() {
        val messageView = layoutInflater.inflate(R.layout.about,null,false)
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)

        builder.setPositiveButton(R.string.ok) {
            _, _ ->
                Log.d(TAG,"onClick: Entering messageView.onClick")
                if (aboutDialog != null && aboutDialog?.isShowing == true) {
                    aboutDialog?.dismiss()
                }
        }

        aboutDialog = builder.setView(messageView).create()
        aboutDialog?.setCanceledOnTouchOutside(true)

        val aboutVersion = messageView.findViewById(R.id.about_version) as TextView
//        aboutVersion.text = BuildConfig.VERSION_NAME
        aboutVersion.text = BuildConfig.VERSION_NAME
        aboutDialog?.show()
    }

    override fun onTaskEdit(task: Task) {
        taskEditRequest(task)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onSaveClicked() {
        Log.d(TAG,"onSaveClicked: called")
        removeEditPane(findFragmentById(R.id.task_details_container))
    }

    private fun taskEditRequest(task: Task?) {
        Log.d(TAG,"taskEditRequest: starts")

//        val newFragment = AddEditFragment.newInstance(task)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.task_details_container,newFragment)
//            .commit()

        showEditPane()
        replaceFragment(AddEditFragment.newInstance(task),R.id.task_details_container)
        Log.d(TAG,"Existing taskEditRequest")
    }

    override fun onBackPressed() {
        val fragment = findFragmentById(R.id.task_details_container)
        if (fragment == null || mTwoPane) {
            super.onBackPressed()
        } else {
//            removeEditPane(fragment)
            if ((fragment is AddEditFragment) && fragment.isDirty()) {
                showConfirmationDialog(DIALOG_ID_CANCEL_EDIT,
                    getString(R.string.cancelEditDialog_message),
                    R.string.cancelEditDialog_positive_caption,
                    R.string.cancelEditDialog_negative_caption)
            } else {
                removeEditPane(fragment)
            }
        }
    }

    override fun onPositiveDialogResult(dialoId: Int, args: Bundle) {
        Log.d(TAG,"onPositiveDialogResult: called with dialogId $dialogId")
        if (dialogId == DIALOG_ID_CANCEL_EDIT) {
            val fragment = findFragmentById(R.id.task_details_container)
            removeEditPane(fragment)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG,"onSaveInstanceState: called")
        super.onSaveInstanceState(outState)
    }

    override fun OnTaskEdit(task: Task) {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        Log.d(TAG,"onStop: called")
        super.onStop()
        if (aboutDialog?.isShowing == true) {
            aboutDialog?.dismiss()
        }
    }
}