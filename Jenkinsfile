#!/usr/bin/env groovy

// Label will be set to the Jenkins BUILD_TAG aka ${JOB_NAME}-${BUILD_NUMBER}
// also removes problematic slashes and special characters to avoid problems
// with accessing the containers
// We prepend the "AK_" prefix to avoid special characters as first char.
String label = "AK_" + env.BUILD_TAG.replace("/", "_").replace("%2F", "_").replace("-", "_").replaceAll(" ", "").reverse().take(60).reverse()
String currentStage = 'Setup'

properties([
        // adjust thresholds as needed, but try to keep it as low as possible. This is already a good configuration.
        buildDiscarder(logRotator(artifactDaysToKeepStr: '360', artifactNumToKeepStr: '5', daysToKeepStr: '360', numToKeepStr: '10')),
        // disableConcurrentBuilds is mandatory when using Kubernetes or you should risk to broke everything
        disableConcurrentBuilds(),
        // this options force Jenkins to keep in memory build logs until the build is done
        durabilityHint('PERFORMANCE_OPTIMIZED'),
        // limit to 4 builds per hour, but also allow users to manually start the build
        [$class: 'JobPropertyImpl', throttle: [count: 4, durationName: 'hour', userBoost: true]]
])

timeout(time: 60, unit: 'MINUTES') {
    timestamps {
        podTemplate(
                label: label,
                cloud: 'k8s-ci-cd',
                namespace: 'lae-jenkins',
                inheritFrom: 'pod-base-configuration-with-dind',
                containers: [
                    containerTemplate(name: 'gradle',
                        image: 'gradle:6.4-jdk8',
                        ttyEnabled: true,
                        command: 'cat',
                        resourceRequestCpu: '500m',
                        resourceLimitCpu: '1000m',
                        resourceRequestMemory: '512Mi',
                        resourceLimitMemory: '2Gi')
        ], envVars: [
                envVar(key: 'BRANCH_NAME', value: env.BRANCH_NAME)
        ], volumes: [
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
        ]) {
            node(label) {
                try {
                    stage('Checkout') {
                        currentStage = 'Checkout'
                        timeout(time: 1, unit: 'MINUTES') {
                            checkout scm
                        }
                    }

                    stage('Test') {
                        currentStage = 'Test'
                        container('gradle') {
                            withCredentials([usernamePassword(credentialsId: 'azure-artifacts-manager', passwordVariable: 'AZURE_ARTIFACTS_PASSWORD', usernameVariable: 'AZURE_ARTIFACTS_EMAIL')]) {
                                sh "gradle clean allTests --stacktrace --info"
                            }
                        }
                    }

                    if (env.BRANCH_NAME == "azure_artifacts") { // TODO
                        stage('Deploy Artifact') {
                            currentStage = 'Deploy Artifact'
                            container('gradle') {
                                withCredentials([usernamePassword(credentialsId: 'azure-artifacts-manager', passwordVariable: 'AZURE_ARTIFACTS_PASSWORD', usernameVariable: 'AZURE_ARTIFACTS_EMAIL')]) {
                                    sh "gradle clean publish -PbuildVersion=${currentBuild.startTimeInMillis} --stacktrace"
                                }
                                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
                            }
                        }
                    }
                } catch (Throwable e) {
                    throw e
                }

            }
        }
    }
}
