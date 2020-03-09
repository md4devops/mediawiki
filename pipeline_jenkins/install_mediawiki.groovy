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
          branches: [[name: '*/develop']],
          doGenerateSubmoduleConfigurations: false,
          extensions: [],
          submoduleCfg: [],
          userRemoteConfigs: [[credentialsId: '03e6f26f-e1ff-45b3-8d96-c2f6e7200618', url: 'https://github.com/md4devops/mediawiki.git']]
        ])
}
    }
    stage ('Install serveur LAMP') {
      when {
        expression { params.version != "migration" }
      }
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_lamp.yml',
          inventory: 'inventories/alex/hosts',
          extras: '${VERBOSE}'
        )
      }
    }
    stage ('Deploy appli') {
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      when {
        expression { params.version != "migration" }
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_deploy.yml',
          inventory: 'inventories/alex/hosts',
          extras: '${VERBOSE}'
        )
      }
    }
    stage ('Migration 01.01.00') {
      when {
        expression { params.version == "01.01.00" | params.version == 'migration' }
      }
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_migration.yml',
          inventory: 'inventories/alex/hosts',
          extras: '${VERBOSE}'
        )
      }
    }
  }
}
