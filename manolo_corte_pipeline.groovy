// Manolo Corte Technical interiviewm, Jenkins section
pipeline {
    agent any
    stages {
        // Copying Subversion repository 
        stage('Copy Subversion') {
            steps {
                echo 'Checking out repo in temporary dir'
                dir('/temp_dir'){
                    svn 'http://svn.example.com:9834/repos'
                }
            }
        }
        // Setting the directory to the previous code, pulling git code into it. 
        stage('Copy Git code') {
            steps {
                dir('/temp_dir/subv_repo/git-code'){
                    // Get some code from a GitHub repository
                    git url: 'https://github.com/manolocorte/hp-intervirew-practice.git', branch: 'main'   
                }
            }
        }
        // Moving to the makefile directory (whatever that may be) and building it        
        stage('Build') {
            steps {
                dir('/temp_dir/subv_repo/git-code/makefile_dir'){
                    sh'make install' 
                    
                }
            }
        }
        // Moving to the makefile tests directory (whatever that may be) and running it, 
        // given this is it's own stage, if it fails, it will be handled in the post section. 
        stage('Test') {
            steps {
                dir('/temp_dir/subv_repo/git-code/testfile_dir'){
                    sh 'make test'
                }
            }
        }
    }
   post {
        // If all stages succeed, this conditional will be triggered
        success {
            echo "TESTS SUCCEEDED, NOTIFYING VIA EMAIL AND PUSHING TO GIT"
            emailext body: 'Test body SUCCESS', subject: 'TEST HEADER', to: 'manolo.corte@protnmail.com'
            withCredentials([usernamePassword(credentialsId: 'fixed',
                 usernameVariable: 'username',
                 passwordVariable: 'password')]){
            sh("git push http://$username:$password@git.corp.mycompany.com/repo")
            }
            
        } 
        // Section executed if a stage fails. 
        failure {
            echo "TESTS FAILED, NOTIFYING VIA EMAIL"
            emailext body: 'Test body FAILURE', subject: 'TEST HEADER', to: 'manolo.corte@protnmail.com'
        }
        always {
            // Preserving whatever logs might exist from tests or running makefiles
            // It would also be needed to give the .zip file a unique name, such as a timestamp or version
            // The logs however, might be handled by another tool .
                dir('/temp_dir/subv_repo/'){
                    sh 'zip -r logs.zip ./logs'
                    sh 'mv ./logs.zip /some/other/dir/logs.zip' 
                }
            // Once the logs are preserved, either the way described above, or some other, the temporary repo can be deleted
                dir('/'){
                    echo "Deleting temporary dir"
                    sh 'rm -r ./temp_dir'
                }
        }
    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
