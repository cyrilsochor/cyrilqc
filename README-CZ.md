Cyril Quality Centre
--------------------
Toto je nástroj na definici integračních testů.

Úvod k české verzi
------------------
Toto je česká verze dokumentu README.md, měla by obsahovat stejné informace. Používam ji však také k ukládání svých myšlenek, což je pro mě rychlejší než v angličtině. Tento dokument je tedy často podrobnější než anglická verze, ale text je v surovější podobě.

Sestavení
---------
    cd cyrilqc-core
    ./gradle clean all

Spuštění
--------
    unzip cyrilqc-*.zip
    cyrilqc-*/bin/cyrilqc


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

