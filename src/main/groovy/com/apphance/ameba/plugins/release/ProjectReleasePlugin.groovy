package com.apphance.ameba.plugins.release

import com.apphance.ameba.configuration.release.ReleaseConfiguration
import com.apphance.ameba.plugins.project.tasks.CleanFlowTask
import com.apphance.ameba.plugins.release.tasks.BuildSourcesZipTask
import com.apphance.ameba.plugins.release.tasks.ImageMontageTask
import com.apphance.ameba.plugins.release.tasks.SendMailMessageTask
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.inject.Inject

import static com.apphance.ameba.configuration.reader.ConfigurationWizard.green
import static org.gradle.api.logging.Logging.getLogger

/**
 *
 * This is Ameba release plugin.
 *
 * Plugin for releasing projects.
 *
 * The plugin provides all the basic tasks required to prepare OTA release of
 * an application. It should be added after build plugin is added.
 *
 */
class ProjectReleasePlugin implements Plugin<Project> {

    def log = getLogger(this.class)

    @Inject ReleaseConfiguration releaseConf

    @Override
    void apply(Project project) {
        if (releaseConf.isEnabled()) {
            log.lifecycle("Applying plugin ${green(this.class.simpleName)}")

            project.configurations.add('mail')//TODO
            project.dependencies {
                mail 'org.apache.ant:ant-javamail:1.9.0'
                mail 'javax.mail:mail:1.4'
                mail 'javax.activation:activation:1.1.1'
            }

            project.task(ImageMontageTask.NAME,
                    type: ImageMontageTask)

            project.task(SendMailMessageTask.NAME,
                    type: SendMailMessageTask,
                    dependsOn: 'prepareMailMessage')

            project.task(BuildSourcesZipTask.NAME,
                    type: BuildSourcesZipTask)

            project.tasks.findByName(CleanFlowTask.NAME) << {
                releaseConf.otaDir.deleteDir()
                releaseConf.otaDir.mkdirs()
            }
        }
    }
}