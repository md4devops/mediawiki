pipeline {

  agent any

options {
ansiColor("xterm")
}

stages {
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
