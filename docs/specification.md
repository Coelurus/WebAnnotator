# Podrobná Specifikace Webové Aplikace

## Úvod
Tato specifikace popisuje webovou aplikaci pro načítání, anotaci a správu dat ze senzorů. Aplikace bude vyvinuta pomocí Java Spring Boot pro frontend i backend. Cílem je vytvořit nástroj, který umožní uživatelům pracovat s daty, anotovat je a připravit je pro trénink a testování modelů umělé inteligence.

## Zamýšlené Funkce a Vlastnosti

### Hlavní Funkce
1. **Načítání dat ze senzoru**: Uživatel bude moci načíst záznam dat ze souborů uložených v souborovém systému.
2. **Správa souborového systému**: Procházení složkami a soubory v souborovém systému. Mazání či nahrávání nových dat ze senzoru.
3. **Definice anotování** Možnost definice dalších štítků pro anotování dat.
4. **Anotace dat**: Možnost anotovat data pomocí předpřipravených a uživatelem definovaných štítků.
5. **Export anotovaných dat**: Export anotovaných dat pro trénink umělé inteligence.
6. **Testování anotace AI**: Posílání dat vycvičené síti AI a kontrola výsledných anotací.
7. **Správa uživatelů** Správa účtů a týmů pracujících se systémem.

### Vedlejší Funkce
1. **Sběr dat** (Python) Skript pro zahájení nahrávání dat ze senzoru a z kamery ve stejný čas.
2. **Synchronizace dat** (Python + ffmpeg)  Skript pro synchronizaci snímaného ze senzoru a z kamery.


### Vlastnosti
- Uživatelsky přívětivé rozhraní pro správu a anotaci dat.
- Možnost tvorby vlastních štítků.
- Možnost anotovat po jednom či vybrat větší skupinu.
- Podpora více uživatelských účtů a správa jejich oprávnění.
    - Admin může nahrávat a mazat nasnímaná data.
    - Ostatní mohou pouze anotovat
- Možnost ukládání a načítání projektů.
- Klávesové zkratky pro rychlejší používání.

### Funkce podrobněji
- Nahrávání dat:
    - Systém poskytuje formulář pro:
        - Název projektu
        - Nahrání log souboru 
        - (Nahrání snímků)
        - Výběru deadlinu
        - Výběr priority
        - Výběr týmu/zaměstnance, kterému je přiřazen
        - Přidání klíčových slov / štítků pro označení projektu
    - Systém defaultně označí projekt jako nezačatý a neprochází testy (tzn. AI ho správně označila)

- Práce s projektem:
    - Systém zobrazí:
        - Název projektu
        - Štítky
        - Deadline
        - prioritu
        - seznam štítků 
        - možnost vytvořit nový štítek
        - možnost nastavení frekvence samplování
        - možnost uložení a opuštění projektu
        - možnost poslat data AI na natrénování
        - možnost spuštení AI na tato data
        - grafické zobrazení logu
            - pro každý záznam (v určité frelvenci)
                - okénko s pozicí ruky v prostoru (3d grafika)
                - odpovídající snímek z videa
    - Systém ukládá změny průběžně
    - Uživatel může
        - Tvořit / vybírat štítky 
        - Označovat / odoznačovat snímky štítky
            - Výběr buď:
                - po jednom
                - označení více
                - označení všech
        - Krok zpět / krok vpřed
        - Nechat si vyznačit jen snímky označené určitým štítkem
        - Uložit změny
        - Změnit stav projektu
        - Opustit projekt
        - Poslat data AI na natrénování
        - Spustit AI na tato data

- File system:
    - Systém zobrazí strukturu složek pro každý den
    - po kliknutí se zobrazí seznam záznamů / projektů pro ten den
    - možnost filtrovat (pak se zobrazi odpovídající soubory a ne složky):
        - dle intervalu záznamu
        - dle přiřazení (mně/mému týmu)
        - dle priority
        - dle štítku 
        - dle stavu projektu (nezačatý, rozpracovaný)
        - dle průchodu testů
    - možnost seřadit:
        - dle deadline 
        - dle priority
        - dle dne vytvoření 
        - dle délky záznamu
    - u každého souboru ve FS je vidět stav rozpracování 
    - složka pro každý den je označena: 
        - dokončena/nedokončena dle stavu projektů v ní
        - prochází/neprochází dle toho, jestli AI správně označila projekty v ní
    - uživatel má též možnost v menu rozkliknout seznam jemu zadaných projektů

