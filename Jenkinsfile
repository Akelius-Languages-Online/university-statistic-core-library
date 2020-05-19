#!/usr/bin/env groovy

// Label will be set to the Jenkins BUILD_TAG aka ${JOB_NAME}-${BUILD_NUMBER}
// also removes problematic slashes and special characters to avoid problems
// with accessing the containers
// We prepend the "AK_" prefix to avoid special characters as first char.
String label = "AK_" + env.BUILD_TAG.replace("/", "_").replace("%2F", "_").replace("-", "_").replaceAll(" ", "").reverse().take(60).reverse()

String slackChannel = '#university-pipelines'
String currentStage = 'Setup'

properties([
        // adjust thresholds as needed, but try to keep it as low as possible. This is already a good configuration.
        buildDiscarder(logRotator(artifactDaysToKeepStr: '10', artifactNumToKeepStr: '5', daysToKeepStr: '10', numToKeepStr: '10')),
        // disableConcurrentBuilds is mandatory when using Kubernetes or you should risk to broke everything
        disableConcurrentBuilds(),
        // this options force Jenkins to keep in memory build logs until the build is done
        durabilityHint('PERFORMANCE_OPTIMIZED'),
        // limit to 4 builds per hour, but also allow users to manually start the build
        [$class: 'JobPropertyImpl', throttle: [count: 4, durationName: 'hour', userBoost: true]]
])

def notifyOnSlack(String message, String channel, String color) {
    slackSend(message: message, channel: channel, color: color, token: "dm2VVI1A01G6iwYrfD5kBM5u")
}

timeout(time: 10, unit: 'MINUTES') {
    timestamps {
        podTemplate(
                label: label,
                annotations: [
                        podAnnotation(key: 'kube-slack/slack-channel', value: '#university-k8s')
                ],
                cloud: 'k8s-ci-cd',
                namespace: 'business-school', containers: [
                containerTemplate(name: 'gradle', image: 'gradle:6.4-jdk8', ttyEnabled: true, command: 'cat',
                        resourceRequestCpu: '500m',
                        resourceLimitCpu: '1000m',
                        resourceRequestMemory: '512Mi',
                        resourceLimitMemory: '1.5Gi')
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
                            withCredentials([usernamePassword(credentialsId: 'artifactory.lae', passwordVariable: 'ARTIFACTORY_PASSWORD', usernameVariable: 'ARTIFACTORY_USERNAME')]) {
                                sh "echo ${ARTIFACTORY_PASSWORD} > artifactoryPw"
                                sh "echo ${ARTIFACTORY_USERNAME} > artifactoryUser"
                                sh "export ARTIFACTORY_PASSWORD=\$(cat artifactoryPw) && export ARTIFACTORY_USERNAME=\$(cat artifactoryUser)"
                                sh "gradle clean allTests --stacktrace"
                                sh "rm artifactoryPw && rm artifactoryUser"
                            }
                        }
                    }

                    if (env.BRANCH_NAME == "master") {
                        stage('Deploy Artifact') {
                            currentStage = 'Deploy Artifact'
                            container('gradle') {
                                withCredentials([usernamePassword(credentialsId: 'artifactory.lae', passwordVariable: 'ARTIFACTORY_PASSWORD', usernameVariable: 'ARTIFACTORY_USERNAME')]) {
                                    sh "echo ${ARTIFACTORY_PASSWORD} > artifactoryPw"
                                    sh "echo ${ARTIFACTORY_USERNAME} > artifactoryUser"
                                    sh "export MASTER_BRANCH_BUILD_VERSION=${currentBuild.number}"
                                    sh "echo \"Current version = ${MASTER_BRANCH_BUILD_VERSION}\""
                                    sh "export ARTIFACTORY_PASSWORD=\$(cat artifactoryPw) && export ARTIFACTORY_USERNAME=\$(cat artifactoryUser)"
                                    sh "gradle clean publish --stacktrace"
                                    sh "rm artifactoryPw && rm artifactoryUser"
                                }
                                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
                            }
                        }
                    }

                    String message = "Build <${env.BUILD_URL}|*${currentBuild.displayName}*> for *commons ${env.BRANCH_NAME}* successfuly deployed to artifactory :beer:"
                    notifyOnSlack(message, slackChannel, 'good')


                } catch (Throwable e) {
                    String message = "@here Build <${env.BUILD_URL}|*${currentBuild.displayName}*> failed for *commons ${env.BRANCH_NAME}* at stage *${currentStage}* :scream_cat: \n${e.toString()}"
                    notifyOnSlack(message, slackChannel, 'bad')

                    throw e
                }

            }
        }
    }
}