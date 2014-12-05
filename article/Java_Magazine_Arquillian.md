# Unit v.s. Integration testing
De laatste periode is er een verschuiving gaande naar DevOps. Door deze verschuiving worden er ook steeds meer en vooral ander eisen gesteld aan de test methoden. Was het tot op heden voldoende om een unit test voor je code te schrijven zie je nu een verschuiving naar het uitvoeren van geautomatiseerde functionele en integratie testen.

Maar er is meer aan de hand is deze verschuiving. De verschuiving heeft een oorzaak. De oorzaak ligt in het gat dat aanwezig is tussen de Unit test en de integratie testen die uitgevoerd worden.

Tegenwoordig weet iederen het belang van TDD en BDD, we schrijven allemaal braaf onze unit testen en zeggen dan een goed gevoel te hebben over de stabiliteit en de werking van de applicatie zoals die in productie ge-deployed wordt. Maar is dit wel een goede aanname?

De algemen tendens is dat dit niet het geval is. Met de komst van CDI, JPA, EJB3, JAX-RS en andere EE features worden we aan één kant minder afhankelijk van aparte libraries zoals Hibernate en Spring. De EE features stellen hogere eisen aan de applicatie server implementaties. Zou moeten nu een JPA provider beschikbaar stellen en ook het CDI deel. Ik denk dat het een goede beweging is. Hiermee wordt het mogelijk om lean en mean applicaties te schrijven die niet onmogelijk groot worden door dependencies die specifieke JSR's implementeren. Een beetje spring applicatie levert al snel een war file van 50 á 60Mb op. Dit terwijl de daadwerkelijke applicatie maar een war van 100k zou opleveren. Dergelijke kleine war files zijn mogelijk als je een applicatie ontwikkeld met alleen de EE7 specificaties. Aaron Gupta heeft er 2012 al een artikel over geschreven zie links. Conclusie van het artikel is, gebruik geen Spring maar richt je op de standaarden

Direct nadeel van deze aanpak is dat er direct ook een groot gat valt in de mogelijkheden voor het testen van de applicatie. Het test van EE applicaites is iets wat (nog) steeds niet in de specificaties wordt meegenomen.

#Wat gaat er mis?
Op zich is er niks mis met de oude vertrouwde unit test. Maar wat wordt er dan eigenlijk getest?
Over het algemeen kunnen we stellen dat een Unit test, een enkele API test en dat deze test in isolatie wordt uitgevoerd. Voordeel van deze aanpak is dat we kleine simpele testen hebben die snel uitgevoerd worden. bij voorkeur een hele suite van testen onder de minuut om er voor te zorgen dat ze regelmatig uitgevoerd worden tijdens het ontwikkel proces.

In veel gevallen wordt er geen integratie test uitgevoerd tijdens de ontwikkel fase. Op het moment dat de testers er mee aan de slag gaan is de applicatie gedeployed op een (test) omgeving. De tester voert op dat platform zijn testen 'geautomatiseerd' uit. Bij voorbeeld doormiddel van Selenium of Fitnesse. Door deze aanpak krijgen de ontwikkelaars pas in een laat stadium feedback over het gedrag van de applicatie in de container.
Ik hoor velen zeggen: __Ja maar ik doe wel integratie testen tijdens de ontwikkel fase!__ Dat zal in veel gevallen ook gebeuren. Maar hoe doen we die eigenlijk?

Vele integratie testen die uitgevoerd worden maken gebruik van mock's. Het gebruik van mocked objecten maakt het welliswaar eenvoudiger om een 'integratie' test te schrijven maar je moet he af vragen wat er nog getest wordt. Wat wordt er in de volgende code getest:

Implementatie class.
```Java
@Stateless @Local(UserRepository.class)
public MyJpaRepository implements UserRepository {
   @PersitanceContext
   private EntityManager em;

   public User create(User user) {
     em.persist(user);
     return user;
   }
}
```

