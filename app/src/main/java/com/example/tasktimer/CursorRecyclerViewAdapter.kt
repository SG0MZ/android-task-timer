package com.example.tasktimer

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException

class TaskViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),LayoutContainer {

}

private const val TAG = "CursorRecyclerViewAdapter"

class CursorRecyclerViewAdapter(private var cursor: Cursor?):
    RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG,"onCreateViewHolder: new view requested")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_items,parent,null)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d(TAG,"onBindViewHolder: starts")

        val cursor = cursor

        if (cursor == null || cursor.count == 0) {
            Log.d(TAG,"onBindViewHolder: providing instructions")
            holder.tli_name.setText("Instructions")
            holder.tli_description.setText("")
            holder.tli_edit.visibility = View.GONE
            holder.tli_delete.visibility = View.GONE
        } else {
            if (!cursor.moveToPosition(position)) {
                throw IllegalStateException("Couldn't move cursor to position $position")
            }

            val task = Task(
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER))
            )
            task.id = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.ID))

            holder.tli_name.text = task.name
            holder.tli_description.text = task.description
            holder.tli_edit.visibility = View.VISIBLE
            holder.tli_delete.visibility = View.VISIBLE
        }
    }

}