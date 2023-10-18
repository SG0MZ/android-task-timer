package com.example.tasktimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

private const val TAG = "MainActivityFragment"

private const val DIALOG_ID_DELETE = 1
private const val DIALOG_TASK_ID = "task_id"
private const val DIALOG_TASK_POSITION = "task_position"

class MainActivityFragment : Fragment(),
    CursorRecyclerViewAdapter.OnTaskClickListener {

    interface OnTaskEdit {
        fun OnTaskEdit(task: Task)
    }

    //    private val viewModel by lazy { ViewModelProviders.of(activity!!).get(TaskTimerViewModel::class.java) }
    private val viewModel: DurationsViewModel by viewModels()

    private val mAdapter = CursorRecyclerViewAdapter(null, this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: called")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onAttach(context: Context?) {
        Log.d(TAG, "onAttach: called")
        super.onAttach(context)

        if (context !is OnTaskEdit) {
            throw RuntimeException("${context.toString()} must implement OnTaskEdit")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)
        viewModel.cursor.observe(this, Observer { cursor -> mAdapter.swapCursor(cursor)?.close() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: called")
        super.onViewCreated(view, savedInstanceState)

        task_list.layoutManager = LinearLayoutManager(context)    // <-- set up RecyclerView
        task_list.adapter = mAdapter

        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    // Implement this to allow sorting the tasks by dragging them up and down in the list.
                    return false  // (return true if you move an item)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    Log.d(TAG, "onSwiped called")
                    if (direction == ItemTouchHelper.LEFT) {
                        val task = (viewHolder as TaskViewHolder).task
                        if (task.id == viewModel.editedTaskId) {
                            mAdapter.notifyItemChanged(viewHolder.adapterPosition)
                            Toast.makeText(context,getString(R.string.delete_edited_task),Toast.LENGTH_SHORT).show()
                        } else {
                            onDeleteClick(task, viewHolder.adapterPosition)
                        }
                    }
                }
            })

        itemTouchHelper.attachToRecyclerView(task_list)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: called")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onEditClick(task: Task) {
        (activity as OnTaskEdit?)?.onTaskEdit(task)
    }

    override fun onDeleteClick(task: Task, position: Int) {

        val args = Bundle().apply {
            putInt(DIALOG_ID, DIALOG_ID_DELETE)
            putString(DIALOG_MESSAGE, getString(R.string.deldiag_message, task.id, task, name))
            putInt(DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption)
            putLong(DIALOG_TASK_ID, task.id)
            putInt(DIALOG_TASK_POSITION, position)
        }
        val dialog = AppDialog()
        dialog.arguments = args
        dialog.show(childFragmentManager, null)

    }

    override fun onTaskLongClick(task: Task) {
        Log.d(TAG, "onTaskLongClick: called")
        viewModel.timeTask(task)
    }

    override fun onPositiveDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onPositiveDialogResult: called with id $dialogId")
        if (dialogId == DIALOG_ID_DELETE) {
            val taskId = args.getLong(DIALOG_TASK_ID)
            if (BuildConfig.DEBUG && taskId == 0L) throw AssertionError("Task ID is zero")
            viewModel.deleteTask(taskId)
        }
    }

    override fun onNegativeDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onNegativeDialogResult called")
        if (dialogId == DIALOG_ID_DELETE) {
            val position = args.getInt(DIALOG_TASK_POSITION)
            Log.d(TAG, "onNegativeDialogResult restoring item at position $position")
            mAdapter.notifyItemChanged(position)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewStateRestored: called")
        super.onViewStateRestored(savedInstanceState)
    }
}