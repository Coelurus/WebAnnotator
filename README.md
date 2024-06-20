# Specifikace Ročníkového projektu pro Webový anotátor pro data z kapacitního senzoru
#### Webová aplikace pro načítání, anotaci a správu dat ze senzorů, vyvinutá v Java Spring Boot. Umožňuje uživatelům načítat data, definovat a aplikovat anotace, exportovat anotovaná data pro AI trénink, a spravovat uživatelské účty. Integrace s AI umožňuje testování anotací.
#### Autor: Filip Vopálenský
#### Vedoucí práce: doc. RNDr. Petr Hnětynka, Ph.D.
#### Verze: 1.0.0
#### Dne: 14.6.2024

## Tabulka revizí

| Jméno            | Datum     | Důvod změny                     | Verze |
| ---------------- | --------- | ------------------------------- | ----- |
| Filip Vopálenský | 30.5.2024 | Počáteční text                  | 0.1.0 |
| Filip Vopálenský | 14.6.2024 | Upřesnění detailů funkcionality | 1.0.0 |

## 1. Základní informace

### 1.1 Popis a zaměření softwarového díla
Mít možnost předávat strojům instrukce pomocí gest se vždy zdálo jako budoucnost, ale s rozvojem umělé inteligence se nereálné stává skutečností.
Avšak aby něco takového fungovalo, musí nejdříve někdo naši AI natrénovat. Takovéto úkony mají často na starosti lidé, jež nemají hlubší porozumění počítačům. \
Tento projekt si tak klade za cíl vytvořit pro uživatele srozumitelnou aplikaci, která uživateli usnadní již tak úmornou práci pochopitelným a přehledným ovládáním.

### 1.2 Použité technologie
- Java Spring Boot
- HTML, CSS, JavaScript (pro tvorbu frontendu)
- Bootstrap (pro CSS)
- MySQL (pro metadata)
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
1. **Požadavky a předpoklady**
   1. Uživatel má platný účet.
   2. Na serveru běží aplikace a je dostupná přes webový prohlížeč.
   3. Uživatel má právo na načítání a anotování dat.
        - Pro krok *2. Vytvoření nového projektu:* je třeba uživatele s právy admina.
   4. AI systém je dostupný a správně nakonfigurovaný pro komunikaci s aplikací.

2. **Hlavní tok**
   1. **Přihlášení do systému:**
      - Uživatel otevře webový prohlížeč a přejde na přihlašovací stránku aplikace.
      - Zadá své přihlašovací údaje (uživatelské jméno a heslo) a klikne na tlačítko "Přihlásit se".
      - Systém ověří přihlašovací údaje a v případě úspěchu uživatele přesměruje na hlavní dashboard.
     
   2. **Vytvoření nového projektu:**
      - Uživatel na hlavním dashboardu klikne na tlačítko "Nový projekt".
      - Systém zobrazí formulář pro vytvoření projektu.
      - Uživatel vyplní povinná pole formuláře:
        - Název projektu
        - Nahrání log souboru ze senzoru
        - Nahrání snímků z kamery
      - Uživatel může volitelně vyplnit další pole:
        - Deadline
        - Priorita
        - Přiřazení k týmu nebo zaměstnanci
        - Klíčová slova / štítky
      - Uživatel klikne na tlačítko "Vytvořit projekt".
      - Systém validuje formulář a data, v případě úspěchu uloží data a informuje uživatele o úspěšném vytvoření projektu.

   3. **Anotace dat:**
      - Uživatel vybere nově vytvořený projekt z seznamu projektů.
      - Systém zobrazí grafický editor pro anotaci dat.
      - Uživatel může v editoru provádět následující akce:
        - Procházet jednotlivé snímky v logu.
        - Označovat snímky různými štítky.
        - Vytvářet nové štítky.
        - Upravovat existující anotace.
        - Ukládat průběžné změny.
      - Uživatel může kdykoliv kliknout na tlačítko "Uložit a opustit" pro uložení změn a návrat na hlavní dashboard.

   4. **Učení AI:**
      - Uživatel může kliknout na tlačítko "Naučit AI".
      - Systém pošle anotovaná data AI systému, aby se na nich naučil vztahy mezi anotacemi a daty.

   5. **Testování anotací AI:**
      - Uživatel může kliknout na tlačítko "Otestovat s AI".
      - Systém pošle anotovaná data AI systému a obdrží výsledky testu.
      - Systém zobrazí výsledky testu, kde zvýrazní správně a nesprávně anotované snímky.
      - V případě, že AI anotovala data správně, systém označí projekt jako "Prochází testy".


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
  - Data budou uložena ve formátu JSON.
  - Jeden JSON soubor vždy reprezentuje jeden projekt.
  - Soubor reprezentuje slovník:
    - Klíče jsou časové známky, pro které senzor provedl měření.
    - Hodnota je slovník sestávající z dat dvou typů:
      - Klíče a hodnoty převzaté z měření senzorem
        - Klíče jsou následující:
          - Running
          - fTx
          - Pos x
          - Pos y
          - Pos z
          - CIC S
          - CIC W
          - CIC N
          - CIC E
          - CIC C
          - SD S
          - SD W
          - SD N
          - SD E
          - SD C
          - Touch
          - Tap
          - DblTap
          - AirWheel
          - Gesture
        - Hodnota je buď `String` či null
      - Uložená data o anotacích
        - Klíč je `"anotace"`
        - Hodnota je pole Stringů se jmény anotací přidělených danému měření

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
  - Grafický editor pro anotaci:
    ![Anotace GUI](annotate-ui.jpg)

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
