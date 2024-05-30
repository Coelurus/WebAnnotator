# Specifikace Ročníkového projektu pro Webový anotátor pro data z kapacitního senzoru
#### Webová aplikace pro načítání, anotaci a správu dat ze senzorů, vyvinutá v Java Spring Boot. Umožňuje uživatelům načítat data, definovat a aplikovat anotace, exportovat anotovaná data pro AI trénink, a spravovat uživatelské účty. Integrace s AI umožňuje testování anotací.
#### Autor: Filip Vopálenský
#### Vedoucí práce: doc. RNDr. Petr Hnětynka, Ph.D.
#### Verze: 0.1.0.
#### Dne: 30.5.2024

## 1. Základní informace

### 1.1 Popis a zaměření softwarového díla
Mít možnost předávat strojům instrukce pomocí gest se vždy zdálo jako budoucnost, ale s rozvojem umělé inteligence se nereálné stává skutečností.
Avšak aby něco takového fungovalo, musí nejdříve někdo naši AI natrénovat. Takovéto úkony mají často na starosti lidé, jež nemají hlubší porozumění počítačům. \
Tento projekt si tak klade za cíl vytvořit pro uživatele srozumitelnou aplikaci, která uživateli usnadní již tak úmornou práci pochopitelným a přehledným ovládáním.

### 1.2 Použité technologie
- Java Spring Boot
- HTML, CSS, JavaScript (pro tvorbu frontendu)
- Bootstrap (pro CSS)
- MySQL nebo PostgreSQL (pro metadata)
- Git 
- Maven
- FFMPEG (pro samplování dat)
- Python (pro zautomatizování sběru dat)
- Python knihovny
  - OpenCV (pro natáčení videa)
  - pynput (pro ovládání myši)

### 1.3 Odkazy
- Dokumentace k Java Spring Boot:
  - https://spring.io/projects/spring-boot
  - © Pivotal Software, Inc., a VMware company
- Dokumentace k Pythonu: 
  - https://www.python.org/doc/
  - © Python Software Foundation
- Dokumentace k FFMPEG: 
  - https://ffmpeg.org/documentation.html
  - © FFmpeg Developers
- Dokumentace k Aurea GUI: 
  - https://ww1.microchip.com/downloads/aemDocuments/documents/OTH/ProductDocuments/UserGuides/Aurea-GUI-User-Guide-40001681E.pdf
  - © 2018 Microchip Technology Inc.

## 2. Stručný popis softwarového díla

### 2.1 Důvod vzniku softwarového díla a jeho základní části a cíle řešení
Aplikace je navržena pro zpracování a anotaci dat z kapacitního senzoru MGC3130, jelikož pro něj na trhu není obdoby. Tato anotovaná data se nadále využijí pro natrénování umělé inteligence pro rozpoznávání gest, čímž se ještě zvýší efektivita a využitelnost tohoto senzoru. \
Tento projekt je samospustitelnou a používatelnou aplikací, avšak využívá tež API zmíněné umělé inteligence, které posílá anotovaná data v dohodnutém formátu a od které si může sama nechat anotovat data. \
Program bude složen z několika  modulů:
1. **Uživatelský Modul**: Registrace, přihlášení, správa uživatelských účtů.
2. **Data Management Modul**: Načítání, ukládání, procházení a správa souborů a složek.
3. **Annotation Modul**: Nástroje pro anotaci dat.
4. **Export Modul**: Export anotovaných dat pro AI trénink.
5. **AI Interaction Modul**: Integrace s AI pro testování anotací.

### 2.2 Hlavní funkce
1. Načítání dat ze senzoru
2. Správa souborového systému
3. Definice anotování
4. Anotace dat
5. Export anotovaných dat
6. Testování anotace AI
7. Správa uživatelů

### 2.3 Motivační příklad užití
Uživatel načte data ze senzoru, definuje anotace, anotuje data, a následně exportuje anotovaná data pro trénink AI modelu.

### 2.4 Prostředí aplikace
Aplikace poběží na serveru s Java Spring Boot, přičemž uživatelské rozhraní bude přístupné přes webový prohlížeč. Bude potřeba databáze MySQL pro ukládání dat o uživatelích a metadat o logovaných datech.

### 2.5 Omezení díla
- Aplikace bude závislá na specifických verzích technologií (Java 21, Python, FFMPEG).
- Aplikace bude též závislá ná funkční umělé inteligenci komunikující dle stanovených procedur.
- Potřeba serverového prostředí pro provozování aplikace.

## 3. Vnější rozhraní

### 3.1 Uživatelské rozhraní, vstupy a výstupy
- Aplikace bude implementovat několik různých scén pro obhospodařování různých funkcí systému:
  - Formulář pro nahrávání dat a vytváření projektů.
  - Zobrazení struktury souborového systému s možností filtrování.
  - Editor pro anotování dat.
  - Formuláře pro spravování uživatelů a týmů.

