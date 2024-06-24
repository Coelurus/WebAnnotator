workspace {

    model {
        user = person "User" {
            description "User of the data annotation application"
            tags "person"
        }
        admin = person "Admin" {
            description "Administrator of the application"
            tags "person"
        }
    
        aiSystem = softwareSystem "AI" {
            description "External application running AI, which learns from data provided by the annotator"
            tags "External"
            
        }
        
        sensorDataSystem = softwareSystem "Data Collection System" {
            description "External system for collecting data from sensors and cameras"
            tags "External"
            
            admin -> this "Captures data from sensor"

        }

        database = softwareSystem "Database" {
            description "Stores user data and metadata about logged data"
            tags "Database"
            #technology "MySQL"
        }

        disc = softwareSystem "Hard Drive" {
            tags "Drive"
            description "Storage for sensor and camera data and annotation data for projects"
        }

        
        

        annotatorSystem = softwareSystem "Web Annotator" {
            description "Web application for loading, annotating, and managing sensor data"

            

            webApp = container "Web Application" {
                user -> this "Uses"
                admin -> this "Admins"

                description "Frontend application providing the user interface"
                technology "HTML, CSS, JavaScript, Bootstrap"

                loginFrontend = component "Login Frontend" {
                    description "Screen for user to log in or reset his password"
                    tags "Screen"

                    user -> this "Logs into the application"
                    admin -> this "Logs into the application"
                }

                usersManagerFrontend = component "Users Manager Frontend" {
                    description "Screen for admin to perform CRUD operations on users and teams in system"
                    tags "Screen"

                    admin -> this "Manages users and teams"
                } 

                annotatorFrontend = component "Annotator Frontend" {
                    description "Screen with annotation graphic editor to edit and save annotations for projects"
                    tags "Screen"

                    user -> this "Annotates data"
                    #admin -> this "Annotates data"
                }

                fileSystemFrontend = component "File System Frontend"{
                    description "Screen to allow user to browse and filter his projects"
                    tags "Screen"

                    user -> this "Browse project files"
                    #admin -> this "Browse project files"
                }

                projectCreationFrontend = component "Project Creation Frontend"{
                    description "Loads and stores sensor and camera data and metadata for newly created project"
                    tags "Screen"

                    admin -> this "Creates new project"
                    this -> sensorDataSystem "Loads sensor and camera data"
                }

            }

            backendApp = container "Backend Application" {
                description "Backend application for data processing"
                technology "Java Spring Boot"

                webApp -> this "Sends API requests"

                api = component "API gateway"{
                    description "Core for handling and redirecting requests and returning responses"

                    webApp -> this "Sends API requests"

                    loginFrontend -> this "Request to verify user login details"

                    annotatorFrontend -> this "Request to CRUD project data / send data to AI"

                    usersManagerFrontend -> this "Request to CRUD users data"

                    fileSystemFrontend -> this "Request for project files file system structure"

                    projectCreationFrontend -> this "Request to create new project and save corresponding data"
                }

                dataLoader = component "Data Manager" {
                    description "Manages loading sensor and camera data and synchronizing it with corresponding annotation data file"

                    api -> this "Request for CRUD on edit data"
                    this -> database "Request for project metadata"
                    this -> disc "CRUD on project data"
                }

                loginValidator = component "Login Resolver" {
                    description "Component responsible for authentication and authorization"

                    api -> this "Request to verify login details"
                    this -> database "Request for user"
                }

                usersManager = component "Users Administrator" {
                    description "Component responsible for CRUD operations on users"

                    api -> this "Request for CRUD on users data"
                    this -> database "Modify user"
                }

                teamsManager = component "Teams Administrator" {
                    description "Component responsible for CRUD operations on teams"

                    api -> this "Request for CRUD on teams data"
                    this -> database "Modify team"
                }

                fileSystemManager = component "File System Manager"{
                    description "Manages browsing and retrieving in project files structure"

                    api -> this "Request for file system structure and metadata"
                    this -> disc "Load files structure"
                    this -> database "Request for projects metadata"
                }

                aiManager = component "AI communicator"{
                    description "Resolves communication with external AI system and feeding it with training / testing data"
                    
                    api -> this "Request for AI data training / testing"
                    this -> aiSystem "Sends data for training / Sends data for testing"
                    aiSystem -> this "Sends test results"
                    this -> disc "CRUD on project data"

                }             
            }
        }
    }

    views {

        container annotatorSystem {
            include *
            #autolayout lr
        }

        component backendApp {
            include *
            #autoLayout lr
        }

        component webApp {
            include *
            # autoLayout lr
        }
    
    

        theme default
        
        styles {

            element "External" {
                background #aaaaaa
                color #ffffff
                shape RoundedBox
            }

            element "Database"{
                shape cylinder
            }

            element "Drive"{
                shape Folder
            }

            element "Person"{
                shape Person
                background #1010CE
                color #ffffff
            }

            element "Screen"{
                shape WebBrowser
                background #1C66F0
                color #ffffff
            }
        }
    }
}