- Tvorba uživatelů a týmů:
- admin uživatel má v menu možnosti:
    - spravovat uživatele
        - možnost vytvořit nového
        - možnost smazat uživatele
        - možnost deaktivovat účet
        - správa pravomocí
        - přeřazení do jiného týmu
    - spravovat týmy
        - možnost tvořit
            - Systém zobrazí formulář pro:
                - Název 
                - vedoucího 
                - seznam uživatelů na přiřazení 
        - možnost mazat týmy
            - smaže se pouze značka týmu, ne samotné účty uživatelů
        - možnost upravovat týmy 
            - změna názvu 
            - změna vedoucího 
            - změna seznamu členů

- Komunikace a práce s AI
    - TODO
    - Uživatel v projektu může poslat AI testovací data na anotování
    - Systém pak zobrazí výsledky AI na testovacích datech
        - Viditelně označí skupiny snímků, které:
            - AI oanotovala správně
            - AI oanotovala chybně
    - Pokud AI označí data správně, projekt celý se označí jako že prochází testy
    - Uživatel při průchodu file systemem může též odeslat celou jednu složku na otestování AI
        - V tom případě se postupně posílají všechny dokončené projekty ze složky a testují, zda je AI označí správně


### Vzorové use cases
Use case Admin
1. Uživatel se přihlásí do aplikace 
2. V hlavním menu si vybere akci vytvořit nový/smazat záznam. 
3. Pokud vybral smazat:
    - Systém zobrazí dialog o potvrzení 
    - Pokud uživatel potvrdí, tak systém smaže záznam 
4. Pokud vybral vytvořit:
    - Systém zobrazí formulář na nahrání logu ( + snímků/videa?)
    - Uživatel nahraje log, pojmenuje záznam, přidá klíčová slova/štítky, přiřadí tým/určitého zaměstnance k projektu. 
    - Systém uloží nahrané záznamy a potvrdí úspěšné nahrání souborů

Use case Uživatel
1. Uživatel se přihlásí do aplikace 
2. V menu vybere buď:
    - Návrat k nedávnemu projektu
    - Prohledávání souborů 
         - Uživatel si vybere záznam ze složky pro určitý den
    - Uživatel si zobrazí jemu přiřazené projekty
3.  Sytém zobrazí UI pro anotování
4. Uživatel může vytvořit vlastní štítky.
5. Uživatel označí snímky štítky.
6. Uživatel klikne na uložit a ukončit
7. Systém se zeptá, v jakém stavu je projekt
8. Uživatel vybere možnost buď: rozpracovaný či hotový 
9. Systém uloží změny, přiřadí štítky a označí projekt dle výběru uživatele. 
10. Systém informuje o úspěšném uložení dat.

## Analýza Existujících Řešení

### Existující Řešení
1. **Labelbox**: Nabízí rozsáhlé možnosti anotace dat a integrace s AI, ale je to komerční řešení s vysokými náklady.
2. **VGG Image Annotator (VIA)**: Open-source řešení pro anotaci obrazových dat, omezené možnosti práce se souborovými systémy a senzory.

### Odlišnosti a Výhody Naší Aplikace
- Moje aplikace bude pro zadavatele zadarmo (za kredity)
- Bude podporovat práci se souborovým systémem pro snadnou správu dat dle dnů a dalších parametrů.
- Integrace s AI pro okamžitou kontrolu anotací.

## Návrh Struktury Programu

### Architektura
- **Frontend**: Java Spring Boot pro dynamické generování HTML stránek.
- **Backend**: Java Spring Boot REST API pro komunikaci s frontendem a správu dat.

### Moduly
1. **Uživatelský Modul**: Registrace, přihlášení, správa uživatelských účtů.
2. **Data Management Modul**: Načítání, ukládání, procházení a správa souborů a složek.
3. **Annotation Modul**: Nástroje pro anotaci dat.
4. **Export Modul**: Export anotovaných dat pro AI trénink.
5. **AI Interaction Modul**: Integrace s AI pro testování anotací.

### Knihovny a Frameworky
- Spring Boot
- Spring Webflux
- Bootstrap for CSS
- Spring Security (pro autentizaci a autorizaci)
- Spring Data JPA (pro práci s databází)
- Python + FFMPEG pro načtení dat ze senzoru a kamery

### Integrace
- Externí API pro trénink a testování AI (pokud existuje).
- Souborový systém pro ukládání a načítání dat.
- Systém pro bezpečné skladování dat o uživatelích.

## Technologický Stack

### Operační Systém
- Aplikace bude běžet na jakémkoliv OS podporujícím Java pro spuštění samotného serveru.
- Jinak se lze k frontendu připojit z běžných browserů.

### Programovací Jazyk
- Java verze 21
- Python (pro samplovací skript) - nejnovější verze
- HTML + CSS + JS - frontend v Spring boot

### Vývojové Prostředí
- IntelliJ IDEA.
- VSCode (pro python)

### Další Využívané Zdroje
- MySQL nebo PostgreSQL jako relační databáze.
- Maven pro správu závislostí a build management.
- Git pro verzování kódu.
