package com.example.tasktimer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivityFragment"
class MainActivityFragment: Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(TaskTimerViewModel::class.java) }
    private val mAdapter = CursorRecyclerViewAdapter(null)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView: called")
        return inflater.inflate(R.layout.fragment_main,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate: called")
        super.onCreate(savedInstanceState)
        viewModel.cursor.observe(this,Observer{ cursor -> mAdapter.swapCursor(cursor)?.close() })
    }
}