package com.example.tasktimer

import android.annotation.SuppressLint
import android.content.ContentValues
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
import com.example.tasktimer.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

//        testUpdate()
        testUpdate2()

        val projection = arrayOf(TasksContract.Columns.TASK_NAME,TasksContract.Columns.TASK_SORT_ORDER)
        val sortColumn = TasksContract.Columns.TASK_SORT_ORDER

        val cursor = contentResolver.query(TasksContract.CONTENT_URI,
            null,
            null,
            null,
            sortColumn)
        Log.d(TAG,"********************")

        cursor.use {
            while (it.moveToNext()) {
                with(cursor) {
                    val id = getLong(0)
                    val name = getString(1)
                    val description = getString(2)
                    val sortOrder = getString(3)
                    val result = "ID: $id, Name: $name, Description: $description, Sort Order: $sortOrder"
                    Log.d(TAG,"onCreate: reading data $result")
                }
            }
        }

        Log.d(TAG,"********************")

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun testDelete2() {
        val selection = TasksContract.Columns.TASK_DESCRIPTION + " = ?"
        val selectionArgs = arrayOf("For deletion")
        val rowsAffected = contentResolver.delete(TasksContract.CONTENT_URI,selection,selectionArgs)
        Log.d(TAG,"Number of rows deleted is $rowsAffected")
    }

    private fun testDelete() {
        val taskUri = TasksContract.buildUriFromId(3)
        val rowsAffected = contentResolver.delete(taskUri,null,null)
        Log.d(TAG,"Number of rows deleted is $rowsAffected")
    }

    private fun testUpdate2() {
        val values = ContentValues().apply {
            put(TasksContract.Columns.TASK_SORT_ORDER,999)
            put(TasksContract.Columns.TASK_DESCRIPTION,"For deletion")
        }

        val selection = TasksContract.Columns.TASK_SORT_ORDER + " = ?"
        val selectionArgs = arrayOf("99")

//        val taskUri = TasksContract.buildUriFromId(4)
        val rowsAffected = contentResolver.update(TasksContract.CONTENT_URI,
            values,
            selection,
            selectionArgs)
        Log.d(TAG,"Number of rows updated is $rowsAffected")
    }

    private fun testUpdate() {
        val values = ContentValues().apply {
            put(TasksContract.Columns.TASK_NAME,"Content Provider")
            put(TasksContract.Columns.TASK_DESCRIPTION,"Record content providers videos")
        }

        val taskUri = TasksContract.buildUriFromId(4)
        val rowsAffected = contentResolver.update(taskUri,values,null,null)
        Log.d(TAG,"Number of rows updated is $rowsAffected")
    }

    private fun testInsert() {
        val values = ContentValues().apply {
            put(TasksContract.Columns.TASK_NAME,"New Task 1")
            put(TasksContract.Columns.TASK_DESCRIPTION,"Description 1")
            put(TasksContract.Columns.TASK_SORT_ORDER,2)
        }

        val uri = contentResolver.insert(TasksContract.CONTENT_URI,values)
        Log.d(TAG,"New row id (in uri) is $uri")
        Log.d(TAG,"id (in uri) is ${TasksContract.getId(uri)}")
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
        return when (item.itemId) {
            R.id.menumain_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}