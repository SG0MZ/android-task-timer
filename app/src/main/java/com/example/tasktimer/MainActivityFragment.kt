package com.example.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

private const val TAG = "MainActivityFragment"
class MainActivityFragment: Fragment(), CursorRecyclerViewAdapter.OnTaskClickListener {

    interface OnTaskEdit {
        fun OnTaskEdit(task: Task)
    }

    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(TaskTimerViewModel::class.java) }
    private val mAdapter = CursorRecyclerViewAdapter(null,this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView: called")
        return inflater.inflate(R.layout.fragment_main,container,false)
    }

    override fun onAttach(context: Context?) {
        Log.d(TAG,"onAttach: called")
        super.onAttach(context)

        if (context !is OnTaskEdit) {
            throw RuntimeException("${context.toString()} must implement OnTaskEdit")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"onCreate: called")
        super.onCreate(savedInstanceState)
        viewModel.cursor.observe(this,Observer{ cursor -> mAdapter.swapCursor(cursor)?.close() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG,"onViewCreated: called")
        super.onViewCreated(view, savedInstanceState)

        task_list.layoutManager = LinearLayoutManager(context)
        task_list.adapter = mAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG,"onActivityCreated: called")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onEditClick(task: Task) {
        (activity as OnTaskEdit?)?.onTaskEdit(task)
    }

    override fun onDeleteClick(task: Task) {
        TODO("Not yet implemented")
    }

    override fun onTaskLongClick(task: Task) {
        TODO("Not yet implemented")
    }
}