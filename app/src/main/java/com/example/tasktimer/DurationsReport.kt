package com.example.tasktimer

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasktimer.databinding.ActivityDurationsReportBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class SortColumns {
    NAME,
    DESCRIPTION,
    START_DATE,
    DURATION
}

private const val TAG = "DurationsReport"

class DurationsReport : AppCompatActivity() {

    private val reportAdapter by lazy { DurationsRVAdapter(this,null)}

    var databaseCursor: Cursor? = null

    var sortOrder = SortColumns.NAME

    private val selection = "${DurationsContract.Columns.START_TIME} Between ? and ?"
    private var selectionArgs = arrayOf("155668800","1559347199")

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDurationsReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDurationsReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_durations_report)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        td_list.layoutManager = LinearLayoutManager(this)
        td_list.adapter = reportAdapter
    }

    private fun loadData() {
        val order = when(sortOrder) {
            SortColumns.NAME -> DurationsContract.Columns.NAME
            SortColumns.DESCRIPTION -> DurationsContract.Columns.DESCRIPTION
            SortColumns.START_DATE -> DurationsContract.Columns.START_DATE
            SortColumns.DURATION -> DurationsContract.Columns.DURATION
        }
        Log.d(TAG,"order is $order")

        GlobalScope.launch {
            val cursor = application.contentResolver.query(
                DurationsContract.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                order
            )
            databaseCursor = cursor
            reportAdapter.swapCursor(cursor)?.close()
        }
    }

//    override fun onDestroy() {
//        reportAdapter.swapCursor(null)?.close()
//        super.onDestroy()
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_durations_report)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}