### 3.2 Rozhraní s hardware
- Aplikace Aurea GUI
  - Využití funkce nahrávání snímaných dat a export tohoto logu do textového souboru.
- Podpora pro načítání dat ze senzoru a kamery pomocí Python skriptu a FFMPEG.
- Jelikož pro sběr dat jak ze senzoru tak kamery jsou používány externí aplikace je potřeba je synchronizovat.
  - K tomu se použije Python script a FFMPEG, který ve stejnou chvíli spustí a zastaví sběr dat a poté je nasamplují do formátu požadovaného aplikací.

### 3.3 Rozhraní se software
- Integrace s databází MySQL.
  - Databáze bude používána pro ukládání 2 následujících dat:
    - Informace o uživatelích
      - Jejich jména, hesla, oprávnění pro práci s databází a přiřazení k týmům.
    - Metadata pro nasamplovaná sesbíraná data
      - Samotná data budou kvůli velikosti uložena mimo databázi na disku.
      - Pro efektivní práci si o nich všech uložíme informace do databáze:
        - Název souboru
        - Umístění na disku
        - Příslušnost k určitému projektu
        - Čas vzniku
- Použití externích API pro trénink a testování AI.
  - ***Bude upřesněno***
    - V jakém formátu se budou anotovaná data posílat a v jakém se bude vracet odpověď

### 3.4 Komunikační rozhraní
- REST API pro komunikaci mezi frontendem a backendem.

## 4. Detailní popis funkcionality

### 4.1 Nahrání dat do systému
- Systém poskytuje formulář pro:
    - Název projektu (povinné)
    - Nahrání log souboru (povinné)
    - Nahrání snímků (povinné)
    - Výběru deadline
    - Výběr priority
    - Výběr týmu/zaměstnance, kterému je přiřazen
    - Přidání klíčových slov / štítků pro označení projektu
- Systém defaultně označí projekt jako nezačatý a neprochází testy (tzn. AI ho správně označila).
- Funkcionalitu může používat uživatel s právy admina.
- Takový uživatel vyplní ve formuláři minimálně povinná pole a formulář odešle.
- Systém validuje formulář a data.
  - V případě selhání upozorní uživatele a vyzve k opravení chyb.
  - V případě úspěchu systém uloží data a informuje uživatele o úspěšném nahrání dat.

### 4.2 Práce s projektem
- Systém zobrazí grafický editor s následujícími položkami:
  - Název projektu
  - Štítky
  - Deadline
  - Priorita
  - Seznam štítků 
  - Možnost vytvořit nový štítek
  - Možnost nastavení frekvence zobrazených snímků
  - Možnost uložení a opuštění projektu
  - Možnost poslat data AI na natrénování
  - Možnost spuštení AI na tato data
  - Grafické zobrazení logu:
    - Pro každý záznam (v určité frekvenci):
      - Okénko s pozicí ruky v prostoru (3d grafika)
      - Odpovídající snímek z videa
  - Systém ukládá změny průběžně
  - Uživatel v editoru může:
    - Tvořit / vybírat štítky 
    - Označovat / odznačovat snímky štítky
      - Výběr buď:
        - Po jednom
        - Označení více najednou
        - Označení všech
    - Krok zpět / krok vpřed
    - Nechat si vyznačit jen snímky označené určitým štítkem
    - Uložit změny
    - Změnit stav projektu
    - Opustit projekt
    - Poslat data AI na natrénování
    - Spustit AI na tato data
  - Při uložení změn se vygeneruje/přepíše JSON soubor, přiřazen ke konkrétnímu projektu v němž klíče budou jména štítků a hodnota pole intervalů v němž jsou snímky označeny tímto štítkem.

### 4.3 Správa souborového systému
- Systém zobrazí strukturu složek pro každý den.
    - Po kliknutí se zobrazí seznam projektů z tohoto dne.
    - Možnost filtrovat (pak se zobrazí odpovídající projekty a ne složky):
        - Dle intervalu původu záznamu
        - Dle přiřazení (mně/mému týmu)
        - Dle priority
        - Dle štítku 
        - Dle stavu projektu (nezačatý, rozpracovaný)
        - Dle průchodu testů
    - Možnost seřadit:
        - Dle deadline 
        - Dle priority
        - Dle dne vytvoření 
        - Dle délky záznamu
    - U každého souboru v souborovém systému je vidět stav rozpracování 
    - Složka pro každý den je označena: 
        - dokončena/nedokončena dle stavu projektů v ní
        - prochází/neprochází dle toho, jestli AI správně označila projekty v ní

