package com.apphance.ameba.plugins.android.test.tasks

import com.apphance.ameba.configuration.android.AndroidConfiguration
import com.apphance.ameba.executor.command.Command
import com.apphance.ameba.executor.command.CommandExecutor
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

import static com.apphance.ameba.plugins.FlowTasksGroups.FLOW_TEST

class StopAllEmulatorsTask extends DefaultTask {

    static String NAME = 'stopAllEmulators'
    String group = FLOW_TEST
    String description = 'Stops all emulators and accompanying logcat (includes stopping adb)'

    @Inject AndroidConfiguration conf
    @Inject CommandExecutor executor

    @TaskAction
    void stopAllEmulators() {
        executor.executeCommand(new Command(runDir: conf.rootDir, cmd: ['killall', 'emulator-arm'], failOnError: false))
        executor.executeCommand(new Command(runDir: conf.rootDir, cmd: ['killall', 'adb'], failOnError: false))
    }
}
