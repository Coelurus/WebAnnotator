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
- **Frontend**: Java Spring Boot s využitím Thymeleaf pro dynamické generování HTML stránek.
- **Backend**: Java Spring Boot REST API pro komunikaci s frontendem a správu dat.

### Moduly
1. **Uživatelský Modul**: Registrace, přihlášení, správa uživatelských účtů.
2. **Data Management Modul**: Načítání, ukládání, procházení a správa souborů a složek.
3. **Annotation Modul**: Nástroje pro anotaci dat.
4. **Export Modul**: Export anotovaných dat pro AI trénink.
5. **AI Interaction Modul**: Integrace s AI pro testování anotací.

### Knihovny a Frameworky
- Spring Boot
- Spring Security (pro autentizaci a autorizaci)
- Spring Data JPA (pro práci s databází)
- Webjars (pro frontend)
- Python + FFMPEG pro načtení dat ze senzoru a kamery

### Integrace
- Externí API pro trénink a testování AI (pokud existuje).
- Souborový systém pro ukládání a načítání dat.

## Technologický Stack

### Operační Systém
- Aplikace bude běžet na jakémkoliv OS podporujícím Java pro spuštění samotného serveru.
- Jinak se lze k frontendu připojit kdekoliv s přístupem o půlnoci

### Programovací Jazyk
- Java verze 21

### Vývojové Prostředí
- IntelliJ IDEA.

### Další Využívané Zdroje
- MySQL nebo PostgreSQL jako relační databáze.
- Maven pro správu závislostí a build management.
- Git pro verzování kódu.