### 4.4 Správa uživatelů a týmů
- Systém rozděluje uživatele na dvě skupiny dle oprávnění:
  - Skupina Admin
    - Mohou spravovat uživatele
        - Možnost vytvořit nového
          - Systém zobrazí formulář s položkami:
            - Jméno
            - Oprávnění
            - E-mail
            - Tým
          - Při odeslání systém verifikuje formulář:
            - Při neúspěchu: 
              - Vyzve uživatele k odstranění chyb
            - Při úspěchu:
              - Vygeneruje heslo pro uživatele
              - Uloží data o uživateli do databáze
              - Pošle e-mail novému uživateli s přihlašovacími údaji a výzvou ke změně hesla
              - Informuje uživatele, který nový účet vytvořil, o úspěchu
        - Možnost smazat uživatele
        - Možnost deaktivovat účet
        - Správa oprávnění
        - Přeřazení do jiného týmu
    - Mohou spravovat týmy
        - Možnost tvořit
            - Systém zobrazí formulář s položkami:
                - Název 
                - Vedoucí
                - Seznam uživatelů na přiřazení 
        - Možnost mazat týmy
            - Smaže se pouze značka týmu, ne samotné účty uživatelů
        - Možnost upravovat týmy 
            - Změna názvu 
            - Změna vedoucího 
            - Změna seznamu členů
    - Mohou nahrávat a mazat projekty
    - Mohou dělat to, co Zaměstnanci
  - Skupina Zaměstnanec
    - Může pracovat na projektech - tzn. anotovat je a měnit stav dokončení

### 4.5 Komunikace a práce s AI
- ***Bude upřesněno***
  - *Informace o API a komunikaci s externím AI systémem*
- Uživatel má v projektu může poslat AI testovací data na anotování
- Systém pak zobrazí výsledky AI na testovacích datech
  - Viditelně označí skupiny snímků, které:
    - AI anotovala správně
    - AI anotovala chybně
- Pokud AI označí data správně, projekt celý se označí jako že prochází testy
- Uživatel při průchodu souborovým systémem může též odeslat celou jednu složku na otestování AI
  - V tom případě se postupně posílají všechny dokončené projekty ze složky a testují, zda je AI označí správně


## 5. Obrazovky
- Hlavní obrazovka pro přihlášení
- Dashboard tlačítek/odkazů na jiné obrazovky v závislosti na oprávnění uživatele:
  - Zaměstnanec:
    - Návrat k poslednímu projektu
    - Zobrazení všech projektů (tzn. souborového systému)
    - Zobrazení profilu
  - Admin:
    - Nahrání nového projektu
    - Správa uživatelů
    - Správa týmů
    - Vše co má Zaměstnanec

## 6. Ostatní (mimofunkční) požadavky

### 6.1 Požadavky na výkon
- Server musí být schopen uložit a pracovat s velkým množstvím obrázků, jelikož pro každý projekt bude spousta snímků ruky snímané senzorem.

### 6.2 Požadavky na bezpečnost využívání aplikace
- Ochrana dat před neoprávněným přístupem.
- Autentizace a autorizace uživatelů.

### 6.3 Požadavky na rozšířitelnost a začlenitelnost
- Možnost přidání podpory práce s daty z jiného senzoru bez nutnosti zásadních změn v kódu.

## 7. Negativní vymezení
- Tento projekt neobsahuje implementaci AI.
- Nebude poskytovat nástroje pro vizualizaci výsledků AI tréninku mimo základní kontrolu anotací.

## 8. Časový plán a milníky
| Datum      | Milník                                                     | Způsob prezentace              |
| ---------- | ---------------------------------------------------------- | ------------------------------ |
| 31.5.2024  | Finální verze specifikace                                  | Existující dokument na GitLabu |
| 30.6.2024  | Skript na získání a samplování dat + architektura programu | Osobní předvedení              |
| 4.8.2024   | Základní funkce souborového systému                        | Osobní předvedení              |
| 1.9.2024   | Základní funkce anotování                                  | Osobní předvedení              |
| 30.9.2024  | Alfa verze                                                 | Osobní předvedení              |
| 30.10.2024 | Správa uživatel + dokončení souborového systému            | Osobní předvedení              |
| 30.11.2024 | Další funkce anotování                                     | Osobní předvedení              |
| 30.12.2024 | Beta verze                                                 | Osobní předvedení              |

*Všehny milníky s osobním předvedením budou předcházeny commitem do tohoto repozitáře.*
## Dodatek A: Vymezení pojmů
- Označením `projekt` v kontextu anotování dat je myšlen jeden (několikavteřinový) záznam ze senzoru a kamery, který vznikl mezi spuštěním snímání a zastavením snímání a jejich následným nasamplováním. Prací na projektu se pak myslí přídávání anotací jednotlivým snímkům takto vzniklému záznamu/projektu.

## Dodatek B: Seznam detailů k upřesnění
- Je nutno upřesnit komunikační rozhraní s AI. Jak a kam data posílat, v jakém formátu je posílat a co jsou očekávané odpovědi. 
  - viz [3.3](#33-rozhraní-se-software) a [4.5](#45-komunikace-a-práce-s-ai)
  
## 9. Poznámky
- Inspirace z existujících řešení jako Labelbox a VGG Image Annotator.
- Tato dokumentace byla inspirována šablonou:
  - https://d3s.mff.cuni.cz/files/teaching/nprg045/SablonaSpecifikace.pdf
