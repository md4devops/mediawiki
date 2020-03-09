pipeline {

  agent any

options {
ansiColor("xterm")
}

stages {

stage ('Clonage repo GIT') {
steps {
checkout([
          $class: 'GitSCM',
          branches: [[name: '*/master']],
          doGenerateSubmoduleConfigurations: false,
          extensions: [],
          submoduleCfg: [],
          userRemoteConfigs: [[credentialsId: '03e6f26f-e1ff-45b3-8d96-c2f6e7200618', url: 'https://github.com/md4devops/mediawiki.git']]
        ])
}
    }
    stage ('Install serveur LAMP') {
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_lamp.yml',
          inventory: 'inventories/hosts',
          extras: '${VERBOSE}'
        )
      }
    }
    stage ('Deploy appli') {
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_deploy.yml',
          inventory: 'inventories/hosts',
          extras: '${VERBOSE}'
        )
      }
    }
    if ( ${version} == "01.01.00") {
      stage ('Migration 01.01.00') {
        environment {
          ANSIBLE_FORCE_COLOR = true
        }
        steps {
          ansiblePlaybook (
            colorized: true,
            playbook: 'install_migration.yml',
            inventory: 'inventories/hosts',
            extras: '${VERBOSE}'
          )
        }
      }
    }
  }
}
