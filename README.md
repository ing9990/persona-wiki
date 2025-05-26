```text
persona-wiki         
  ├── Project Modules  
  │   ├── admin                    | Admin pages
  │   ├── authentication           | OAuth 2.0 social login (Kakao, Google, Naver)
  │   ├── common                   | Common utilities
  │   ├── monitor                  | Slack logging, Spring Actuator
  │   ├── notification-api         | Email, push notifications (Gmail)
  │   ├── storage-api              | Domain models, JPA entities
  │   ├── backend                  | Backend APIs
  │   └── web                      | Thymeleaf with Spring Boot  
  
  ├── External APIs
  │   ├── google-image-api         | Google Image API 
  │   └── wikipedia-api            | Wikipedia auto-figure search API
  
  ├── Deployment & Infrastructure
  │   ├── datas                    | Test SQL data
  │   ├── static                   | Static files (e.g., images, icons)
  │   ├── docker                   | Docker setup (MySQL, Redis, Grafana, Prometheus, Alertmanager)
  │   ├── deploy-script.sh         | Deployment script (Server)
  │   ├── .gitatributes            | 
  │   ├── default.conf             | Nginx configuration 
  │   └── docker-compose.yml       | Docker Compose configuration
  
  ├── GitHub & Conventions & Gradle
  │   ├── .githooks                 
  │   ├── .github                   
  │   └── .gitignore               
  └── Gradle
      ├── gradle                   
      ├── build.gradle.kts         
      ├── settings.gradle.kts      
      ├── gradle.properties        
      ├── gradlew                  
      └── gradlew.bat              
```
