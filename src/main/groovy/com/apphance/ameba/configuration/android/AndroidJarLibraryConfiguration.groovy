package com.apphance.ameba.configuration.android

import com.apphance.ameba.configuration.Configuration
import com.apphance.ameba.configuration.properties.StringProperty
import com.google.inject.Inject

@com.google.inject.Singleton
class AndroidJarLibraryConfiguration extends Configuration {

    final String configurationName = 'Android Jar Library Configuration'

    private boolean enabled = false

    private AndroidConfiguration androidConfiguration

    @Inject
    AndroidJarLibraryConfiguration(AndroidConfiguration androidConfiguration) {
        this.androidConfiguration = androidConfiguration
    }

    @Override
    boolean isEnabled() {
        this.@enabled
    }

    @Override
    void setEnabled(boolean enabled) {
        this.@enabled = enabled
    }

    @Override
    boolean isActive() {
        this.@enabled && androidConfiguration.active
    }

    def resourcePrefix = new StringProperty(
            name: 'android.jarLibrary.resPrefix',
            message: 'Internal directory name used to embed resources in the jar',
            defaultValue: { '' }
    )
}
