package com.apphance.flow.plugins.ios.apphance.source

import com.apphance.flow.configuration.ios.variants.AbstractIOSVariant
import com.apphance.flow.configuration.properties.ApphanceModeProperty
import com.apphance.flow.plugins.ios.apphance.pbx.IOSApphancePbxEnhancer
import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.apphance.flow.configuration.apphance.ApphanceMode.QA
import static com.google.common.io.Files.createTempDir

class IOSApphanceSourceEnhancerSpec extends Specification {

    @Shared
    def projectDir = new File('testProjects/ios/GradleXCode')

    def tmpDir = createTempDir()

    def cleanup() {
        tmpDir.deleteDir()
    }

    def 'NSLog is replaced with APHLog'() {
        given:
        def filesToReplace = [
                'GradleXCode/subdir/emptyPathView.m',
                'GradleXCode/gradleXCodeAppDelegate.m',
                'GradleXCode/gradleXCodeViewController.m',
                'GradleXCode/main.m',
                'GradleXCode/subdir/NonEmptyPathView.m']

        new AntBuilder().copy(toDir: tmpDir.absolutePath) {
            fileset(dir: projectDir.absolutePath) {
                filesToReplace.each {
                    include(name: it)
                }
            }
        }

        and:
        def sourceEnhancer = new IOSApphanceSourceEnhancer(
                GroovyMock(AbstractIOSVariant) {
                    getTmpDir() >> tmpDir
                },
                GroovyMock(IOSApphancePbxEnhancer) {
                    getFilesToReplaceLogs() >> filesToReplace
                },

        )
        sourceEnhancer.ant = ProjectBuilder.builder().build().ant

        when:
        sourceEnhancer.replaceLogs()

        then:
        def files = filesToReplace.collect { new File(tmpDir, it) }
        !files.any { it.text.contains('NSLog') }
        files.any { it.text.contains('APHLog') }
    }

    def 'apphance is added to PCH file'() {
        given:
        def sourceEnhancer = new IOSApphanceSourceEnhancer(
                GroovyMock(AbstractIOSVariant) {
                    getTmpDir() >> tmpDir
                    getApphanceMode() >> new ApphanceModeProperty(value: QA)
                },
                GroovyMock(IOSApphancePbxEnhancer) {
                    getGCCPrefixFilePath() >> 'GradleXCode/GradleXCode-Prefix.pch'
                },
        )

        and:
        new AntBuilder().copy(toDir: tmpDir.absolutePath) {
            fileset(dir: projectDir.absolutePath) {
                include(name: 'GradleXCode/GradleXCode-Prefix.pch')
            }
        }

        when:
        sourceEnhancer.addApphanceToPch()

        then:
        def pch = new File(tmpDir, 'GradleXCode/GradleXCode-Prefix.pch')
        pch.text.contains('#import <Apphance-Pre-Production/APHLogger.h>')
    }

    def 'apphance init section is added'() {
        given:
        def sourceEnhancer = new IOSApphanceSourceEnhancer(
                GroovyMock(AbstractIOSVariant) {
                    getTmpDir() >> tmpDir
                    getApphanceMode() >> new ApphanceModeProperty(value: QA)
                    getApphanceAppKey() >> '3145abcd'
                },
                null
        )

        and:
        new AntBuilder().copy(toDir: tmpDir.absolutePath) {
            fileset(dir: projectDir.absolutePath) {
                include(name: 'GradleXCode/gradleXCodeAppDelegate.h')
                include(name: 'GradleXCode/gradleXCodeAppDelegate.m')
            }
        }

        when:
        sourceEnhancer.addApphanceInit()

        then:
        def mFileContent = new File(tmpDir, 'GradleXCode/gradleXCodeAppDelegate.m').text
        mFileContent.contains('[APHLogger startNewSessionWithApplicationKey:@"3145abcd" apphanceMode:kAPHApphanceModeQA];')
        mFileContent.contains('NSSetUncaughtExceptionHandler(&APHUncaughtExceptionHandler);')
    }

    def 'exception is thrown when no UIApplicationDelegate file found'() {
        given:
        def sourceEnhancer = new IOSApphanceSourceEnhancer(
                GroovyMock(AbstractIOSVariant) {
                    getTmpDir() >> tmpDir
                    getApphanceMode() >> new ApphanceModeProperty(value: QA)
                    getApphanceAppKey() >> '3145abcd'
                },
                null
        )

        when:
        sourceEnhancer.addApphanceInit()

        then:
        def e = thrown(GradleException)
        e.message == "Can not find UIApplicationDelegate file in dir: $tmpDir.absolutePath"
    }
}