Test class.
```Java
@RunWith(MockitoRunner.class)
public MyJpaRepositoryTest {
  @Mock
  EntityManager em;
  @InjectMocks
  MyJpaRepository repo;

  @Test
  public void insertUserTest() {
    User user = new User(...);
    User created = repo.create(user);
    verify(em, times(1)).persist(user);
    assertThat(created, equalTo(user));
  }
}
```
Als je bovenstaande code lijkt op het eerste gezicht een valide test case. We kijken of de entity manager aangeroepen wordt met de dat die we verwachten. Maar deze code is breekbaar. We herhalen ons zelf, ```verify(...)``` is precies het zelfde als ```em.persist(...)```, dus we vallen in het DRY principe. Tevens is het gebruik van de mock in deze context niet een goede toepassing van mocks.

We kunnen de zelfde test case ook met een echte zelf geinitialiseerde entity manager schrijven. Dit lost het probleem maar deels op. Het transaction management en fushing van de persistence context roepen we zelf direct aan in de test case.

#Micro deployments
Het uitvoeren van een volledige integratie test is veel werk. Hoe kunnen we toch op een beheersbare wijze integratie tests uitvoeren? Het sleutel wordt hiervoor is op dit moment micro deployments. Met deze definitie kunnen we ons systeem opdelen in kleine brokken die een functioneel geheel vormen. In het bovenstaande voorbeeld kunnen we een deployment doen van de ```UserRepository``` en zijn direct gebruikte classes. De combinatie van deze classes is de micro deployment. Als dit op de target container gedeployed kunnen we met de container ```EntityManager``` gebruiker voor de persistance. In combinatie met bijvoorbeeld een in memory database hebben we een echte integratie te pakken.

