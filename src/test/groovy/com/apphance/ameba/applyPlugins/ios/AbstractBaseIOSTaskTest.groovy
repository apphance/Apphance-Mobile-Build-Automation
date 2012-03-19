package com.apphance.ameba.applyPlugins.ios

import java.util.Collection

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import com.apphance.ameba.applyPlugins.android.BaseTaskTest;
import com.apphance.ameba.ios.plugins.buildplugin.IOSProjectProperty;
import com.apphance.ameba.plugins.projectconfiguration.ProjectConfigurationPlugin;

abstract class AbstractBaseIOSTaskTest extends BaseTaskTest {
    protected Project getProject() {
        ProjectBuilder projectBuilder = ProjectBuilder.builder()
        projectBuilder.withProjectDir(new File("testProjects/ios/GradleXCode"))
        Project project = projectBuilder.build()
        project.ext[IOSProjectProperty.PLIST_FILE.propertyName] = 'Test.plist'
        project.ext[IOSProjectProperty.DISTRIBUTION_DIR.propertyName] = 'release/distribution_resources'
        project.project.plugins.apply(ProjectConfigurationPlugin.class)
        return project
    }
}
