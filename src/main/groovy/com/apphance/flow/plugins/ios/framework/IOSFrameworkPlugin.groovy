package com.apphance.flow.plugins.ios.framework

import com.apphance.flow.configuration.ios.IOSFrameworkConfiguration
import com.apphance.flow.configuration.ios.variants.AbstractIOSVariant
import com.apphance.flow.configuration.ios.variants.IOSVariantsConfiguration
import com.apphance.flow.plugins.ios.buildplugin.tasks.CopyMobileProvisionTask
import com.apphance.flow.plugins.ios.framework.tasks.BuildFrameworkTask
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.inject.Inject

/**
 * Plugin for preparing reports after successful IOS build.
 *
 * This plugins provides functionality of building shared framework for IOS projects.
 *
 * While iOS itself provides a number of frameworks (shared libraries) that
 * can be used in various projects. It is undocumented feature of iOS that one can create own
 * framework. This plugin closes the gap.
 */
class IOSFrameworkPlugin implements Plugin<Project> {

    @Inject IOSFrameworkConfiguration frameworkConf
    @Inject IOSVariantsConfiguration variantsConf

    @Override
    void apply(Project project) {
        if (frameworkConf.isEnabled()) {

            def task = project.task(BuildFrameworkTask.NAME,
                    type: BuildFrameworkTask,
                    dependsOn: [CopyMobileProvisionTask.NAME]) as BuildFrameworkTask

            task.variant = frameworkVariant()
        }
    }

    private AbstractIOSVariant frameworkVariant() {
        variantsConf.variants.find {
            it.name == frameworkConf.variantName.value
        }
    }
}
