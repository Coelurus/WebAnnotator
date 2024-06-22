workspace {

    model {
        user = person "User" {
            description "User of the data annotation application"
        }
        admin = person "Admin" {
            description "Administrator of the application"
        }
    
        aiSystem = softwareSystem "AI" {
            description "External application running AI, which learns from data provided by the annotator"
            tags "External"
            
        }
        
        sensorDataSystem = softwareSystem "Data Collection System" {
            description "External system for collecting data from sensors and cameras"
            tags "External"
            #technology "Python, OpenCV, FFMPEG"

        }

        database = softwareSystem "Database" {
            description "Stores user data and metadata about logged data"
            tags "Database"
            #technology "MySQL"
        }

        disc = softwareSystem "Hard Disk" {
            tags "Drive"
            description "Storage for sensor and camera data"
        }

        
        

        annotatorSystem = softwareSystem "Web Annotator" {
            description "Web application for loading, annotating, and managing sensor data"

            

            webApp = container "Web Application" {
                user -> this "Uses"
                admin -> this "Admins"

                description "Frontend application providing the user interface"
                technology "HTML, CSS, JavaScript, Bootstrap"

                loginFrontend = component "Login Frontend" {
                    description "User login screen"

                    user -> this "Logs into the application"
                    admin -> this "Logs into the application"
                }

                usersManagerFrontend = component "Users Manager Frontend" {
                    description "Manage users and teams"

                    admin -> this "Manages users and teams"
                } 

                annotatorFrontend = component "Annotator Frontend" {
                    description "Project annotation screen"

                    user -> this "Annotates data"
                    admin -> this "Annotates data"
                }

            }

            backendApp = container "Backend Application" {
                description "Backend application for data processing"
                technology "Java Spring Boot"

                webApp -> this "Sends API requests"
                this -> webApp "Returns responses"

                api = component "API"{
                    description "Core for handling requests"

                    webApp -> this "Sends API requests"
                    this -> webApp "Returns responses"

                    loginFrontend -> this "Sends a request to verify user login details"
                    this -> loginFrontend "Returns success information"

                    annotatorFrontend -> this "Sends a request for project data"
                    this -> annotatorFrontend "Returns project data"

                    usersManagerFrontend -> this "Sends a request to modify user"
                    this -> usersManagerFrontend "Return success information"
                    usersManagerFrontend -> this "Sends a request to get users data"
                    this -> usersManagerFrontend "Returns users data"
                }

                dataLoader = component "Data Manager" {
                    description "Manages project data"

                    api -> this "Request for project data and information"
                    this -> api "Returns project data"
                    this -> database "Request for project metadata"
                    this -> disc "Request for project data"
                }

                loginValidator = component "Login Resolver" {
                    description "Component responsible for verifying the validity of login details"

                    api -> this "Request to verify login details"
                    this -> api "Returns information on the validity of login"
                    this -> database "Request for user"
                }

                usersManager = component "Users Manager" {
                    description "Component managing users"

                    api -> this "Request to modify user"
                    this -> api "Returns success information"

                    this -> database "Modify user"
                }

                teamsManager = component "Teams Manager" {
                    description "Component managing teams"

                    api -> this "Request to modify teams"
                    this -> api "Success information"
                    this -> database "Modify team"
                }
               
                # AI
                this -> aiSystem "Sends data for training" "JSON"
                this -> aiSystem "Sends data for testing" "JSON"
                aiSystem -> this "Sends test results" "JSON"
                
                # Sensor
                this -> sensorDataSystem "Loads data"

                # Actors
                
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
        }
    }
}
