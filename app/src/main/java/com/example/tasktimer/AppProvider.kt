package com.example.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException

private const val TAG = "AppProvider"

const val CONTENT_AUTHORITY = "learnprogramming.academy.tasktimer.provider"

private const val TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMINGS_ID = 201

private const val TASK_DURATIONS = 400
private const val TASK_DURATIONS_ID = 401

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

class AppProvider: ContentProvider() {

    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher(): UriMatcher {
        Log.d(TAG,"buildUriMatcher starts")
        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        matcher.addURI(CONTENT_AUTHORITY,TasksContract.TABLE_NAME, TASKS)
        matcher.addURI(CONTENT_AUTHORITY,"${TasksContract.TABLE_NAME}/#", TASKS_ID)

        return matcher
    }

    override fun onCreate(): Boolean {
        Log.d(TAG,"onCreate: starts")
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG,"query: called with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG,"query: match is $match")

        val queryBuilder: SQLiteQueryBuilder = SQLiteQueryBuilder()

        when(match) {
            TASKS -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
            }
            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId(uri)
                queryBuilder.appendWhereEscapeString("${TasksContract.Columns.ID} = $taskId")
            }

//            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME
//
//            TIMINGS_ID -> {
//                queryBuilder.tables = TimingsContract.TABLE_NAME
//                val timingId = TimingsContract.getId(uri)
//                queryBuilder.appendWhereEscapeString("${TimingsContract.Columns.ID} = $timingId")
//            }
//
//            TASK_DURATIONS -> queryBuilder.tables = DurationsContract.TABLE_NAME
//
//            TASK_DURATIONS_ID -> {
//                queryBuilder.tables = DurationsContract.TABLE_NAME
//                val durationId = DurationsContract.getId(uri)
//                queryBuilder.appendWhereEscapeString("${DurationsContract.Columns.ID} = $durationId")
//            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val db = AppDatabase.getInstance(context).readableDatabase
        val cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder)
        Log.d(TAG,"query: rows in returned cursos = ${cursor.count}")

        return cursor
    }

    override fun getType(p0: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

}