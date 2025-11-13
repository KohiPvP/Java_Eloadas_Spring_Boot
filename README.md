# Dokumentáció
## 1. feladat
### Választott sablon
- **Forrás:** [HTML5 UP](https://html5up.net/)
- **Téma neve:** **Future Imperfect**
- **Licenc:** Creative Commons Attribution 3.0 (CC BY 3.0) – forrásmegjelölés szükséges

---

### Miért ezt választottam?
- **Reszponzív kialakítás:** mobil–tablet–desktop nézetekre optimalizált
- **Modern design:** kontrasztos, könnyen testreszabható

## 3. feladat
### Jax-ws Runtime hozzáadása
- Mivel az alkalmazás **SOAP webszolgáltatással** kommunikál (az MNB árfolyam-szolgáltatás WSDL-jén keresztül), szükség van egy olyan könyvtárra, amely biztosítja a **JAX-WS (Java API for XML Web Services)** futtatókörnyezetet.
- Ehhez a **jaxws-rt** könyvtárat (JAX-WS Reference Implementation) kellett felvenni a **pom.xml** fájlba.
- Ez a függőség tartalmazza a szükséges **SOAP kliens osztályokat** és a **webszolgáltatás futtatási környezetet**, amelyek nélkül a következő hiba jelentkezne futásidőben: **ClassNotFoundException**
- A **jaxws-rt** tehát elengedhetetlen ahhoz, hogy a Spring Boot alkalmazás képes legyen a WSDL alapján generált SOAP kliensen keresztül kommunikálni az MNB webszolgáltatásával.
### A SoapController feladata
- meghívni az MNB SOAP webszolgáltatást
- feldolgozni a kapott XML választ
- majd az adatokat továbbítani Thymeleaf nézethez (HTML)
- Az osztály a Spring Boot keretrendszer **@Controller** annotációjával van ellátva, így a Spring felismeri, és automatikusan kezeli a beérkező kéréseket a megadott URL-eken.
### Annotációk és importok
- **@Controller** – jelzi, hogy az osztály egy webes vezérlő (Controller).
- **@GetMapping("/soap")** – a /soap URL-hez tartozó GET kéréseket kezeli.
- **@PostMapping("/soap")** – a /soap URL-hez tartozó POST kéréseket kezeli.
- **@ModelAttribute** – a HTML űrlapban megadott értékeket automatikusan összeköti a MessagePrice objektum mezőivel.
- _Model_ – a Spring által biztosított osztály, amelyen keresztül az adatok a HTML nézethez továbbíthatók.
- _XML feldolgozáshoz_: org.w3c.dom, javax.xml.parsers osztályok (DOM parser).
- _SOAP klienshez_: soapclient.MNBArfolyamServiceSoap, MNBArfolyamServiceSoapImpl.
### soap() - GET metódus
- Feladata: a kezdeti űrlap oldal (form.html) megjelenítése.
- A Model objektumba egy új MessagePrice példány kerül, így a Thymeleaf sablon (th:object="${param}") tud hivatkozni az adatokra.
- Visszatérési érték: A return "form"; utasítás azt jelenti, hogy a form.html sablont jelenítse meg.
### soap() - POST metódus
- Feldolgozza az űrlapon megadott adatokat (_currency_, _startDate_, _endDate_).
- Létrehozza a SOAP kliens objektumot.
- Meghívja az MNB webszolgáltatást.
- Ez a hívás egy XML-formátumú sztringet ad vissza, amely az adott deviza árfolyamait tartalmazza.
- Az XML-t feldolgozza a **parseMnbXml()** segédfüggvénnyel.
- A visszakapott **RatePoint** lista alapján előkészíti az adatokat a grafikonhoz: **labels** → dátumok listája, **series** → árfolyamértékek listája
- A modellbe átadja a Thymeleaf-nek szükséges adatokat.
- Visszatérési érték: A return "result"; utasítás a result.html sablon megjelenítését eredményezi, amely a grafikonos nézet.
### parseMnbXml() – XML feldolgozó függvény
- Feladata: a SOAP válaszban érkező XML szöveg feldolgozása és RatePoint objektumok listájává alakítása.
- **Lépései:**
- DOM parser létrehozása (DocumentBuilderFactory, DocumentBuilder).
- Az XML beolvasása Document objektumba.
- Minden <Day> elem feldolgozása:
- lekéri a dátumot (date attribútum),
- megkeresi a <Rate> elemeket,
- kiválasztja a megfelelő devizát (curr attribútum),
- átalakítja a tizedesvesszőt pontra,
- kezeli az unit attribútumot, ha van (pl. 100 JPY),
- létrehoz egy RatePoint példányt és hozzáadja a listához.
- Ezután a lista visszaadásra kerül a soap2() metódus felé, amely a Chart.js grafikonhoz használja.
### MessagePrice feladata
- A MessagePrice osztály a felhasználói űrlap által megadott paramétereket tárolja. Ez egy egyszerű adatmodell (**POJO** – Plain Old Java Object), amelyet a Spring automatikusan feltölt az űrlap mezőiből a **@ModelAttribute** segítségével.
|    Mező   |  Típus |                   Leírás                  |
|:---------:|:------:|:-----------------------------------------:|
| currency  | String | A kiválasztott deviza (pl. EUR, USD, GBP) |
| startDate | String | Az árfolyam lekérdezés kezdő dátuma       |
| endDate   | String | Az árfolyam lekérdezés záró dátuma        |
### RatePoint feladata
- A RatePoint osztály az MNB-től lekért egyedi árfolyamadatokat tárolja. Minden objektum egy nap és a hozzá tartozó árfolyam értékét képviseli. Ez az adatmodell szolgál alapul a grafikon kirajzolásához (Chart.js).
|   Mező  |    Típus   |                Leírás               |
|:-------:|:----------:|:-----------------------------------:|
| date    | LocalDate  | Az adott árfolyam napja             |
| rate    | BigDecimal | Az adott napon mért árfolyam értéke |
| endDate | String     | Az árfolyam lekérdezés záró dátuma  |
### Összegzés
-A három osztály együtt valósítja meg az alkalmazás logikáját:
-az űrlapról érkező adatok bekerülnek a **MessagePrice model**lbe,
-a SoapController a **SOAP webszolgáltatás**on keresztül lekéri az MNB-től az árfolyamokat,
-majd a feldolgozott adatokat **RatePoint objektum**okként átadja a Thymeleaf sablonnak, amely a **Chart.js** segítségével grafikont jelenít meg.
