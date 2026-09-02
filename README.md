# DrugDose — Applicazione Android Dermatologica
**Corso di Programmazione di Dispositivi Mobili — Università dell'Insubria**
Prof. Ignazio Gallo

---

## 🌟 Funzionalità Avanzate per la Consegna

Oltre all'architettura MVVM richiesta, il progetto implementa:
*   **Safe Dose Clamping**: Distinzione tra dose teorica calcolata e dose finale applicata dopo i limiti di sicurezza (min/max).
*   **Validazione Dinamica**: Il campo Età diventa obbligatorio o opzionale in tempo reale in base ai vincoli del farmaco selezionato.
*   **Parsing Flessibile**: Supporto all'inserimento di decimali sia con il **punto** che con la **virgola** per peso e altezza.
*   **Test Unitari**: Suite di test automatizzati per verificare la correttezza delle formule e dei limiti di sicurezza.

---

## 📂 Struttura del Progetto

```
DrugDose/
├── app/src/main/
│   ├── assets/drugs.json         ← Database farmaci (fonti: BNF, AIFA, WHO)
│   ├── java/com/drugdose/
│   │   ├── MainActivity.kt       ← Controller UI e validazione dinamica
│   │   ├── model/                ← Modelli dati (Drug, DoseResult)
│   │   ├── logic/                ← DoseCalculator (Formule e Clamping)
│   │   ├── data/                 ← Repository e parsing JSON
│   │   └── viewmodel/            ← ViewModel (Stato e validazione input)
│   └── res/layout/               ← Layout Material Design 3
└── app/src/test/                 ← Test unitari della logica di calcolo
```

---

## 🏗 Architettura: MVVM

L'app segue rigorosamente il pattern **Model-View-ViewModel**:
1.  **View**: `MainActivity` osserva i `LiveData` e aggiorna la UI.
2.  **ViewModel**: `DrugViewModel` gestisce la logica di input e lo stato della UI.
3.  **Model**: `Drug` e `DoseResult` definiscono la struttura dei dati.
4.  **Logic**: `DoseCalculator` è un componente a parte per garantire la testabilità della logica di calcolo.

---

## 📊 Formule e Sicurezza

| Tipo       | Formula / Logica                               | Sicurezza                          |
|------------|------------------------------------------------|------------------------------------|
| `per_kg`   | doseUnitaria × peso (kg)                       | Conversione automatica µg → mg     |
| `per_m2`   | BSA (Mosteller) × doseUnitaria                 | Richiede altezza obbligatoria      |
| `fissa`    | doseUnitaria costante                          | Gestione unità "applicazione"      |
| `fasce`    | Lookup tabella per range di peso               | Limite inferiore incluso, superiore escluso |

---

## 🧪 Test Unitari

Il progetto include test automatizzati in `DoseCalculatorTest.kt` per verificare:
*   Calcolo Ivermectina (70kg → 14mg teorici, 12mg finali).
*   Correttezza della formula BSA.
*   Validazione dell'età minima obbligatoria.
*   Gestione delle fasce di peso.

---

## 🚀 Setup e Avvio

1.  Aprire Android Studio → **Open** → selezionare la cartella `DrugDose/`.
2.  Attendere il **Gradle sync**.
3.  Eseguire i test: cliccare con il tasto destro su `app/src/test` e selezionare **Run 'Tests in...'**.
4.  Avviare l'app su emulatore o dispositivo (API ≥ 26).

---

## ⚕ Disclaimer
I dosaggi sono inseriti a scopo didattico da fonti pubbliche verificabili (RCP AIFA, BNF/NICE, WHO). Non sostituisce il giudizio clinico del medico né il foglio illustrativo ufficiale del farmaco.


## Nota sulla cronologia Git

Il repository Git è stato inizializzato il 29/08/2026. Il progetto era già stato
sviluppato precedentemente senza una cronologia Git, quindi i commit precedenti
non sono disponibili.

Per correttezza, le date dei commit non sono state modificate artificialmente.
La cronologia presente rappresenta l'organizzazione tecnica reale delle principali
componenti del progetto, registrata a partire dalla data di inizializzazione del
repository.
