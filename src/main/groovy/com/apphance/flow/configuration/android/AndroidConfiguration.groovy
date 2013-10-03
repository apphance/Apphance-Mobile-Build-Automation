package com.apphance.flow.configuration.android

import com.apphance.flow.configuration.ProjectConfiguration
import com.apphance.flow.configuration.properties.StringProperty
import com.apphance.flow.executor.AndroidExecutor
import com.apphance.flow.plugins.android.parsers.AndroidBuildXmlHelper
import com.apphance.flow.plugins.android.parsers.AndroidManifestHelper
import com.google.inject.Singleton

import javax.inject.Inject

import static com.apphance.flow.detection.project.ProjectType.ANDROID
import static com.google.common.base.Strings.isNullOrEmpty

@Singleton
class AndroidConfiguration extends ProjectConfiguration {

    String configurationName = 'Android Configuration'

    @Inject AndroidBuildXmlHelper buildXmlHelper
    @Inject AndroidManifestHelper manifestHelper
    @Inject AndroidExecutor androidExecutor

    private Properties androidProperties

    @Override
    @Inject
    void init() {
        super.init()
        readProperties()
    }

    @Override
    boolean isEnabled() {
        projectTypeDetector.detectProjectType(project.rootDir) == ANDROID
    }

    StringProperty projectName = new StringProperty(
            name: 'android.project.name',
            message: "Project name. This property is used with command: 'android update project --name' before every build.",
            defaultValue: { defaultName() },
            possibleValues: { possibleNames() },
            required: { true }
    )

    private String defaultName() {
        buildXmlHelper.projectName(rootDir)
    }

    private List<String> possibleNames() {
        [rootDir.name, defaultName()].findAll { !it?.trim()?.empty }
    }

    @Override
    String getVersionCode() {
        extVersionCode ?: manifestHelper.readVersion(rootDir).versionCode ?: ''
    }

    @Override
    String getVersionString() {
        extVersionString ?: manifestHelper.readVersion(rootDir).versionString ?: ''
    }

    File getResDir() {
        project.file('res')
    }

    def target = new StringProperty(
            name: 'android.target',
            message: "Android target. This property is used with command: 'android update project --target' before every build.",
            defaultValue: { androidProperties.getProperty('target') ?: '' },
            required: { true },
            possibleValues: { possibleTargets() },
            validator: { it in possibleTargets() }
    )

    private List<String> possibleTargets() {
        androidExecutor.targets
    }

    Collection<String> sourceExcludes = super.sourceExcludes + ['**/*.class', '**/bin/**']

    def readProperties() {
        androidProperties = new Properties()
        ['local', 'build', 'default', 'project'].each {
            File propFile = project.file("${it}.properties")
            if (propFile?.exists()) {
                androidProperties.load(new FileInputStream(propFile))
            }
        }
    }

    @Override
    void checkProperties() {
        check !isNullOrEmpty(reader.envVariable('ANDROID_HOME')), "Environment variable 'ANDROID_HOME' must be set!"
        check !isNullOrEmpty(projectName.value), "Property ${projectName.name} must be set!"
        check versionValidator.isNumber(versionCode), bundle.getString('exception.android.version.code')
        check versionValidator.hasNoWhiteSpace(versionString), bundle.getString('exception.android.version.string')
        check target.validator(target.value), "Property ${target.name} is incorrect." +
                (target.value ? " Probably target $target.value is not installed in your system" : '')
    }
}
