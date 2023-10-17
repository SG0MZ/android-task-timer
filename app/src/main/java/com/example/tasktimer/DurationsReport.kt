package com.example.tasktimer

import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasktimer.databinding.ActivityDurationsReportBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "DurationsReport"

class DurationsReport : AppCompatActivity(),
    View.OnClickListener {

    private val viewModel by lazy { ViewModelProviders.of(this).get(DurationsViewModel::class.java) }
    private val reportAdapter by lazy { DurationsRVAdapter(this,null)}

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

        viewModel.cursor.observe(this,Observer { cursor -> reportAdapter.swapCursor(cursor)?.close() })

        td_name_heading.setOnClickListener(this)
        td_description_heading?.setOnClickListener(this)
        td_start_heading.setOnClickListener(this)
        td_duration_heading.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v.id) {
            R.id.td_name_heading -> viewModel.sortOrder = SortColumns.NAME
            R.id.td_description_heading -> viewModel.sortOrder = SortColumns.DESCRIPTION
            R.id.td_start_heading -> viewModel.sortOrder = SortColumns.START_DATE
            R.id.td_duration_heading -> viewModel.sortOrder = SortColumns.DURATION
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_report,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.rm_filter_period -> {
                viewModel.toggleDisplayWeek()
                invalidateOptionsMenu()
                return true
            }
            R.id.rm_filter_date -> {}
            R.id.rm_delete -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu.findItem(R.id.rm_filter_period)
        if (item != null) {
            if (viewModel.displayWeek) {
                item.setIcon(R.drawable.ic_filter_1_black_24dp)
                item.setTitle(R.string.rm_title_filter_day)
            } else {
                item.setIcon(R.drawable.ic_filter_7_black_24dp)
                item.setTitle(R.string.rm_title_filter_week)
            }
        }
        return super.onPrepareOptionsMenu(menu)
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