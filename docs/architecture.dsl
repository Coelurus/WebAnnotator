workspace {

    model {
        user = person "Uživatel" {
            description "Uživatel aplikace pro anotaci dat"
        }
        admin = person "Admin" {
            description "Administrátor aplikace"
        }
    
        aiSystem = softwareSystem "AI" {
            description "Externí aplikace provozující AI, jež se učí na datech poskytovaných anotátorem"
            tags "External"
            
        }
        
        sensorDataSystem = softwareSystem "Systém pro sběr dat" {
            description "Externí systém pro sběr dat ze senzoru a kamery"
            tags "External"
            #technology "Python, OpenCV, FFMPEG"

        }

        database = softwareSystem "Databáze" {
            description "Ukládá uživatelská data a metadata o logovaných datech"
            #technology "MySQL"
        }

        disc = softwareSystem "Pevný disk" {
            description "Úložiště pro data ze senzorů a kamery"
        }

        
        

        annotatorSystem = softwareSystem "Webový Anotátor" {
            description "Webová aplikace pro načítání, anotaci a správu dat ze senzorů"

            

            webApp = container "Webová aplikace" {
                description "Frontend aplikace poskytující uživatelské rozhraní"
                technology "HTML, CSS, JavaScript, Bootstrap"

                loginFrontend = component "Login Frontend" {
                    description "Obrazovka pro přihlášení uživatele"

                    user -> this "Přihlašuje se do aplikace"
                    admin -> this "Přihlašuje se do aplikace"
                }

                usersManagerFronted = component "Users Manager Frontend" {
                    description "Manage users and teams"

                    admin -> this "Spravuje uživatele a týmy"

                } 

                annotatorFrontend = component "Annotator Frontend" {
                    description "Obrazovka pro anotování projektů"

                    user -> this "Anotuje data"
                    admin -> this "Anotuje data"
                }
                
                user -> this "Používá"
                admin -> this "Spravuje"
            }

            backendApp = container "Backend aplikace" {
                description "Backendová aplikace pro zpracování dat"
                technology "Java Spring Boot"

                api = component "API"{
                    description "Core pro vyřízení požadavků"

                    loginFrontend -> this "Posílá žádost na ověření přihlašovacích údajů uživatele"
                    this -> loginFrontend "Vrací informaci o úspěchu"

                    annotatorFrontend -> this "Posílá žádost na data k projektu"
                    this -> annotatorFrontend "Vrací data o projektu"
                }

                dataLoader = component "Data manager" {
                    description "Spravuje data o projektech"

                    api -> this "Dotaz na data a informace k projektu"
                    this -> api "Projektová data"
                    this -> database "Dotaz na metadata o projektu"
                    this -> disc "Dotaz na data k projektu"
                }

                loginValidator = component "Login resolver" {
                    description "Komponente starající se o ověření platnosti přihlašovacích údajů"

                    api -> this "Dotaz na ověření přihlašovacích údajů"
                    this -> api "Informace o platnosti údajů"
                    this -> database "Dotaz na uživatele"
                }

                usersManager = component "Users manager" {
                    description "Komponenta spravující uživatele"

                    api -> this "Požadavek na upravení uživatele"
                    this -> api "Informace o úspěchu"
                }

                teamsManager = component "Teams manager" {
                    description "Komponenta spravující týmy"

                    api -> this "Požadavek na upravení týmů"
                    this -> api "Informace o úspěchu"
                }
               
                # AI
                this -> aiSystem "Posílá data k učení" "JSON"
                this -> aiSystem "Posílá data k tesování" "JSON"
                aiSystem -> this "Posílá výsledky testů" "JSON"
                
                #Senzor
                this -> sensorDataSystem "Načítá data"
            }


        }
        
        

    }

    views {
        systemContext annotatorSystem {
            include *
            autolayout lr
        }

        container annotatorSystem {
            include *
            autolayout lr
        }

        component backendApp {
            include *
            autoLayout lr
        }

        component webApp {
            include *
            autoLayout lr
        }

        theme default
        
        styles {
            element "External" {
                background #aaaaaa
                color #ffffff
                shape RoundedBox
            }
        }
    }
}