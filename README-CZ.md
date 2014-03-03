Cyril Quality Centre
--------------------
Toto je nástroj na definici integračních testů.

Úvod k české verzi
------------------
Toto je česká verze dokumentu README.md, měla by obsahovat stejné informace. Používam ji však také k ukládání svých myšlenek, což je pro mě rychlejší než v angličtině. Tento dokument je tedy často podrobnější než anglická verze, ale text je v surovější podobě.

Sestavení
---------
```shell
cd cyrilqc-core
./gradle clean all
```


Po úspěšném sestavení je vytvořen distribuční balík *build/distributions/cyrilqc-&lt;verze&gt;.zip*.

Spuštění
--------
CyrilQC je možno spustit jako samostatnou konzolovou aplikaci, nebo v rámci vašeho buldovacícho procesu pomocí standardních prostředků junit.

## Samostatná aplikace
Distribuční balík zatím není k dispozici ke stažení na internetu, je však možno si ho sestavit ze zdrojáků, viz předchozí kapitola. Následně je třeba balík rozbalit do libovolného adresáře na disku a spustit skript v adresáři *bin* odpovídající Vašemu operačnímu systému. Jako aktuální adresář při spuštění je nutno mít adresář s Vaším projektem - obsahující soubor *cyrilqc.xml*.


### MS Windows:
```shell
unzip cyrilqc-*.zip
cyrilqc-*/bin/cyrilqc.bat
```

### Unix/Linux:
```shell
unzip cyrilqc-*.zip
cyrilqc-*/bin/cyrilqc
```

## Maven projekt

### Instalace cyrilqc do lokálního repozitáře  
CyrilQC zatím není v oficialních Maven repozitářích, je třeba ho nainstalovat lokálního maven repozitáře pomocí targetu *install*:

```shell
cd cyrilqc-core
./gradle install
```

### Úprava pom.xml
Nyní je třeba přidat do Vašeho *pom.xml* závislost na artefakt com.cyrilqc:cyrilqc-core:
```xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>com.cyrilqc</groupId>
            <artifactId>cyrilqc-core</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    ...
</project>
```

### Vytvoření testu
Dále je třeba vytvořit Test spuštěný pomocí *com.cyrilqc.runner.junit4.JUnit4Runner*, vytvoříme tedy soubor *src/test/java/my/package/IntegrationTest.java* s obsahem:
  
```java
package my.package;
    
import org.junit.runner.RunWith;
import com.cyrilqc.runner.junit4.JUnit4Runner;
    
@RunWith(JUnit4Runner.class)
public class IntegrationTest {
}
```
    
### Vytvoření definice cyrilqc testů
V domovském adresáři vytvoříme soubor cyrilqc.xml s popisem testu, například tento jednoduchý základ:

```xml
<project>

	<target name="test-simple">
		<echo>THIS IS SIMPLE TEST</echo>
	</target>

	<target name="test-another">
		<echo>THIS IS ANOTHER TEST - EXPECTED FAILURE</echo>
		<fail/>
	</target>
    	
</project>
```
    
### Spuštění
Nyní spustíme testy Vašeho projektu obvyklým způsobem.
        
```shell
mvn test
```
    

Definiční soubor cyrilqc.xml
----------------------------
Tento soubor definuje testy spouštěné CyrilQC Enginem. Jde v zásadě o ant soubor splňující jisté konvence a používající specíalní ant tasky.

##Targety

Prefixy targetů jsou uvedeny v pořadí, v němž dochází k jejich spouštění.
###before-scan*
###after-scan*
###before-module*
Spouští se jednou pro celý projekt, před spuštěním prvního testu. Jestliže spuštění targetu skončí chybou, nejsou už spuštěny další targety before-module, ani žádný test, jsou však spuěštěny všechny after-module targety, které mají za úkol uklidit.
###test*
###after-test*
###before-test*
###after-module*
Spouští se jednou po všech testech na závěr celého procesu. Každý z techto targetu je spuštěn i v případě předchozi chyby v nějakém before-module či jiném after-module targetu. Je tedy brát na zřetel, že v okamžiku spustění after-module nemusí existovat zdroje, ktere měly bý vytvořeny v before-module


Licence
-------
Tento software je distribuován pod dvěma licencema - GPL3 nebo proprietální.
Úplný text opensorce GPL3 licence je na adrese http://www.gnu.org/licenses/gpl.html, pro získání sofwaru pod proprietální licencí kontaktuje cyril.sochor@gmail.com.

Support
-------
V případě potíží s použitím CyrilQC mě kontaktujte na adrese cyril.sochor@gmail.com.

