# Analysis

## Available software technologies

### Backend development

Recommended possibility: [Spring boot](https://spring.io/guides/gs/rest-service)

### Frontend

- [Spring](https://spring.io/blog/2021/12/17/client-side-development-with-spring-boot-applications)
  - [Project template](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.3.0&packaging=jar&jvmVersion=21&groupId=cz.cuni.mff.vopalenf&artifactId=annotator&name=annotator&description=Individual%20Software%20Project%20for%20annotating%20sensor%20data&packageName=cz.cuni.mff.vopalenf.annotator&dependencies=web)

## Similar programs

- [LabelBox](https://labelbox.com)(images)
- [iMerit](https://imerit.net/video-annotation-and-labeling-tool/)(video)
- [CVAT](https://www.cvat.ai)(free)

## HW technologies

### MGC3130 Hillstar Development Kit

- [User's guide](https://docs.rs-online.com/d6d0/A700000007850140.pdf)
- [Software](https://www.microchip.com/en-us/products/touch-and-gesture/3d-gestures/getting-started#Software)
- [Aurea (software for logging data)](https://ww1.microchip.com/downloads/aemDocuments/documents/OTH/ProductDocuments/UserGuides/Aurea-GUI-User-Guide-40001681E.pdf)


### Later changes

#### Thymeleaf -> JS React
- Since frontend has to be quite powerful to allow choosing multiple images to annotate etc. 
- I believe it would be easier to migrate to JS React instead of using Thymeleaf.
- Also it allows us to use REST approach which is very popular at the moment.

#### Annotating view
- https://www.npmjs.com/package/react-selecto - is for rectangular choices - not our case
- has to be custom made
