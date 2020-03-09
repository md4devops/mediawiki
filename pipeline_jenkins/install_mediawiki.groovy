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

    stage ('Invoke Ansible Playbook install_mediawiki.yml') {
      environment {
        ANSIBLE_FORCE_COLOR = true
      }
      steps {
        ansiblePlaybook (
          colorized: true,
          playbook: 'install_mediawiki.yml',
          inventory: 'inventories/hosts',
          extras: '${VERBOSE}'
        )
      }
    }
  }
}
