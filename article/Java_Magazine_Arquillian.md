#Arquillian, een functionale primer

De laatste periode is er een verschuiving gaande naar DevOps. Door deze verschuiving worden er ook steeds meer en vooral ander eisen gesteld aan de test methoden. 
Was het tot op heden voldoende om een unit test voor je code te schrijven zie je nu een verschuiving naar het uitvoeren van geautomatiseerde functionele en integratie testen. 
Arquillian speelt hier op in door op een voor de ontwikkelaars bekende wijze (lees JUnit style) deze testen te kunnen uitvoeren.

## Wat is Arquillian


## Historie
Arquillian maakt nu ongeveer een jaar een buzz. Op zich vreemd wat de basis van Arquillian stamt uit 2009 en is een verdere ontwikkeling van het JBoss Test Harness. Voor de ontwikkeling van de CDI en Bean Validation JSR's was een TCK nodig, JBoss bood deze aan. De belangrijkste punten van dit harnass zijn:

 - Deklaratief annotatie gebaseerd
 - Start stop container en deploy een test
 - In en buiten container
 - Plugable.

Al snel was duidelijk dat dit iets was dat ook voor anderen van toegevoegde waarde zou zijn.



## JUnit

## Maven/Jenkins

### Maven BOM's
Dependencies in het algemeen zijn lastig. Als je met Arquillian aan de slag gaat kom je al snel in de _dependency hell_. Om deze op te lossen bied Arquillian Maven BOM's ( **B**ill **O**f **M**aterials) aan. Sinds de introductie van Maven 2.0.9 heeft maven het keyword _import_ (zie ook: http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Importing_Dependencies) ingevoerd. Hiermee wordt het mogelijk om in je pom files slechts een maal een dependency met een versie op te nemen en voor alle andere gerelateerde dependencies niet meer. Ze worden door de BOM afgehandeld.


## Architectuur
Hoe kan een goede architectuur je helpen met het testen van de applicatie, en dan met name testen met Arquillian

## WebApps
Welke mogelijkheden hebben we voor het testen van een Web Containter.

## Voorbeeld applicatie
Zie github


