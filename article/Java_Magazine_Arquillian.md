
![](http://design.jboss.org/arquillian/logo/final/arquillian_icon_64px.png) 
#Arquillian, een functionale primer 
De laatste periode is er een verschuiving gaande naar DevOps. Door deze verschuiving worden er ook steeds meer en vooral ander eisen gesteld aan de test methoden. Was het tot op heden voldoende om een unit test voor je code te schrijven zie je nu een verschuiving naar het uitvoeren van geautomatiseerde functionele en integratie testen. 

Arquillian speelt hier op in door op een voor de ontwikkelaars bekende wijze (lees JUnit style) test cases testen te kunnen ontwikkelen.

In dit artikel behandel ik de volgende zaken:
- Wat is het
- Waarom wil je het
- Hoe kan je architectuur het testen vereenvoudigen
- Belangrijke concepten
- Een voorbeeld.

# Wat is Arquillian
Arquillian is een plugable test framework dat meerdere containers onderstuend.

## Historie
Arquillian maakt nu ongeveer een jaar een buzz. Op zich vreemd wat de basis van Arquillian stamt uit 2009 en is een verdere ontwikkeling van het JBoss Test Harness. Voor de ontwikkeling van de CDI en Bean Validation JSR's was een TCK nodig, JBoss bood deze aan. De belangrijkste punten van dit harnass zijn:

 - Deklaratief annotatie gebaseerd
 - Start stop container en deploy een test
 - In en buiten container
 - Plugable.

Al snel was duidelijk dat dit iets was dat ook voor anderen van toegevoegde waarde zou zijn.

# Waarom is Arquillian hot?
Met de komst van EE6/7 en JDK8 is de noodzaak van frameworks zoals Spring verdwenen. Het gat dat Spring, Hibernate en anderen lang gevuld heeft is ondertussen allemaal opgenomen in de EE specificaties. zie ook de  JSR's voor JPA, CDI, Transaction management. 
Voor de meeste specificaties geldt dat de container al een standaard implementatie voor bijvoorbeeld JPA moet mee leveren. Dit verwijderd direct de noodzaak om naar libraries voor JPA terug te vallen.

Op het moment dat je gaat gebruiken van de standaard libraries is het wel nadelig dat je ook direct alle support voor testing kwijt bent. Spring heeft heeft in de loop van de jaren de nodige helper classes geintroduceerdom op eenvoudige wijze je test cases in elkaar te zetten.

Als je toch alleen van de standaarden gebruik wilt maken en niet wilt terug vallen op deze libraries is Arquillian een goede keuze.

Met behulp van Arquillian kan je op een eenvoudige wijze goede integratie testen maken, als je integratie testen correct worden uitgevoerd kan je er zeker van zijn dat je applicatie op je target containter werkt.



# Concepten
Om met Arquillian aan de slag te gaan is het belangrijk dat je de volgende concepten goed in het ook houd.

## Maven
Het gebruik van Arquillian en Maven is niet anders dan dat je al gewend bent. Er zijn wel een paar zaken die je in de gaten moeten houden. Hieronder staan een paar zaken die je zou moeten meenemen als je Arquillian integreert in je project. 

## Maven BOM's
Als je met Arquillian start heb je al snel last van _dependency hell_. Om dit op te lossen bied Arquillian Maven BOM's ( **B**ill **O**f **M**aterials) aan. Sinds de introductie van Maven 2.0.9 heeft maven het keyword _import_  ingevoerd. Hiermee wordt het mogelijk om in je pom files slechts een maal een dependency met een versie op te nemen en voor alle andere gerelateerde dependencies. Met de opname van deze import ben je zo goed als verlost van de dependency problemen. Andere aan Arquillian gerelateerde modules bieden ook BOM's, maak er gebruik van.

```
<dependencyManagement>
    <dependencies>
      <dependency>
       <groupId>com.test</groupId>
       <artifactId>bom</artifactId>
       <version>1.0.0</version>
       <type>pom</type>
       <scope>import</scope>
     </dependency>
   </dependencies>
 </dependencyManagement>
  
```
## Splitsing van Unit test en integratie tests
De test cases die je ontwikkeld zijn integratie test cases maak dat ook duidelijk in de opbouw van je source structuur. Zelf ben ik er altijd voor om in de __source__ tree een aparte source folder aan te maken met de naam __it__. In deze directory plaats ik alle integratie testen. Door middel van een Maven build profiles en de surefire plugin kan je op een eenvoudige manier wisselen tussen de 'normale' en de integratie testen.

Zoals we allemaal gebruik maken van TDD en BDD willen we een test set hebben die snel uitgevoerd kan worden. Helaas zijn integratie testen meestal niet de snelste. Het gebruik van gewone en integratie testen heb je wel een snelle terugkoppeling van de test resultaten en kan je op de build server zowel de gewone als integratie testen uitvoeren.

## Containers

Zoals al eerder vermeld is er een goede integratie met de bestaande test frameworks. De defacto standaarden voor Junit en TestNG zijn uiteraard niet overgeslagen. Andere zoals bijvoorbeeld JBehave zijn ook voorhanden.
Wat betreft de EE containers die onderstuend worden kan je eigenlijk wel zeggen dat alle ter zaken doende containers worden onderseund:
Weld, EJB, Glassfish, Wildfly, Websphere.
Mocht de container die je wilt gebruiken er niet zijn, is het relatief eenvoudig om een eigen container adapter te schrijven.

# Hoe schrijf ik een Aquilian test

Voor het schrijven van een Arquillian test heb je maar 1 annotatie nodig, **Deployment**.  De annotatie moet geplaatst worden op een *public static* method. Deze method moet een ShrinkWrap archive retouneren.
```
@Deployment
public static ShrinkWrap deployment() {
   ShrinkWrap archive = new Shrinkwrap.create(WebArchive.class, ...);
   //Vul het archive met dependencies en classes
   .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml"); 
   return archive;
}
```
Nadat je het archive gevuld hebt met de classes en dependencies die benodigd zijn kan je door middel van CDI annotaties de classes die je wilt testen injecteren en daar gebruik van maken op de bekende wijze voor bijvoorbeeld JUnit. In het bovenstaande voorbeeld wordt een *WebArchive* (war) gemaakt en omdat we gebruikt maken van CDI hebben we een *beans.xml* nodig.


<!--De belangrijke annotaties voor Arquillian, met name @Deployment @ArquillianResource en de samenwerking met de CDI annotaties (let op @Deployment(testable = true|false) 
-->

## Schrinkwrap
De interface van *ShrinkWrap* stelt je in staat om alle classes die nodig zijn voor het uitvoeren van je test toe tevoegen aan een archive dat door Arquillian depolyed wordt.
Het toevoegen van classes uit je eigen module/project gaat op de volgende wijze:
```
//Voeg alle classes van het package toe
JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addPackage(ClassToTest.class.getPackage());

//Voeg Specifieke toe
JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addClass(ClassToTest.class);
```

Door de fluent interface blijft de syntax begrijpelijk en compact ook als je veel aan het archive toevoegd. Het toevoegen van classes uit je eigen module is natuurlijk fijn. Een echte EE applicatie heeft natuurlijk niet alleen te maken met de classes die we zelf geschreven hebben, libraries en andere modules van ons project zijn ook nodig.
Voor de onderstuening van Maven dependencies is er de ShrinkWrap *Maven* class. Dit is niets meer dan een depenency resolver voor maven die een resultaat op levert dat je kan toevoegen aan je test archive.

```
final JavaArchive[] as = Maven.resolver()
            .loadPomFromFile("pom.xml")
            .resolve("nl.elucidator.arquillian.article.sample:model")
            .withTransitivity()
            .as(JavaArchive.class);

        for (JavaArchive a : as) {
            archive.merge(a);
        }
```

Wat doet bovenstaande nu precies; de method ```loadPomFromFile("pom.xml")``` laad de pom file van je huidige project in. Deze heb je nodig voor het uitzoeken en laden van je dependencies. methode ```resolve("nl.elucidator.arquillian.article.sample:model")``` zoekt in de dependencies van de pom file artifact ```model```. De versie isexpliciet niet meegegeven zodat hij door maven geresolved wordt. Vervolgens zeggen dat alle transitive dependencies van dit artifact meegenomen moeten worden, uiteraard is er ook de mogeleid om dit niet te doen. Alle dependencies worden als jar aan het resultaat toegevoegd. 

Een gemis in de ```ShrinkWrap``` interface vind ik dat er geen mogelijkheid is om het resultaat van de Maven resolver in een keer toe te voegen, nu moet je dat doormiddel van een merge doen voor elk element uit de lijst die je geresolved hebt.  

## @Deployment(__testable__=true|false)
Arquillian biedt twee vershillende test modus aan. In-Container en client mode. 

### In-Container mode
Wanneer je gebruik maakt van de In-Container mode, de default, voegt Arquillian diverse classes aan het archief toe om de testen te kunnen uitvoeren. Door deze teovoegingen is het mogelijk om tijdens de uitvoering van de testen te communiceren met de classen die getest worden. 
In deze modus worden de testen in de remote container uitgevoerd. Dit is de default.

### Client mode
De client mode is de eenvoudigste. Arquillian voegt in deze modus niks of in iedergeval minimaal toe aan het archive dat je gecreerd hebt. Na de deployment komt het systeem overeen alsof je deze op de daadwerkelijke container hebt gedployed. Nu kan je testen uit voeren als zijnde een client van de applicatie.

# Architectuur
Hoe kan een goede architectuur je helpen met het testen van de applicatie, en dan met name testen met Arquillian


## Samenvatting
* Zet maven project op
* Voeg Arquillian bom toe 
* Standaard test case
* Voeg methode geannoteerd met ```@Deployment```
* Voeg Classes en dependencies toe. 
* Voer de tests uit.

## Voorbeeld applicatie
Zie github


# Bronnen:
* http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Importing_Dependencies
* http://www.petrikainulainen.net/programming/maven/integration-testing-with-maven/
* https://docs.jboss.org/author/display/ARQ/Reference+Guide