#De Software pyramid
De software pyramid is ontwikkeld door Mike Cohn en wordt beschreven in een van zijn boeken. Essentie van deze pyramide is dat hoe lager je komt hoe meer testen er zouden moeten zijn. Manuale testen zouden er niet of nauwelijks moeten zijn.
![](http://www.elucidator.nl/wp-content/uploads/2014/11/automatedtestingpyramid.png)

 Helaas zien we maar al te vaak dat deze pyramide omgedraaid wordt. We krijgen dan een ijshorentje.

 ![](http://www.elucidator.nl/wp-content/uploads/2014/11/softwaretestingicecreamconeantipattern.png)

 In dit horentje vallen een aantal zaken op; Een van de belangrijkste vind ik de grote focus op geautometiseerde GUI testen. Tools zoals selenium maken het inderdaad makkelijk om een applicatie door middel van de GUI testen. Het effect van deze werkwijze is dat de focus hier naar toe verschijft en de verdeling van de testen niet meer in verhouding is. Als de GUI testen maar goed gaan zal het met de applicatie verder ook wel goed zitten.
Als je alleen gebruik maakt van de GUI missen we een hoop testen die zich richten op de integratie. Tools zoals selenium zijn black box testers. En het enige wat zij testen zijn de transities tussen de pagina's 

#Essentie
Voor het goed uitvoeren van testen en met name de integratie testen willen we gebruik maken van micro deployments. Deze micro deployments omvatten een subsystem. Het uitvoeren van tests op dit subsysteem kunnen we vergelijken met een integratie test op de container die we gaan gebruiken. Natuurlijk onze vertrouwde tests met mocks zijn nog steeds noodzakelijk, de micro deployments stelt ons in staat om 'Automated Integration Tests' uit te voeren.

# Arquillian?
Wat heeft Arquillian hier mee te maken?
Arquillian is een  test framework voor het uitvoeren van deze Micro deployments en integratie testen. Het stelt onwikkelaars in staat om op voor  ontwikkelaars bekende wijze (lees JUnit style) test cases te kunnen ontwikkelen.

De basis van Arquillian stamt uit 2009 en is een verdere ontwikkeling van het JBoss Test Harness. Destijds onwikkeld als TCK voor de CDI en Bean Validation JSR's, JBoss bood deze aan. De belangrijkste punten van dit harnass zijn:

 - Deklaratief annotatie gebaseerd
 - Start stop container en deploy een test
 - In en buiten container
 - Plugable.
 - Meerdere containters 

Na een stilte periode is de ontwikkeling weer opgepakt er verder gegaan onder de naam **Arquillian**

Het werken met Arquillian is gemakkelijk. In de volgende paragrafen staan een aantal tips en aanbevelingen. Met deze zal je snel in staat zijn om maximaal gebruik te maken van Arquillian.

## Splitsing van Unit test en integratie tests
Omdat we allemaal gebruik maken van TDD en BDD willen we een test set hebben die snel uitgevoerd kan worden. Helaas zijn integratie testen meestal niet de snelste. Het gebruik van gewone en integratie testen heb je wel een snelle terugkoppeling van de test resultaten en kan je op de build server zowel de gewone als integratie testen uitvoeren.
De test cases die je ontwikkeld zijn integratie test cases maak dat ook duidelijk in de opbouw van je source structuur. Zelf ben ik er altijd voor om in de __source__ tree een aparte source folder aan te maken met de naam __it__. In deze directory plaats ik alle integratie testen. Door middel van een Maven build profiles en de surefire plugin kan je op een eenvoudige manier wisselen tussen de 'normale' en de integratie testen.

# "Hello world" met Aquilian

Voor het schrijven van een Arquillian test heb je maar 1 annotatie nodig, **Deployment**.  De annotatie moet geplaatst worden op een *public static* method. Deze method moet een ShrinkWrap archive retouneren.
```
@Inject
HelloWorld helloWorld;

@Deployment
public static ShrinkWrap deployment() {
   ShrinkWrap archive = new Shrinkwrap.create(JavaArchive.class, ...)
   .archive.addClass(HelloWorld.class)
   .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   return archive;
}
@Test
public void helloTest() {
  assertThat(helloWorld.hello("Arquillian"), is("Hello Arquillian));
}
```
Nadat je het archive gevuld hebt met de classes en dependencies die benodigd zijn kan je door middel van CDI annotaties de classes die je wilt testen injecteren en daar gebruik van maken op de bekende wijze voor bijvoorbeeld JUnit. In het bovenstaande voorbeeld wordt een *JavaArchive* (jar) gemaakt en omdat we gebruikt maken van CDI hebben we een *beans.xml* nodig.

##Arquillian dependencies

Als je initieel met Arquillian van start heb je al snel last van _dependency hell_. Om dit op te lossen bied Arquillian Maven BOM's ( **B**ill **O**f **M**aterials) aan. Sinds de introductie van Maven 2.0.9 heeft maven het keyword _import_  ingevoerd. Hiermee wordt het mogelijk om in je pom files slechts een maal een dependency met een versie op te nemen en voor alle andere gerelateerde dependencies. Met de opname van deze import ben je zo goed als verlost van de dependency problemen. Andere aan Arquillian gerelateerde modules bieden ook BOM's, maak er gebruik van.

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

<!--
## Containers

Zoals al eerder vermeld is er een goede integratie met de bestaande test frameworks. De defacto standaarden voor Junit en TestNG zijn uiteraard niet overgeslagen. Andere zoals bijvoorbeeld JBehave zijn ook voorhanden.
Wat betreft de EE containers die onderstuend worden kan je eigenlijk wel zeggen dat alle ter zaken doende containers worden onderseund:
Weld, EJB, Glassfish, Wildfly, Websphere.
Mocht de container die je wilt gebruiken er niet zijn, is het relatief eenvoudig om een eigen container adapter te schrijven.
-->


<!--De belangrijke annotaties voor Arquillian, met name @Deployment @ArquillianResource en de samenwerking met de CDI annotaties (let op @Deployment(testable = true|false)
-->

## System Under Test
Voor de uitvoeren van een Micro deployment maken we gebruik van een set classes en mogelijk andere  substemen. ```ShrinkWrap``` is de core van Arquillian. Het biedt een fluent API aan om deze op eenvoudige wijze te managen door bijvoorbeeld hele java packages in een maal toe te kunnen voegen.

Het toevoegen van classes uit je eigen module/project gaat op de volgende wijze:
```
//Voeg alle classes van het package toe
JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addPackage(ClassToTest.class.getPackage());

//Voeg een specifieke toe
JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addClass(ClassToTest.class);
```

Door de fluent interface blijft de syntax begrijpelijk en compact ook als je veel aan het archive toevoegd. Het toevoegen van classes uit je eigen module is natuurlijk gemakkelijk. Een echte EE applicatie heeft natuurlijk niet alleen te maken met de classes die we zelf geschreven hebben, libraries en andere modules van ons project zijn ook nodig.
Voor de onderstuening van Maven dependencies is er de *Maven* class. Dit is niets meer dan een depenency resolver voor maven die een resultaat op levert dat je kan toevoegen aan je test archive.

```
final webArchive[] as = Maven.resolver()
            .loadPomFromFile("pom.xml")
            .resolve("nl.elucidator.arquillian.article.sample:model")
            .withTransitivity()
            .as(JavaArchive.class);

        for (webArchive a : as) {
            archive.merge(a);
        }
```

Wat doet bovenstaande nu precies; de method ```loadPomFromFile("pom.xml")``` laad de pom file van je huidige project in. Deze heb je nodig voor het uitzoeken en laden van je dependencies. methode ```resolve("nl.elucidator.arquillian.article.sample:model")``` zoekt in de dependencies van de pom file artifact ```model```. De versie is expliciet niet meegegeven zodat hij door maven geresolved wordt. Vervolgens zeggen dat alle transitive dependencies van dit artifact meegenomen moeten worden, uiteraard is er ook de mogelijkheid om dit niet te doen. Alle dependencies worden als jar aan het resultaat toegevoegd.

Een gemis in de ```Maven``` interface vind ik dat er geen mogelijkheid is om het resultaat van de Maven resolver in een keer toe te voegen, nu moet je dat doormiddel van een merge doen voor elk element uit de lijst die je geresolved hebt.  

## Whitebox en Blackbox
Met behulp van de annotatie ```@Deployment``` kunnen we wisselen tussen een blackbox of whitebox test method. Arquillian gebruikt hier de terment In-Container en Client mode voor.

### In-Container mode
Wanneer je gebruik maakt van de In-Container mode, de default, voegt Arquillian diverse classes aan het archief toe om de testen te kunnen uitvoeren. Door deze toevoegingen is het mogelijk om tijdens de uitvoering van de testen te communiceren met de classen die getest worden.
In deze modus worden de testen in de remote container uitgevoerd. Dit is de default.

### Client mode
De client mode is de eenvoudigste. Arquillian voegt in deze modus niks of in iedergeval minimaal toe aan het archive dat je gecreerd hebt. Na de deployment komt het systeem overeen alsof je deze op de daadwerkelijke container hebt gedployed. Nu kan je testen uit voeren als zijnde een client van de applicatie.
Je kan deze mode activeren door middel van de volgende annotatie:
```
@Deployment(testable=false)
```

## Voorbeeld applicatie
Als ondersteuning voor dit artikel heb ik op github (https://github.com/elucidator/arquillian-sample) een simpele implementatie van een basale CRUD applicatie. In de code wordt gebruik gemaakt van verschillende technieken van de micro deployments getoond. 


# Bronnen:
* http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Importing_Dependencies
* http://www.petrikainulainen.net/programming/maven/integration-testing-with-maven/
* https://docs.jboss.org/author/display/ARQ/Reference+Guide
* http://www.mountaingoatsoftware.com/books (Testing Pyramid)
* https://blogs.oracle.com/arungupta/entry/why_java_ee_6_is

## Bio
Pieter van der Meer is Architect bij Ordina J-Tech. Heeft een focus op de ontwikkeling van applicaties met EE6/7 en JDK8. 
