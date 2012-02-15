package com.apphance.ameba.plugins.projectconfiguration


import groovy.io.FileType

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import com.apphance.ameba.AbstractPrepareSetupTask;
import com.apphance.ameba.PropertyCategory


class PrepareBaseSetupTask extends AbstractPrepareSetupTask {
    Logger logger = Logging.getLogger(PrepareBaseSetupTask.class)

    PrepareBaseSetupTask() {
        super(BaseProperty.class)
    }

    @TaskAction
    void prepareSetup() {
        logger.lifecycle("Preparing ${propertyDescription}")
        def files = []
        new File('.').eachFileRecurse(FileType.FILES) {
            if (it.name.toLowerCase().equals('icon.png')) {
                def path = it.path.startsWith("./") ? it.path.substring(2) : it.path
                files << path
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
        use(PropertyCategory) {
            BaseProperty.each {
                if (it == BaseProperty.PROJECT_ICON_FILE) {
                    project.getProjectPropertyFromUser(it, files, br)
                } else {
                    project.getProjectPropertyFromUser(it, null, br)
                }
            }
            appendProperties()
        }
    }
}
