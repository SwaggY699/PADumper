package com.dumper.android.messager


import android.os.Build
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.dumper.android.core.MainActivity
import com.dumper.android.core.RootServices
import com.dumper.android.dumper.process.ProcessData
import com.dumper.android.ui.memory.MemoryFragment

class MSGReceiver(private val activity: MainActivity) : Handler.Callback {

    override fun handleMessage(message: Message): Boolean {
        message.data.classLoader = activity.classLoader

        when (message.what) {
            RootServices.MSG_GET_PROCESS_LIST -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    message.data.getParcelableArrayList(
                        RootServices.LIST_ALL_PROCESS,
                        ProcessData::class.java
                    )
                        ?.let {

                            val navController =
                                activity.binding.navHostFragmentActivityMain.getFragment<NavHostFragment>()
                            navController.childFragmentManager.fragments
                                .find { it is MemoryFragment }
                                ?.let { fragment ->
                                    (fragment as MemoryFragment).showProcess(it)
                                }

                        }
                } else {
                    message.data.getParcelableArrayList<ProcessData>(RootServices.LIST_ALL_PROCESS)
                        ?.let {

                            val navController =
                                activity.binding.navHostFragmentActivityMain.getFragment<NavHostFragment>()
                            navController.childFragmentManager.fragments
                                .find { it is MemoryFragment }
                                ?.let { fragment ->
                                    (fragment as MemoryFragment).showProcess(it)
                                }

                        }
                }
            }
            RootServices.MSG_DUMP_PROCESS -> {
                message.data.getString(RootServices.DUMP_LOG)?.let {
                    activity.console.append(it)
                    activity.console.appendLine("==========================")
                    Toast.makeText(activity, "Dump Complete!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return false
    }
}