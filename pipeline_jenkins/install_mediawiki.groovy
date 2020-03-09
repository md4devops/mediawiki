pipeline {

  agent any

options {
ansiColor("xterm")
}

stages {
    stage ('Install serveur LAMP') {
      when {
        expression { params.version != "migration" | params.version != "retour v1" }
      }
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_lamp.yml',
          inventory: 'inventories/alex/hosts',
          extras: '${verbose}'
        )
      }
    }
    stage ('Deploy appli') {
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      when {
        expression { params.version != "migration" | params.version != "retour v1" }
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_deploy.yml',
          inventory: 'inventories/alex/hosts',
          extras: '${verbose}'
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
          extras: '${verbose}'
        )
      }
    }
    stage ('Retour version 01.00.00') {
      when {
        expression { params.version == "retour v1" }
      }
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_retour.yml',
          inventory: 'inventories/alex/hosts',
          extras: '${verbose}'
        )
      }
    }
  }
}
